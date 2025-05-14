package com.fisa.pg.service;

import com.fisa.pg.dto.BaseResponse;
import com.fisa.pg.entity.card.BinInfo;
import com.fisa.pg.entity.card.UserCard;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.feign.client.AppCardClient;
import com.fisa.pg.feign.client.CardClient;
import com.fisa.pg.feign.client.WonQOrderClient;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.feign.dto.appcard.request.AppCardPaymentResultDto;
import com.fisa.pg.feign.dto.appcard.response.AppCardAuthResponseDto;
import com.fisa.pg.feign.dto.wonq.request.*;
import com.fisa.pg.feign.dto.wonq.response.PaymentCreateResponseDto;
import com.fisa.pg.feign.dto.appcard.request.AppCardPaymentAuthResultDto;
import com.fisa.pg.feign.dto.card.request.CardPaymentApprovalRequestDto;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import com.fisa.pg.feign.dto.wonq.response.PaymentMethodUpdateResponseDto;
import com.fisa.pg.feign.dto.wonq.response.PaymentVerificationResponseDto;
import com.fisa.pg.repository.BinInfoRepository;
import com.fisa.pg.repository.PaymentRepository;
import com.fisa.pg.repository.TransactionRepository;
import com.fisa.pg.repository.UserCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static com.fisa.pg.entity.transaction.TransactionStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CardClient cardClient;

    private final AppCardClient appCardClient;

    private final WonQOrderClient wonQOrderClient;

    private final MerchantService merchantService;

    private final PaymentRepository paymentRepository;

    private final TransactionRepository transactionRepository;

    private final BinInfoRepository binInfoRepository;

    private final UserCardRepository userCardRepository;


    /**
     * 결제 요청 처리 메서드
     * 결제흐름 5번, 6번, 7번에 해당
     * @param request 결제 요청 정보
     * @return 결제 처리 결과
     * @throws IllegalStateException 중복 결제인 경우
     */
    @Transactional
    public PaymentCreateResponseDto createPayment(PaymentCreateRequestDto request) {
        // 결제 흐름 5번에 해당
        // 0. 가맹점 유효성 검사
        Merchant merchant = merchantService.validateActiveMerchant(request.getMerchantId());

        // 1. 중복 결제 확인
        paymentRepository.findByOrderIdAndMerchantId(request.getOrderId(), request.getMerchantId())
                .ifPresent(p -> {
                    throw new IllegalStateException("이미 존재하는 결제입니다.");
                });

        // 2. Payment 생성
        // 결제 흐름 6번에 해당
        Payment payment = Payment.from(request, merchant);
        paymentRepository.save(payment);

        // 3. Transaction 생성
        // 결제 흐름 6번에 해당
        Transaction transaction = Transaction.from(payment, merchant, request);
        transactionRepository.save(transaction);

        // 4. 초기 응답 반환
        // 결제 흐름 7번에 해당
        return PaymentCreateResponseDto.from(payment, request.getMerchantId());
    }

    /**
     * 결제 수단 선택 및 결제 UI URL 생성
     * <br/>
     * 이 메서드는 결제 흐름 중 <b>10-12번째 단계</b>를 처리합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 결제 수단 업데이트 요청 DTO
     * @return 결제 UI로 리다이렉트할 URL 정보
     * @throws IllegalArgumentException 결제를 찾을 수 없거나 지원하지 않는 결제 수단인 경우
     * @throws IllegalStateException 트랜잭션이 존재하지 않는 경우
     */
    @Transactional
    public PaymentMethodUpdateResponseDto updatePaymentMethod(PaymentMethodUpdateRequestDto request) {
        log.info("결제 수단 선택 요청: paymentId={}, method={}", request.getPaymentId(), request.getMethod());

        // 결제 정보 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결제를 찾을 수 없습니다: " + request.getPaymentId()));

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByPaymentAndTransactionStatus(
                payment, TransactionStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("처리 가능한 트랜잭션이 존재하지 않습니다."));

        String method = request.getMethod();

        // 지원하는 결제 수단 검증
        if (!"WOORI_APP_CARD".equals(method)) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
        }

        // 결제 수단 및 상태 업데이트 (11단계)
        payment.updatePaymentMethod(method);
        transaction.updateMethod(method);

        log.info("결제 수단 {}로 업데이트 완료: paymentId={}, txnId={}",
                 method, payment.getId(), transaction.getTxnId());

        // 결제 UI URL 생성 (11단계)
        String redirectUrl = generatePaymentRedirectUrl(payment, method);

        // 응답 생성 및 반환 (12단계)
        return PaymentMethodUpdateResponseDto.builder()
                .redirectUrl(redirectUrl)
                .build();
    }

    /**
     * 결제 수단에 따른 결제 페이지 URL 생성
     *
     * @param payment 결제 정보
     * @param method 결제 수단
     * @return 리다이렉트 URL
     */
    private String generatePaymentRedirectUrl(Payment payment, String method) {
        if ("WOORI_APP_CARD".equals(method)) {
            return "/api/payment/ui/wooricard/" + payment.getId();
        }

        throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
    }

    /**
     * 앱카드 결제 인증 요청 처리
     * <br/>
     * 이 메서드는 결제 흐름 중 <b>16-19번째 단계</b>를 처리합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param request 앱카드 인증 요청 정보
     * @return 앱카드 실행을 위한 딥링크
     */
    @Transactional
    public String requestAppCardAuth(AppCardAuthRequestDto request) {
        log.info("앱카드 인증 요청 시작: txnId={}, amount={}", request.getTxnId(), request.getAmount());

        try {
            // 앱카드 서버와 통신하여 인증 딥링크 요청 (16단계)
            ResponseEntity<AppCardAuthResponseDto> response = appCardClient.requestAppCardAuth(request);

            // 응답 검증 및 딥링크 추출 (19단계)
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("앱카드 서버 통신 실패");
            }

            String deepLink = response.getBody().getDeepLink();
            log.info("앱카드 인증 딥링크 수신 완료: txnId={}, deepLink={}", request.getTxnId(), deepLink);

            // 원큐 서버에 딥링크 정보 전달 (20단계)
            Transaction transaction = transactionRepository.findByTxnId(request.getTxnId())
                    .orElseThrow(() -> new RuntimeException("트랜잭션을 찾을 수 없습니다: " + request.getTxnId()));

            DeepLinkPaymentRequestDto deepLinkRequest = DeepLinkPaymentRequestDto.builder()
                    .orderId(request.getOrderId())
                    .paymentId(transaction.getPayment().getId().toString())
                    .deepLink(deepLink)
                    .build();

            // 원큐 오더 서버에 딥링크 전송
            wonQOrderClient.sendDeepLink(deepLinkRequest);
            log.info("딥링크를 원큐 오더 서버에 전송 완료: txnId={}", request.getTxnId());

            return deepLink;
        } catch (Exception e) {
            log.error("앱카드 인증 요청 처리 중 오류 발생: txnId={}, error={}",
                      request.getTxnId(), e.getMessage(), e);
            throw new RuntimeException("앱카드 인증 요청 처리 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 앱카드 인증 결과를 처리하고 카드사 승인을 진행합니다.
     * <br/>
     * 이 메서드는 결제 흐름 중 <b>32-37번째 단계</b>를 처리합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param authResult 앱카드 인증 결과 DTO
     * @return 인증 실패 시 AppCardPaymentAuthFailDto, 성공 시 CardPaymentApprovalResponseDto
     */
    @Transactional
    public Object processPaymentApproval(AppCardPaymentAuthResultDto authResult) {
        log.info("앱카드 인증 결과 처리 시작: txnId={}", authResult.getTxnId());

        String txnId = authResult.getTxnId();

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByTxnId(txnId)
                .orElseThrow(() -> new IllegalStateException("트랜잭션을 찾을 수 없습니다: " + txnId));


        // 인증 실패 처리 (33-34 단계)
        if (!authResult.isAuthenticated()) {
            log.warn("앱카드 인증 실패: txnId={}", authResult.getTxnId());

            // 인증 실패 시 결제 및 트랜잭션 상태 업데이트
            updatePaymentStatus(authResult.getTxnId(), PaymentStatus.FAILED);
            updateTransactionStatus(authResult.getTxnId(), TransactionStatus.AUTH_FAILED);

            return AppCardPaymentAuthFailDto.from(authResult);
        }

        // 트랜잭션에서 카드 승인 정보 가져오기
        Transaction.CardApprovalInfo approvalInfo = transaction.getCardApprovalInfo();


        // 1. BIN 정보 확인하여 카드 발급사 확인
        String cardNumber = authResult.getCardNumber();
        String bin = cardNumber.substring(0, 6);

        BinInfo binInfo = binInfoRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 카드 번호입니다: " + cardNumber));

       // 우리카드 확인
       if (!"WOORI".equals(binInfo.getIssuer())) {
           log.error("지원하지 않는 카드사입니다: {}", binInfo.getIssuer());
           updatePaymentStatus(authResult.getTxnId(), PaymentStatus.FAILED);
           updateTransactionStatus(authResult.getTxnId(), FAILED);
           return CardIssuerValidationFailDto.unsupportedIssuer(
                   authResult.getTxnId(),
                   binInfo.getIssuer()
           );
       }

        // 2. 사용자 카드 정보 저장
        UserCard userCard = UserCard.from(binInfo, cardNumber);

        userCardRepository.save(userCard);
        log.info("사용자 카드 정보 저장 완료: 카드 타입: {}", binInfo.getCardType());

        // 3. 카드사 승인 요청 생성 (33단계)
        CardPaymentApprovalRequestDto cardApprovalRequest = CardPaymentApprovalRequestDto.from(
                txnId,
                transaction.getAmount(),
                transaction.getMerchant().getSettlementAccountNumber(),
                cardNumber
        );


        // 카드사에 승인 요청 (33번째 단계)
        CardPaymentApprovalResponseDto approvalResponse = cardClient.requestCardApproval(cardApprovalRequest);

        log.info("트랜잭션 ID: {}, 상태: {}에 대한 카드사 승인 응답 수신됨", authResult.getTxnId(), approvalResponse.getPaymentStatus());

        // 카드사 응답에 따라 결제/트랜잭션 상태 업데이트
        updatePaymentStatus(authResult.getTxnId(), approvalResponse.getPaymentStatus());
        updateTransactionStatus(authResult.getTxnId(), approvalResponse.getPaymentStatus() == PaymentStatus.SUCCEEDED ? APPROVED : FAILED);

        // 결제 성공 시 원큐 오더 서버에 결제 결과 전송 (38단계)
        if (approvalResponse.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
            notifyPaymentResult(authResult.getTxnId(), authResult.getCardNumber(), LocalDateTime.now());
        }

        return approvalResponse;
    }

    /**
     * 결제 승인 결과를 원큐 오더 서버에 전송
     * 결제 흐름 중 38번째 단계를 처리
     *
     * @param txnId 트랜잭션 ID
     * @param cardNumber 카드 번호
     * @param approvedAt 승인 시각
     */
    @Transactional
    public void notifyPaymentResult(String txnId, String cardNumber, LocalDateTime approvedAt) {
        log.info("결제 결과 전송 시작: txnId={}", txnId);

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByTxnId(txnId)
                .orElseThrow(() -> new IllegalArgumentException("트랜잭션을 찾을 수 없습니다: " + txnId));

        Payment payment = transaction.getPayment();

        // 결제 결과 DTO 생성
        PaymentResultNotificationDto resultDto = PaymentResultNotificationDto.from(
                payment.getId(),
                payment.getOrderId(),
                txnId,
                payment.getAmount(),
                payment.getPaymentStatus(),
                cardNumber,
                approvedAt.toString()
        );

        try {
            // 원큐 오더 서버에 결제 결과 전송
            ResponseEntity<BaseResponse<Void>> response = wonQOrderClient.sendPaymentResult(resultDto);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isSuccess()) {
                log.info("결제 결과 전송 성공: txnId={}", txnId);
            } else {
                log.warn("결제 결과 전송 실패: txnId={}, 응답={}",
                        txnId, response.getBody() != null ? response.getBody().getCode() : "응답 없음");
            }
        } catch (Exception e) {
            log.error("결제 결과 전송 중 오류 발생: txnId={}, 오류={}", txnId, e.getMessage(), e);
        }
    }

    /**
     * 앱카드 서버에 결제 결과 전송
     *
     * @param txnId 트랜잭션 ID
     * @param paymentStatus 결제 상태
     */
    private void notifyAppCardServer(String txnId, PaymentStatus paymentStatus) {
        log.info("앱카드 서버에 결제 결과 전송 시작: txnId={}, status={}", txnId, paymentStatus);

        try {
            // 앱카드 서버에 결제 결과 전송
            AppCardPaymentResultDto resultDto = AppCardPaymentResultDto.from(txnId, paymentStatus);
            ResponseEntity<BaseResponse<Void>> response = appCardClient.sendPaymentResult(resultDto);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isSuccess()) {
                log.info("앱카드 서버에 결제 결과 전송 성공: txnId={}", txnId);
            } else {
                log.warn("앱카드 서버에 결제 결과 전송 실패: txnId={}, 응답={}",
                        txnId, response.getBody() != null ? response.getBody().getCode() : "응답 없음");
            }
        } catch (Exception e) {
            log.error("앱카드 서버에 결제 결과 전송 중 오류 발생: txnId={}, 오류={}", txnId, e.getMessage(), e);
        }
    }

    /**
     * 결제 검증 요청을 처리
     * 결제 흐름 중 44-46번째 단계를 처리
     *
     * @param request 결제 검증 요청 DTO
     * @return 결제 검증 응답 DTO
     */
    public PaymentVerificationResponseDto verifyPayment(PaymentVerificationRequestDto request) {
        log.info("결제 검증 처리 시작: paymentId={}", request.getPaymentId());

        // 결제 정보 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다: " + request.getPaymentId()));

        // 검증 로직 수행 (45단계)
        boolean isOrderIdMatch = payment.getOrderId().equals(request.getOrderId());
        boolean isMerchantMatch = payment.getMerchant().getId().equals(request.getMerchantId());
        boolean isPaymentSucceeded = payment.getPaymentStatus() == PaymentStatus.SUCCEEDED;

        boolean verified = isOrderIdMatch && isMerchantMatch && isPaymentSucceeded;
        String message = verified ? "검증 성공" : "검증 실패";

        if (!isOrderIdMatch) {
            message = "주문 ID가 일치하지 않습니다";
        } else if (!isMerchantMatch) {
            message = "가맹점 정보가 일치하지 않습니다";
        } else if (!isPaymentSucceeded) {
            message = "결제가 성공 상태가 아닙니다: " + payment.getPaymentStatus();
        }

        log.info("결제 검증 결과: paymentId={}, 검증여부={}, 메시지={}",
                 payment.getId(), verified, message);

        // 응답 생성 및 반환 (46단계)
        return PaymentVerificationResponseDto.from(payment, verified, message);
    }


    /**
     * 트랜잭션 ID로 결제 상태 업데이트
     *
     * @param txnId  트랜잭션 ID
     * @param status 업데이트할 결제 상태
     */
    private void updatePaymentStatus(String txnId, PaymentStatus status) {
        log.info("트랜잭션 ID: {}에 대한 결제 상태를 {}로 업데이트", txnId, status);
        paymentRepository.updateStatusByTransactionId(txnId, status);
    }

    /**
     * 트랜잭션 ID로 트랜잭션 상태 업데이트
     *
     * @param txnId  트랜잭션 ID
     * @param status 업데이트할 트랜잭션 상태
     */
    private void updateTransactionStatus(String txnId, TransactionStatus status) {
        log.info("트랜잭션 ID: {}에 대한 트랜잭션 상태를 {}로 업데이트", txnId, status);
        transactionRepository.findByTxnId(txnId)
                .ifPresent(transaction -> {
                    transaction.updateStatus(status);
                    transactionRepository.save(transaction);
                });
    }
}
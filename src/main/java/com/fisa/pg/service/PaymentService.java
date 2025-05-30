package com.fisa.pg.service;

import com.fisa.pg.dto.request.AppCardPaymentRequestDto;
import com.fisa.pg.dto.request.PaymentCreateRequestDto;
import com.fisa.pg.dto.request.PaymentMethodUpdateRequestDto;
import com.fisa.pg.dto.response.PaymentCreateResponseDto;
import com.fisa.pg.dto.response.PaymentDto;
import com.fisa.pg.dto.response.PaymentMethodUpdateResponseDto;
import com.fisa.pg.entity.card.BinInfo;
import com.fisa.pg.entity.card.UserCard;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentMethod;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionLog;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.AppCardAuthenticationFailedException;
import com.fisa.pg.exception.PaymentDuplicateException;
import com.fisa.pg.exception.UnsupportedIssuerException;
import com.fisa.pg.feign.client.AppCardClient;
import com.fisa.pg.feign.client.CardClient;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.feign.dto.appcard.response.AppCardAuthResponseDto;
import com.fisa.pg.feign.dto.card.request.CardPaymentApprovalRequestDto;
import com.fisa.pg.feign.dto.card.response.BaseResponse;
import com.fisa.pg.feign.dto.card.response.CardPaymentApprovalResponseDto;
import com.fisa.pg.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AppCardClient appCardClient;

    private final CardClient cardClient;

    private final BinInfoRepository binInfoRepository;

    private final PaymentRepository paymentRepository;

    private final TransactionRepository transactionRepository;

    private final UserCardRepository userCardRepository;

    private final TransactionLogRepository transactionLogRepository;

    private static final SecureRandom random = new SecureRandom();

    /**
     * 결제 생성 요청 처리 메서드
     * <p>
     * 결제흐름의 5,6,7번 단계에 해당하는 처리를 수행합니다.
     *
     * @param request  결제 생성 요청 정보
     * @param merchant 인증된 상점 정보
     * @return 결제 생성 요청 처리 결과
     */
    @Transactional
    public PaymentCreateResponseDto createPayment(PaymentCreateRequestDto request, Merchant merchant) {

        log.info("요청 받은 currency = {}", request.getCurrency());

        // 1. 중복 결제 확인
        paymentRepository.findByOrderIdAndMerchant(request.getOrderId(), merchant)
                .ifPresent(p -> {
                    throw new PaymentDuplicateException(request.getOrderId(), merchant.getId().toString());
                });

        // 2. Payment 생성
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .merchant(merchant)
                .currency(request.getCurrency())
                .paymentMethod(PaymentMethod.APP_CARD)
                .paymentStatus(PaymentStatus.CREATED)
                .build();

        paymentRepository.save(payment);
        String txnId = "txnId" + String.format("%010d", random.nextLong(1_000_000_000L));

        // 3. Transaction 생성
        Transaction transaction = Transaction.builder()
                .transactionId(txnId)
                .payment(payment)
                .merchant(merchant)
                .amount(request.getAmount())
                .method(PaymentMethod.APP_CARD)
                .requestedAt(LocalDateTime.now())
                .currency(request.getCurrency())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        // 4. 트랜잭션 로그 생성
        TransactionLog logEntry = TransactionLog.builder()
                .transaction(transaction)
                .message("결제 생성 완료")
                .status(transaction.getTransactionStatus())
                .merchant(merchant)
                .createdAt(LocalDateTime.now())
                .build();
        transactionLogRepository.save(logEntry);

        // 5. 응답 반환
        return PaymentCreateResponseDto.from(payment, merchant);
    }

    /**
     * 결제 수단 선택 및 결제 UI URL 생성하는 메서드
     * <p>
     * 결제흐름의 10, 11, 12번 단계에 해당하는 처리를 수행합니다.
     *
     * @param request 결제 수단 업데이트 요청 DTO
     * @return 결제 UI로 리다이렉트할 URL 정보
     * @throws IllegalArgumentException 결제를 찾을 수 없거나 지원하지 않는 결제 수단인 경우
     * @throws IllegalStateException    트랜잭션이 존재하지 않는 경우
     */
    @Transactional
    public PaymentMethodUpdateResponseDto updatePaymentMethod(PaymentMethodUpdateRequestDto request) {
        log.info("결제 수단 선택 요청: paymentId={}, method={}", request.getPaymentId(), request.getMethod());

        // 결제 정보 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결제를 찾을 수 없습니다: " + request.getPaymentId()));

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByPaymentAndTransactionStatus(payment, TransactionStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("처리 가능한 트랜잭션이 존재하지 않습니다."));

        String method = request.getMethod();

        // 지원하는 결제 수단 검증
        if (!"WOORI_APP_CARD".equals(method)) {
            throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
        }

        // 결제 수단 및 상태 업데이트 (11단계)
        payment.updatePaymentMethod(method);
        transaction.updatePaymentMethod(method);

        log.info("결제 수단 {}로 업데이트 완료: paymentId={}, txnId={}", method, payment.getId(), transaction.getTransactionId());

        TransactionLog logEntry = TransactionLog.builder()
                .transaction(transaction)
                .message("결제 수단 업데이트: " + method)
                .status(transaction.getTransactionStatus())
                .merchant(payment.getMerchant())
                .createdAt(LocalDateTime.now())
                .build();
        transactionLogRepository.save(logEntry);

        // 결제 UI URL 생성 (11단계)
        String redirectUrl = generatePaymentRedirectUrl(payment, method);

        // 응답 생성 및 반환 (12단계)
        return PaymentMethodUpdateResponseDto.builder()
                .redirectUrl(redirectUrl)
                .build();
    }

    /**
     * 결제 수단에 따른 결제 페이지 URL 생성하는 메서드
     *
     * @param payment 결제 정보
     * @param method  결제 수단
     * @return 리다이렉트 URL
     */
    private String generatePaymentRedirectUrl(Payment payment, String method) {
        if ("WOORI_APP_CARD".equals(method)) {
            return "/payment/ui/wooricard/" + payment.getId();
        }

        throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
    }

    /**
     * 앱카드 결제 인증 요청 처리하는 메서드
     * <p>
     * 결제흐름의 16, 17, 18, 19번 단계에 해당하는 처리를 수행합니다.
     *
     * @param request 앱카드 인증 요청 정보
     */
    @Transactional
    public String requestDeeplink(AppCardAuthRequestDto request) {
        log.info("앱카드 인증 요청 시작: txnId={}, amount={}", request.getTxnId(), request.getAmount());


        try {
            // 앱카드 서버와 통신하여 인증 딥링크 요청 (16단계)
            AppCardAuthResponseDto response = appCardClient.requestAuth(request);
            log.info("딥링크 ={}", response.getDeepLink());

            // 응답 검증 및 딥링크 추출 (19단계)
            String deepLink = response.getDeepLink();
            log.info("앱카드 인증 딥링크 수신 완료: txnId={}, deepLink={}", request.getTxnId(), deepLink);

            return deepLink;


        } catch (Exception e) {
            log.error("앱카드 인증 요청 처리 중 오류 발생: txnId={}, error={}", request.getTxnId(), e.getMessage(), e);
            throw new RuntimeException("앱카드 인증 요청 처리 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 앱카드 인증 결과를 처리하고 카드사 승인을 진행하는 메서드
     * <br/>
     * 이 메서드는 결제 흐름 중 <b>32-38번째 단계</b>를 처리합니다.
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param authResult 앱카드 인증 결과 DTO
     * @return 인증 실패 시 AppCardPaymentAuthFailDto, 성공 시 CardPaymentApprovalResponseDto
     */
    @Transactional
    public CardPaymentApprovalResponseDto processPaymentApproval(AppCardPaymentRequestDto authResult) {
        log.info("앱카드 인증 결과 처리 시작: txnId={}", authResult.getTxnId());

        String transactionId = authResult.getTxnId();

        // 1. 트랜잭션 조회
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalStateException("트랜잭션을 찾을 수 없습니다: " + transactionId));

        Payment payment = transaction.getPayment();

        // 2. 인증 실패 처리
        if (!authResult.isAuthenticated()) {
            log.warn("앱카드 인증 실패: txnId={}", transactionId);

            // 결제 상태 및 트랜잭션 상태를 실패로 업데이트
            payment.updatePaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            transaction.updateTransactionStatus(TransactionStatus.AUTH_FAILED);
            transactionRepository.save(transaction);

            transactionLogRepository.save(TransactionLog.builder()
                    .transaction(transaction)
                    .message("앱카드 인증 실패")
                    .status(transaction.getTransactionStatus())
                    .merchant(payment.getMerchant())
                    .createdAt(LocalDateTime.now())
                    .build());

            // 인증 실패 예외 던짐
            throw new AppCardAuthenticationFailedException(transactionId);
        }

        // 3. 카드사 확인을 위한 BIN 정보 조회
        String cardNumber = authResult.getCardNumber();
        String bin = cardNumber.substring(0, 6); // 카드 번호 앞 6자리는 BIN

        BinInfo binInfo = binInfoRepository.findById(bin)
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 카드 번호입니다: " + cardNumber));

        // 4. 지원하지 않는 카드사 처리
        if (!"WOORI".equals(binInfo.getIssuer())) {
            log.error("지원하지 않는 카드사입니다: {}", binInfo.getIssuer());

            payment.updatePaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            transaction.updateTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            transactionLogRepository.save(TransactionLog.builder()
                    .transaction(transaction)
                    .message("지원하지 않는 카드사")
                    .status(transaction.getTransactionStatus())
                    .merchant(payment.getMerchant())
                    .createdAt(LocalDateTime.now())
                    .build());

            // 카드사 미지원 예외 던짐
            throw new UnsupportedIssuerException(transactionId, cardNumber);
        }

        // 5. 사용자 카드 정보 저장
        UserCard userCard = UserCard.from(binInfo, cardNumber);
        userCardRepository.save(userCard);

        // 5-2. 결제 정보에 사용자 카드 정보 업데이트
        payment.updateUserCard(userCard);
        paymentRepository.save(payment);

        log.info("사용자 카드 정보 저장 완료: 카드 타입={}", binInfo.getCardType());

        // 6. 카드사 결제 승인 요청 생성
        CardPaymentApprovalRequestDto cardApprovalRequest = CardPaymentApprovalRequestDto.from(
                transactionId,
                transaction.getAmount(),
                transaction.getMerchant().getSettlementAccountNumber(),
                cardNumber
        );

        // 7. 카드사 승인 요청 실행
        BaseResponse<CardPaymentApprovalResponseDto> baseResponse = cardClient.requestAuth(cardApprovalRequest);

        CardPaymentApprovalResponseDto approvalResponse = baseResponse.getData();
        log.info("카드사 승인 응답 수신: txnId={}, 상태={}", transactionId, approvalResponse.getPaymentStatus());

        // 8-1. 결제 및 트랜잭션 상태 업데이트
        payment.updatePaymentStatus(approvalResponse.getPaymentStatus());
        paymentRepository.save(payment);

        // 8-2. 트랜잭션 상태 업데이트
        transaction.updateTransactionStatus(
                approvalResponse.getPaymentStatus() == PaymentStatus.SUCCEEDED ? TransactionStatus.APPROVED : TransactionStatus.FAILED
        );
        transactionRepository.save(transaction);

        transactionLogRepository.save(TransactionLog.builder()
                .transaction(transaction)
                .message("카드사 승인 완료")
                .status(transaction.getTransactionStatus())
                .merchant(payment.getMerchant())
                .createdAt(LocalDateTime.now())
                .build());

        log.info("앱카드 서버에 결제 결과 전송 완료: txnId={}, status={}",
                transactionId, payment.getPaymentStatus());

        return approvalResponse;
    }

    /**
     * orderId 기반으로 결제 정보를 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByOrderId(String orderId, Merchant merchant) {
        log.info("결제 정보 조회 시작: orderId={}, merchantId={}", orderId, merchant.getId());

        Payment payment = paymentRepository.findByOrderIdAndMerchant(orderId, merchant)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 ID에 대한 결제를 찾을 수 없습니다: " + orderId));

        return PaymentDto.from(payment);
    }

}

package com.fisa.pg.service;

import com.fisa.pg.dto.request.PaymentCreateRequestDto;
import com.fisa.pg.dto.request.PaymentMethodUpdateRequestDto;
import com.fisa.pg.dto.response.PaymentCreateResponseDto;
import com.fisa.pg.dto.response.PaymentMethodUpdateResponseDto;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.PaymentDuplicateException;
import com.fisa.pg.feign.client.AppCardClient;
import com.fisa.pg.feign.client.WonQClient;
import com.fisa.pg.repository.PaymentRepository;
import com.fisa.pg.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AppCardClient appCardClient;

    private final WonQClient wonQClient;

    private final PaymentRepository paymentRepository;

    private final TransactionRepository transactionRepository;

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
                .paymentStatus(PaymentStatus.CREATED)
                .build();

        paymentRepository.save(payment);

        // 3. Transaction 생성
        Transaction transaction = Transaction.builder()
                .payment(payment)
                .merchant(merchant)
                .amount(request.getAmount())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        // 4. 응답 반환
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
            return "/api/payment/ui/wooricard/" + payment.getId();
        }

        throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
    }

}

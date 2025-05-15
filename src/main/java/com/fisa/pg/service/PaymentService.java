package com.fisa.pg.service;

import com.fisa.pg.dto.request.PaymentCreateRequestDto;
import com.fisa.pg.dto.response.PaymentCreateResponseDto;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.entity.user.Merchant;
import com.fisa.pg.exception.PaymentDuplicateException;
import com.fisa.pg.repository.PaymentRepository;
import com.fisa.pg.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

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
}

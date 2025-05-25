package com.fisa.pg.service;

import com.fisa.pg.dto.request.RefundRequestFromOneQOrderDto;
import com.fisa.pg.dto.response.RefundResponseToOneQOrderDto;
import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.payment.PaymentStatus;
import com.fisa.pg.entity.refund.Refund;
import com.fisa.pg.entity.refund.RefundStatus;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.feign.client.CardClient;
import com.fisa.pg.feign.dto.card.request.RefundRequestToCardDto;
import com.fisa.pg.feign.dto.card.response.RefundResponseFromCardDto;
import com.fisa.pg.repository.PaymentRepository;
import com.fisa.pg.repository.RefundRepository;
import com.fisa.pg.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 환불 요청을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RefundService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final CardClient cardClient;
    private final RefundRepository refundRepository;

    /**
     * 원큐오더 서버로부터의 환불 요청을 처리하고 카드사에 환불 요청을 위임한 후,
     * 결과를 다시 원큐오더에 응답으로 전달한다.
     *
     * @param request 환불 요청 DTO (paymentId 포함)
     * @return 환불 처리 결과 DTO (txnId, 결제 상태 등 포함)
     */
    @Transactional
    public RefundResponseToOneQOrderDto refund(RefundRequestFromOneQOrderDto request) {

        // 1. 결제 및 거래 조회
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));

        Transaction transaction = transactionRepository.findByPayment(payment)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제에 대한 거래 정보가 없습니다."));

        // 2. 결제 상태 확인
        if (!payment.isSucceeded()) {
            throw new IllegalStateException("SUCCEEDED 상태의 결제만 환불할 수 있습니다.");
        }


        // 3. 카드사에 환불 요청
        RefundRequestToCardDto cardRequest = RefundRequestToCardDto.builder()
                .txnId(transaction.txnId())
                .build();

        RefundResponseFromCardDto cardResponse = cardClient.requestRefundToCard(cardRequest);

        // 4. 결과에 따라 상태 업데이트
        if (cardResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            payment.updatePaymentStatus(PaymentStatus.CANCELLED);
            transaction.updateTransactionStatus(TransactionStatus.CANCELLED);
        } else {
            transaction.updateTransactionStatus(TransactionStatus.REFUND_FAILED);
        }

        // 4-1. 환불 정보 생성 및 저장
        Refund refund = Refund.builder()
                .payment(payment)
                .transaction(transaction)
                .refundAmount(payment.getAmount())
                .requestedAt(LocalDateTime.now())
                .refundStatus(RefundStatus.COMPLETED)
                .build();

        refundRepository.save(refund);

        // 5. 최종 응답 생성
        return RefundResponseToOneQOrderDto.builder()
                .paymentId(payment.getId())
                .txnId(transaction.txnId())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }
}

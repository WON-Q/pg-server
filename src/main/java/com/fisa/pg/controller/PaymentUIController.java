package com.fisa.pg.controller;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.transaction.TransactionStatus;
import com.fisa.pg.feign.dto.appcard.request.AppCardAuthRequestDto;
import com.fisa.pg.repository.PaymentRepository;
import com.fisa.pg.repository.TransactionRepository;
import com.fisa.pg.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/payment/ui")
@RequiredArgsConstructor
public class PaymentUIController {

    private final PaymentService paymentService;

    private final PaymentRepository paymentRepository;

    private final TransactionRepository transactionRepository;

    @GetMapping("/wooricard/{paymentId}")
    public String showWooriCardPaymentPage(@PathVariable Long paymentId, Model model) {

        // paymentId를 모델에 추가
        model.addAttribute("paymentId", paymentId);

        // payment.html 템플릿 렌더링
        return "payment";

    }

    /**
     * 우리카드 결제 처리 API
     * <br/>
     * 이 API는 결제 흐름 중 다음 단계들을 처리합니다:
     * <ul>
     *     <li><b>14단계</b>: 사용자가 "WOORI Pay 결제" 버튼 클릭 (payment.html의 form submit)</li>
     *     <li><b>15단계</b>: PG UI에서 PG사로 결제 방식 선택 이벤트 전달 (이 컨트롤러로 요청)</li>
     *     <li><b>16단계</b>: PG사에서 앱카드 서버로 결제 인증 API 호출 (paymentService.requestAppCardAuth())</li>
     * </ul>
     * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
     *
     * @param paymentId 결제 ID
     * @return 결제 요청 처리 상태를 담은 응답
     * @throws IllegalArgumentException 결제 정보를 찾을 수 없는 경우
     * @throws IllegalStateException 처리 가능한 트랜잭션이 없는 경우
     */
    @PostMapping("/wooricard/process")
    public ResponseEntity<Map<String, String>> processWooriCardPayment(@RequestParam("paymentId") Long paymentId) {
        log.info("우리카드 결제 폼 제출됨: paymentId={}", paymentId);

        // 결제 정보 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentId));

        // 트랜잭션 조회
        Transaction transaction = transactionRepository.findByPaymentAndTransactionStatus(
                        payment, TransactionStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("처리 가능한 트랜잭션이 없습니다"));


        // 앱카드 인증 요청 DTO 생성
        AppCardAuthRequestDto authRequest = AppCardAuthRequestDto.builder()
                .txnId(transaction.getTransactionId())
                .merchantName(payment.getMerchant().getName())
                .amount(payment.getAmount())
                .callbackUrl(payment.getCallbackUrl())
                .build();

        // 앱카드 인증 요청 처리 (내부에서 원큐오더 서버로 딥링크 전송됨)
        String deeplink = paymentService.requestDeeplink(authRequest);

        // 처리 완료 응답
        log.info("앱카드 인증 요청 처리 완료: paymentId={}", paymentId);

        Map<String, String> response = Map.of(
                "status", "success",
                "message", "앱카드 인증 요청이 성공적으로 처리되었습니다.",
                "deeplink", deeplink
        );
        return ResponseEntity.ok(response);
    }
}
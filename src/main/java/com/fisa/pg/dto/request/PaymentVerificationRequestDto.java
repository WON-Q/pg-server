package com.fisa.pg.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트(가맹점)으로부터 PG 서버로 전달되는 결제 검증 요청 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>44번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class PaymentVerificationRequestDto {

    // Todo: 현재 원큐오더 웹하고 상의가 되지 않아 해당 부분에 대해 논의를 진행하고 작성 예정

}

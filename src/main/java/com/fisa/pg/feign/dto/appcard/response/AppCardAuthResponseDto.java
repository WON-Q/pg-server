package com.fisa.pg.feign.dto.appcard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 앱카드 서버에서 PG사로 반환하는 인증 응답 DTO
 * <br/>
 * 이 DTO는 결제 흐름 중 <b>19번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참조해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardAuthResponseDto {

    /**
     * 앱 실행 딥링크 URL
     */
    private String deepLink;

}

package com.fisa.pg.entity.card;

import jakarta.persistence.*;
import lombok.*;

/**
 * BIN (Bank Identification Number) 정보를 저장하는 읽기 전용 엔티티
 * <br/>
 * 일반적으로 외부에서 주기적으로 갱신되며, (예: 카드사에서 제공하는 BIN 정보를 수기로 DB에 저장)
 * 결제 처리 시 카드사 식별 및 라우팅에 사용된다.
 */
@Table(name = "bin_info")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BinInfo {

    /**
     * 6자리로 구성된 BIN 번호
     * <br/>
     * 12자리 카드 번호의 앞 6자리로, 카드사 식별에 사용된다.
     */
    @Id
    @Column(name = "bin", length = 6)
    private String bin;

    /**
     * 카드 발급사 (예: 우리카드, 신한카드)
     */
    @Column(name = "issuer", nullable = false)
    private String issuer;

    /**
     * BIN 설명 또는 전표 인자명
     */
    @Column(name = "description")
    private String description;

    /**
     * 카드 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    /**
     * 결제 요청을 보낼 카드사 API Endpoint 주소
     */
    @Column(name = "endpoint")
    private String endpoint;

}


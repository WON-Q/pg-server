package com.fisa.pg.entity.card;


import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 카드 정보를 저장하는 엔티티
 * <br />
 * 사용자의 결제 처리를 완료한 후 카드 정보를 저장하는데 사용된다.
 * (AppCard 인증 이후 저장)
 */
@Table(name = "user_card")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCard {

    /**
     * 카드 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카드 발급사 (예: 우리카드, 신한카드)
     */
    @Column(name = "issuer", nullable = false)
    private String issuer;

    /**
     * 마스킹된 카드 번호 (예: 1234-****-****-5678)
     */
    @Column(name = "masked_card_number", nullable = false)
    private String maskedCardNumber;

    /**
     * 카드 유형
     */
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    /**
     * BinInfo를 기반으로 UserCard 엔티티를 생성하는 정적 팩토리 메서드
     *
     * @param binInfo 카드 BIN 정보
     * @param cardNumber 마스킹된 카드 번호
     * @return 생성된 UserCard 엔티티
     */
    public static UserCard from(BinInfo binInfo, String cardNumber) {
        return UserCard.builder()
                .issuer(binInfo.getIssuer())
                .maskedCardNumber(cardNumber)
                .cardType(binInfo.getCardType())
                .build();
    }

}

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
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    /**
     * BinInfo와 카드번호를 바탕으로 UserCard 인스턴스를 생성하는 정적 팩토리 메서드
     *
     * @param binInfo BIN 정보 (카드 발급사, 카드 유형 등 포함)
     * @param cardNumber 전체 카드 번호 (마스킹하지 않음)
     * @return UserCard 엔티티 객체
     */
    public static UserCard from(BinInfo binInfo, String cardNumber) {
        return UserCard.builder()
                .issuer(binInfo.getIssuer())
                .cardType(binInfo.getCardType())
                .maskedCardNumber(cardNumber)
                .build();
    }

}

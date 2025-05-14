package com.fisa.pg.repository;

import com.fisa.pg.entity.card.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserCard 엔티티에 대한 데이터 액세스를 제공하는 리포지토리
 */
@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    /**
     * 마스킹된 카드 번호로 UserCard를 찾습니다.
     *
     * @param maskedCardNumber 마스킹된 카드 번호
     * @return 찾은 UserCard (없으면 빈 Optional)
     */
    Optional<UserCard> findByMaskedCardNumber(String maskedCardNumber);

    /**
     * 카드 발급사(issuer)로 UserCard 목록을 찾습니다.
     *
     * @param issuer 카드 발급사 (예: WOORI, SHINHAN)
     * @return UserCard 목록
     */
    List<UserCard> findByIssuer(String issuer);

    /**
     * 해당 마스킹된 카드 번호의 UserCard가 존재하는지 확인합니다.
     *
     * @param maskedCardNumber 마스킹된 카드 번호
     * @return 존재 여부
     */
    boolean existsByMaskedCardNumber(String maskedCardNumber);
}
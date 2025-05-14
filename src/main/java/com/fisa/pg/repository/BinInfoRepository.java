package com.fisa.pg.repository;

import com.fisa.pg.entity.card.BinInfo;
import com.fisa.pg.entity.card.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카드 BIN 정보를 조회하는 레포지토리
 */
@Repository
public interface BinInfoRepository extends JpaRepository<BinInfo, String> {

    /**
     * 발급사별 BIN 정보 목록을 조회한다.
     *
     * @param issuer 카드 발급사
     * @return 발급사에 해당하는 BIN 정보 목록
     */
    List<BinInfo> findByIssuer(String issuer);

    /**
     * 카드 타입별 BIN 정보 목록을 조회한다.
     *
     * @param cardType 카드 타입
     * @return 카드 타입에 해당하는 BIN 정보 목록
     */
    List<BinInfo> findByCardType(CardType cardType);

    /**
     * 발급사와 카드 타입으로 BIN 정보 목록을 조회한다.
     *
     * @param issuer 카드 발급사
     * @param cardType 카드 타입
     * @return 발급사와 카드 타입에 해당하는 BIN 정보 목록
     */
    List<BinInfo> findByIssuerAndCardType(String issuer, CardType cardType);

    /**
     * 카드 번호로 해당하는 BIN 정보를 조회한다.
     *
     * @param cardNumber 카드 번호
     * @return BIN 정보 (Optional)
     */
    @Query("SELECT b FROM BinInfo b WHERE :cardNumber LIKE CONCAT(b.bin, '%')")
    Optional<BinInfo> findByCardNumber(@Param("cardNumber") String cardNumber);

    /**
     * 엔드포인트로 BIN 정보 목록을 조회한다.
     *
     * @param endpoint 카드사 API 엔드포인트
     * @return 엔드포인트에 해당하는 BIN 정보 목록
     */
    List<BinInfo> findByEndpoint(String endpoint);

    /**
     * 특정 BIN 번호가 존재하는지 확인한다.
     *
     * @param bin BIN 번호
     * @return 존재 여부
     */
    boolean existsByBin(String bin);
}
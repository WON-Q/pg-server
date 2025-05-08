package com.fisa.pg.entity.settlement;

/**
 * 정산 상태를 관리하는 enum 클래스
 * <ul>
 *     <li>READY: 정산 대기 상태</li>
 *     <li>SETTLED: 정산 진행 중 상태</li>
 *     <li>FAILED: 정산 완료 상태</li>
 * </ul>
 */
public enum MerchantSettlementStatus {

    /**
     * 정산 대기 상태
     */
    READY,

    /**
     * 정산 진행 중 상태
     */
    SETTLED,

    /**
     * 정산 완료 상태
     */
    FAILED

}

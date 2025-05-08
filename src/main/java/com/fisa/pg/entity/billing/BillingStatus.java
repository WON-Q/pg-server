package com.fisa.pg.entity.billing;

/**
 * 청구 상태를 관리하는 enum 클래스
 * <ul>
 *     <li>PENDING: 청구 대기 상태</li>
 *     <li>SUCCESS: 청구 완료 상태</li>
 *     <li>FAILED: 청구 실패 상태</li>
 *     <li>CANCELED: 청구 취소 상태</li>
 * </ul>
 */
public enum BillingStatus {

    /**
     * 청구 대기 상태
     */
    PENDING,

    /**
     * 청구 완료 상태
     */
    SUCCESS,

    /**
     * 청구 실패 상태
     */
    FAILED,

    /**
     * 청구 취소 상태
     */
    CANCELED;

}

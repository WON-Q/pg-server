package com.fisa.pg.entity.refund;

/**
 * 환불 상태를 관리하는 enum 클래스
 * <ul>
 *     <li>REQUESTED: 환불이 요청된 상태</li>
 *     <li>COMPLETED: 환불이 완료된 상태</li>
 *     <li>FAILED: 환불이 실패된 상태</li>
 * </ul>
 */
public enum RefundStatus {

    /**
     * 환불이 요청된 상태
     */
    REQUESTED,

    /**
     * 환불이 완료된 상태
     */
    COMPLETED,

    /**
     * 환불이 실패된 상태
     */
    FAILED

}

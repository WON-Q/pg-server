package com.fisa.pg.entity.webhook;

/**
 * Webhook 전송 상태를 관리하는 enum 클래스
 * <ul>
 *     <li>PENDING: 전송 대기 상태</li>
 *     <li>SUCCESS: 전송 성공 상태</li>
 *     <li>FAILED: 전송 실패 상태</li>
 * </ul>
 */
public enum WebhookStatus {

    /**
     * 전송 대기 상태
     */
    PENDING,

    /**
     * 전송 성공 상태
     */
    SUCCESS,

    /**
     * 전송 실패 상태
     */
    FAILED

}

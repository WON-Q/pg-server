package com.fisa.pg.entity.transaction;

/**
 * 결제 트랜잭션의 상태를 나타내는 enum 클래스
 * <ul>
 *     <li>PENDING: 결제 요청이 PG 서버에 도달하여 트랜잭션이 생성된 상태</li>
 *     <li>CREATED: 결제 생성 직후 상태 (아직 승인되지 않음)</li>
 *     <li>APPROVED: 결제가 승인되어 카드사 또는 은행으로부터 정상 응답을 받은 상태</li>
 *     <li>CANCELED: 사용자가 결제를 취소하거나 승인된 결제를 취소한 상태</li>
 *     <li>FAILED: 결제 요청이 처리되지 못하거나 외부 시스템(카드사 등)에서 실패한 상태</li>
 *     <li>AUTH_FAILED: 앱카드 인증이 실패한 상태 (예: 인증 거부, 생체 인증 실패 등)</li>
 * </ul>
 */
public enum TransactionStatus {

    /**
     * 결제 요청이 PG 서버에 도달하여 트랜잭션이 생성된 상태
     */
    PENDING,

    /**
     * 결제 생성 직후 상태 (아직 승인되지 않음)
     */
    CREATED,

    /**
     * 결제가 승인되어 카드사 또는 은행으로부터 정상 응답을 받은 상태
     */
    APPROVED,

    /**
     * 사용자가 결제를 취소하거나 승인된 결제를 취소한 상태
     */
    CANCELED,

    /**
     * 결제 요청이 처리되지 못하거나 외부 시스템(카드사 등)에서 실패한 상태
     */
    FAILED,

    /**
     * 앱카드 인증이 실패한 상태 (예: 인증 거부, 생체 인증 실패 등)
     */
    AUTH_FAILED

}

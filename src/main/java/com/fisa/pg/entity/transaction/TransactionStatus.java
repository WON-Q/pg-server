package com.fisa.pg.entity.transaction;

/**
 * 결제 트랜잭션의 상태를 나타내는 enum 클래스
 */
public enum TransactionStatus {

    /**
     * 결제 요청이 PG 서버에 도달하여 트랜잭션이 생성된 상태
     */
    PENDING,

    /**
     * 결제가 승인되어 카드사 또는 은행으로부터 정상 응답을 받은 상태
     */
    APPROVED,

    /**
     * 결제 요청이 처리되지 못하거나 외부 시스템(카드사 등)에서 실패한 상태
     */
    FAILED,

    /**
     * 앱카드 인증이 실패한 상태 (예: 인증 거부, 생체 인증 실패 등)
     */
    AUTH_FAILED,

    /**
     * 환불 대기 중
     */
    REFUND_PENDING,

    /**
     * 환불 승인됨
     */
    REFUND_APPROVED,

    /**
     * 환불 실패
     */
    REFUND_FAILED;


    /**
     * 환불 관련 상태인지 확인
     * @return 환불 관련 상태이면 true, 아니면 false
     */
    public boolean isRefundStatus() {
        return this == REFUND_PENDING || this == REFUND_APPROVED || this == REFUND_FAILED;
    }
}

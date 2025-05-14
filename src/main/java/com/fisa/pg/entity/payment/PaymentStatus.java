package com.fisa.pg.entity.payment;

/**
 * PaymentStatus는 결제 흐름에서의 전체 상태를 표현합니다.
 * 각 상태는 결제 요청 이후의 진행 상황을 나타냅니다.
 */
public enum PaymentStatus {

    /**
     * 결제 요청이 PG 서버에 도달하여 트랜잭션이 생성된 상태
     */
    CREATED,

    /**
     * 결제가 정상적으로 승인된 상태 (카드사에서 승인 완료된 상태)
     */
    SUCCEEDED,

    /**
     * 결제가 실패한 상태 (카드사나 앱카드사에서 승인 거부된 상태)
     * <p>
     * 결제 실패 사유 예시
     * <ul>
     *     <li>앱카드사에서 승인 거부된 경우</li>
     *         <ul>
     *             <li>인증 실패</li>
     *         </ul>
     *     <li>카드사에서 승인 거부된 경우
     *         <ul>
     *             <li>한도 초과</li>
     *             <li>카드 정지</li>
     *             <li>카드 만료</li>
     *             <li>카드사 시스템 오류</li>
     *         </ul>
     *     </li>
     *     <li>네트워크 오류</li>
     * </ul>
     */
    FAILED,

    /**
     * 결제가 취소된 상태
     */
    CANCELED,

    /**
     * 결제가 일정 시간 동안 진행되지 않아 만료된 상태 (ex. 앱카드 인증 세션이 만료된 경우)
     */
    EXPIRED,

    /**
     * 결제 환불됨
     */
    REFUNDED,

    /**
     * 결제 환불 실패 중
     */
    REFUND_FAILED



}

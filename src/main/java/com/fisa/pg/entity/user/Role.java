package com.fisa.pg.entity.user;

/**
 * 시스템 내 사용자 역할을 정의하는 열거형
 * <ul>
 *     <li>MERCHANT: 가맹점 사용자</li>
 *     <li>ADMIN: 시스템 관리자</li>
 * </ul>
 */
public enum Role {
    
    /**
     * 가맹점 사용자
     */
    MERCHANT,
    
    /**
     * 시스템 관리자
     */
    ADMIN
}
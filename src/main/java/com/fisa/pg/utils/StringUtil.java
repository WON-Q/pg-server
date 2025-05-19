package com.fisa.pg.utils;

import org.springframework.stereotype.Component;

@Component
public final class StringUtil {

    private StringUtil() {
        // 인스턴스 생성 방지용 private 생성자
    }

    /**
     * 주어진 길이의 랜덤 문자열을 생성하는 메서드
     *
     * @param length 생성할 문자열의 길이
     * @return 랜덤 문자열
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}

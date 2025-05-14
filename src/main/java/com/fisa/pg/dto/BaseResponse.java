package com.fisa.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    // 성공 응답 생성 (데이터 없음)
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(true, "SUCCESS", "요청이 성공적으로 처리되었습니다.", null);
    }

    // 성공 응답 생성 (데이터 포함)
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
    }

    // 실패 응답 생성 (에러 코드만)
    public static <T> BaseResponse<T> fail(String code) {
        return new BaseResponse<>(false, code, "요청 처리 중 오류가 발생했습니다.", null);
    }

    // 실패 응답 생성 (에러 코드와 메시지)
    public static <T> BaseResponse<T> fail(String code, String message) {
        return new BaseResponse<>(false, code, message, null);
    }
}
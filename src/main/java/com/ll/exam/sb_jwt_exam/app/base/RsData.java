package com.ll.exam.sb_jwt_exam.app.base;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    /* data가 있을 때 응답 body */
    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    /* data가 없을 때 응답 body */
    public static <T> RsData<T> of(String resultCode, String msg) {
        // retrun new RsData<>(resultCode, msg, null)로 해도 상관 없음
        // 다만 Of를 사용하면 전처리(종합해서 값을 채워넣는 등)하기 용턴
        return of(resultCode, msg, null);
    }

    public static <T> RsData<T> successOf(T data) {
        return of("S-1", "성공", data);
    }

    public static <T> RsData<T> failOf(T data) {
        return of("F-1", "실패", data);
    }

    public boolean isSuccess() {
        return resultCode.startsWith("S-1");
    }

    public boolean isFail() {
        return isSuccess() == false;
    }
}

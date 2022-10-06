package com.ll.exam.sb_jwt_exam.app.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

// @RequiredArgsConstructor 어노테이션은
// final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final SecretKey jwtSecretKey;

    public SecretKey getSecretKey() {
        return jwtSecretKey;
    }
}
package com.ll.exam.sb_jwt_exam;

import com.ll.exam.sb_jwt_exam.app.jwt.JwtProvider;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtTests {
    @Autowired
    private JwtProvider jwtProvider;
    @Value("${custom.jwt.secretKey}") // secretKey value annotation
    private String secretKeyPlain;

    @Test
    @DisplayName("secretKey 키가 존재해야한다.")
    void t1() {assertThat(secretKeyPlain).isNotNull();}

    // JWT는 HMAC 또는 RSA, ECDSA를 사용하는 공개키/개인키쌍을 서명
    // 즉 토큰에 사용할 알고리즘은 HS256 외에도 HS512, RS256(RSASSA SHA-256), ES256(ECDSA P-256 curve SHA-256) 등이 있는데
    // 그 중 HMAC을 선택하여 구현한 용
    @Test
    @DisplayName("sercretKey 원문으로 hmac 암호화 알고리즘에 맞는 SecretKey 객체를 만들 수 있다.")
    void t2() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("JwtProvider 객체로 시크릿키 객체를 생성할 수 있다.")
    void t3() {
        SecretKey secretKey = jwtProvider.getSecretKey();

        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("SecretKey 객체는 단 한번만 생성되어야 한다.")
    void t4() {
        SecretKey secretKey1 = jwtProvider.getSecretKey();
        SecretKey secretKey2 = jwtProvider.getSecretKey();

        assertThat(secretKey1 == secretKey2).isTrue();
    }
}

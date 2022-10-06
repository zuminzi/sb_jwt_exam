package com.ll.exam.sb_jwt_exam;

import com.ll.exam.sb_jwt_exam.app.jwt.JwtProvider;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
        SecretKey secretKey = TestUtil.callMethod(jwtProvider, "getSecretKey");
        // SecretKey secretKey = jwtProvider.getSecretKey(); 코드와 동일

        assertThat(secretKey).isNotNull();
    }

    @Test
    @DisplayName("SecretKey 객체는 단 한번만 생성되어야 한다.")
    void t4() {
        SecretKey secretKey1 = TestUtil.callMethod(jwtProvider, "getSecretKey");
        SecretKey secretKey2 = TestUtil.callMethod(jwtProvider, "getSecretKey");

        assertThat(secretKey1 == secretKey2).isTrue();
    }

    @Test
    @DisplayName("accessToken 을 얻는다.")
    void t5() {

        // 회원번호가 1이고
        // username이 admin 이고
        // ADMIN 역할과 MEMBER 역할을 동시에 가지고 있는 회원정보를 구성
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1L);
        claims.put("username", "admin");
        claims.put("authorities", Arrays.asList(
                new SimpleGrantedAuthority("ADMIN"),
                new SimpleGrantedAuthority("MEMBER"))
        );
        // 구성 끝

        // 지금으로부터 5시간의 유효기간을 가지는 토큰을 생성
        String accessToken = jwtProvider.generateAccessToken(claims, 60 * 60 * 5);

        System.out.println("accessToken : " + accessToken);

        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("accessToken 을 통해서 claims 를 얻을 수 있다.")
    void t6() {
        // 회원번호가 1이고
        // username이 admin 이고
        // ADMIN 역할과 MEMBER 역할을 동시에 가지고 있는 회원정보를 구성
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1L);
        claims.put("username", "admin");
        claims.put("authorities", Arrays.asList(
                new SimpleGrantedAuthority("ADMIN"),
                new SimpleGrantedAuthority("MEMBER"))
        );
        // 구성 끝

        // 지금으로부터 5시간의 유효기간을 가지는 토큰을 생성
        String accessToken = jwtProvider.generateAccessToken(claims, 60 * 60 * 5);

        System.out.println("accessToken : " + accessToken);

        assertThat(jwtProvider.verify(accessToken)).isTrue();

        Map<String, Object> claimsFromToken = jwtProvider.getClaims(accessToken);
        System.out.println("claimsFromToken : " + claimsFromToken);
    }
}

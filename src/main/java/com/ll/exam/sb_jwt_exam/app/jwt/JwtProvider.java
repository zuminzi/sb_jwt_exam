package com.ll.exam.sb_jwt_exam.app.jwt;

import com.ll.exam.sb_jwt_exam.util.Utility;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

// @RequiredArgsConstructor 어노테이션은
// final이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final SecretKey jwtSecretKey;

    private SecretKey getSecretKey() {
        return jwtSecretKey;
    }

    public String generateAccessToken(Map<String, Object> claims, int seconds) {
        long now = new Date().getTime();
        /*
        ** Date date2=new Date(0L);
        0L를 매개변수로 초기화하면 1970년 1월 1일 0시 0분 0초 UTC 기준시간으로 초기화
         */
        // Milli-second(1/1000초)마다 1씩 증가시킨 시간
        // 그래서 1000L을 값으로 넘기면 Date date=new Date(1000L); // 기준시간 + 1초로 초기화
        Date accessTokenExpiresIn = new Date(now + 1000L * seconds); // 현재시간(NOW) + 1초로 초기화

        return Jwts.builder()
                .claim("body", Utility.json.toStr(claims))
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512) // 암호화 알고리즘 방식
                .compact();
    }

    /* accessToken 유효화 인증 */
    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /* accessToken의 claims(토큰에 담는 payload, 즉 데이터) -> map으로 */
    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("body", String.class);

        return Utility.json.toMap(body);
    }
}
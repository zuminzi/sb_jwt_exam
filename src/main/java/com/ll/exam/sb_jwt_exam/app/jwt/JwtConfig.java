package com.ll.exam.sb_jwt_exam.app.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

// @Configuration 어노테이션을 사용하면 가시적으로 설정파일이고 Bean 등록할 것임을 알려서 가독성 높여주고
// Bean이 싱글톤으로 유지되도록 해준다
// @Bean 어노테이션만으로도 스프링 빈 등록은 되지만 싱글톤이 유지되지는 않는다
@Configuration
public class JwtConfig {
    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;

    @Bean //Bean 등록 어노테이션 (scope 붙이지 않는 이상 딱 한번만 실행돼서 싱글톤 유지)
    public SecretKey jwtSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
}

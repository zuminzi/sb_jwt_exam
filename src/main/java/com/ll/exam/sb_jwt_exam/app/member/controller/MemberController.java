package com.ll.exam.sb_jwt_exam.app.member.controller;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/member")
public class MemberController {
    @PostMapping("/login")
    // JSON 객체 받는 클래스 따로 생성 필요
    // Member 클래스의 username, password로 바로 받으면 제대로 안받아와짐
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", "JWT키");

        String body = "username : %s, password : %s".formatted(loginDto.getUsername(), loginDto.getPassword());

        return new ResponseEntity<>(body, headers, HttpStatus.OK); // 한 큐에 바디, 헤더, 상태코드 조절 가능
    }

    @Data
    public static class LoginDto {
        private String username;
        private String password;
    }
}
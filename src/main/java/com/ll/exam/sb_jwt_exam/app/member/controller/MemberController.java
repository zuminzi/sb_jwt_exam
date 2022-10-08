package com.ll.exam.sb_jwt_exam.app.member.controller;

import com.ll.exam.sb_jwt_exam.app.member.entity.Member;
import com.ll.exam.sb_jwt_exam.app.member.service.MemberService;
import com.ll.exam.sb_jwt_exam.util.Utility;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    // JSON 객체 받는 클래스 따로 생성 필요
    // Member 클래스의 username, password로 바로 받으면 제대로 안받아와짐
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        // null 체크
        if (loginDto.isNotValid()) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        // 올바른 username과 password인지 체크
        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        if (member == null) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword()) == false) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        // Authentication 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", "JWT_Access_Token");

        // response body 보여주지 않고 null 처리
        return Utility.spring.responseEntityOf(headers);
    }

    @Data
    public static class LoginDto {
        private String username;
        private String password;

        public boolean isNotValid() {
            return username == null || password == null || username.trim().length() == 0 || password.trim().length() == 0;
        }
    }
}
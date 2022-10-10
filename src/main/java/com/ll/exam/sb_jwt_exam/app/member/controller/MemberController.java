package com.ll.exam.sb_jwt_exam.app.member.controller;

import com.ll.exam.sb_jwt_exam.app.base.RsData;
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
    public ResponseEntity<RsData> login(@RequestBody LoginDto loginDto) {
        // null 체크
        if (loginDto.isNotValid()) {
            return Utility.spring.responseEntityOf(RsData.of("F-1", "로그인 정보가 올바르지 않습니다."));
        }

        // 올바른 username과 password인지 체크
        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        if (member == null) {
            return Utility.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword()) == false) {
            return Utility.spring.responseEntityOf(RsData.of("F-3", "비밀번호가 일치하지 않습니다."));
        }

        // Authentication 헤더 추가
        HttpHeaders headers = Utility.spring.httpHeadersOf("Authentication", "JWT_Access_Token");

        // response body 보여주지 않고 null 처리
        return Utility.spring.responseEntityOf(RsData.of("S-1", "로그인 성공, Access Token을 발급합니다."), headers);
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
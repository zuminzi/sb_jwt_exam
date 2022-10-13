package com.ll.exam.sb_jwt_exam.app.member.controller;

import com.ll.exam.sb_jwt_exam.app.base.RsData;
import com.ll.exam.sb_jwt_exam.app.member.dto.request.LoginDto;
import com.ll.exam.sb_jwt_exam.app.member.entity.Member;
import com.ll.exam.sb_jwt_exam.app.member.service.MemberService;
import com.ll.exam.sb_jwt_exam.app.security.entity.MemberContext;
import com.ll.exam.sb_jwt_exam.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public String test(@AuthenticationPrincipal MemberContext memberContext) {
        // @AuthenticationPrincipal은 세션기반 스프링시큐리티 로그인일 때 자동생성 되므로
        // JWT 로그인에서는 따로 작업 안해줄 시 null로 리턴 (로그인이 완료처리된 상태라도)
        // 즉, 세션 저장 작업 별도로 필요
        return "안녕" + memberContext;
    }


    @GetMapping("/me")
    public ResponseEntity<RsData> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) { // 임시코드, 나중에는 시프링 시큐리티를 이용해서 로그인을 안했다면, 아예 여기로 못 들어오도록
            return Utility.spring.responseEntityOf(RsData.failOf(null));
        }

        return Utility.spring.responseEntityOf(RsData.successOf(memberContext));
    }

    @PostMapping("/login")
    // JSON 객체 받는 클래스 따로 생성 필요
    // Member 클래스의 username, password로 바로 받으면 제대로 안받아와짐
    public ResponseEntity<RsData> login(@Valid @RequestBody LoginDto loginDto) {

        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        if (member == null) {
            return Utility.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword()) == false) {
            return Utility.spring.responseEntityOf(RsData.of("F-3", "비밀번호가 일치하지 않습니다."));
        }

        // token 가져오기
        //log.debug("Util.json.toStr(member.getAccessTokenClaims()) : " + Utility.json.toStr(member.getAccessTokenClaims()));

        String accessToken = memberService.genAccessToken(member);

        // Authentication 헤더 추가
        HttpHeaders headers = Utility.spring.httpHeadersOf("Authentication", accessToken);

        // response body와 헤더 채워서 리턴
        return Utility.spring.responseEntityOf(RsData.of(
                "S-1",
                "로그인 성공, Access Token을 발급합니다.",
                Utility.mapOf(
                        "accessToken", accessToken
                )
        ), headers);
    }
}
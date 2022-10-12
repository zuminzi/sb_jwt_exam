package com.ll.exam.sb_jwt_exam.app.security.filter;

import com.ll.exam.sb_jwt_exam.app.jwt.JwtProvider;
import com.ll.exam.sb_jwt_exam.app.member.entity.Member;
import com.ll.exam.sb_jwt_exam.app.member.service.MemberService;
import com.ll.exam.sb_jwt_exam.app.security.entity.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Token으로부터 회원 정보 가져오기
        /*
          AuthTests에서 헤더를 갖고 올 때 .header("Authorization", "Bearer " + accessToken) 이렇게 가져왔음
          즉 이제 Authorization 헤더에 accessToken만 있는 것이 아니라 Bearer도 있으므로 이것을 제거하고
          accessToken에서 회원정보을 가져와야 됨
        */
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());

            if (jwtProvider.verify(token)) {
                Map<String, Object> claims = jwtProvider.getClaims(token);
                String username = (String) claims.get("username");
                Member member = memberService.findByUsername(username).orElseThrow(
                        () -> new UsernameNotFoundException("'%s' Username not found.".formatted(username))
                );

                forceAuthentication(member); // 스프링시큐리티에도 강제로 로그인 처리를 하여 memberContext를 securityContext에 추가
            }
        }

        filterChain.doFilter(request, response);
    }

    private void forceAuthentication(Member member) {
        MemberContext memberContext = new MemberContext(member);

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        null,
                        member.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication); // SecurityContext에 MemberContext 담기
        SecurityContextHolder.setContext(context);
    }
}

package com.ll.exam.sb_jwt_exam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
// mock -> 실제 객체와 비슷한 객체 만들어서 사용(복잡한 의존성 단절 가능)
// Controller 테스트 할 때, 서블릿 컨테이너를 모킹하기 위해서는 @WebMvcTest 또는 @AutoConfigureMockMvc를 사용하면 됨
// @WebMvcTest와 가장 큰 차이점은 컨트롤러 뿐만 아니라 테스트 대상이 아닌 @Service나 @Repository가 붙은 객체들도 모두 메모리에 올린다는 점
//  간단하게 테스트하기 위해서는 @AutoConfigureMockMvc 대신 @WebMvcTest 사용
// 단, @ WebMvcTest는 @SpringBootTest와 같이 사용은 불가
// 각자 서로의 MockMvc를 모킹하기 때문에 충돌 발생하기 때문
@SuppressWarnings("unchecked")
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthTests {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("POST /member/login으로 로그인 처리")
    void t1() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("POST /member/login 으로 올바른 username과 password 데이터를 넘기면 JWT키를 발급해준다.")
    void t2() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();

        // 응답 헤더에서 Authentication 값 얻기
        // 실제 코드에서도 여기서 처리 X 헤더에 대한 흐름 파악 O

        MockHttpServletResponse response = mvcResult.getResponse();

        String authentication = response.getHeader("Authentication");

        assertThat(authentication).isNotEmpty();
    }

    @Test
    @DisplayName("POST /member/login 으로 username과 password 누락하면 http status 400")
    void t3() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is4xxClientError());

        mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": ""
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is4xxClientError());
    }
    @Test
    @DisplayName("POST /member/login 호출할 때 올바르지 않은 username 이나 password 를 입력하면 400")
    void t4() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user3",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is4xxClientError());

        resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "12345"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("로그인 후 얻은 JWT 토큰으로 현재 로그인 한 회원의 정보를 얻을 수 있다.")
    void t5() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        String accessToken = response.getHeader("Authentication");

        resultActions = mvc
                .perform(
                        get("/member/me")
                                .header("Authorization", "Bearer " + accessToken) // value: Bearer + 한 칸 공백 + accessToken
                        /*
                        bearer token은 (Security Header에 담아지는)token 포맷의 일종
                        API에 access하려면 accessToken을 사용해야 하는데, accessToken 액세스 토큰의 유효시간이 다 되면 bearerToken을 사용하여 새 액세스 토큰을 얻는다
                        즉 accessToken을 받으려면 인증 서버에 이 bearerToken을 클라이언트 ID와 함께 보내면 된다
                         */
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        // MemberController me 메서드에서는 @AuthenticationPrincipal MemberContext memberContext 를 사용해서 현재 로그인 한 회원의 정보를 얻어야 한다.

        // 추가
        // /member/me 에 응답 본문
        /*
          {
            "resultCode": "S-1",
            "msg": "성공",
            "data": {
              "id": 1,
              "createData": "날짜",
              "modifyData": "날짜",
              "username": "user1",
              "email": "user1@test.com"
            }
            "success": true,
            "fail": false
          }
       */
    }
}

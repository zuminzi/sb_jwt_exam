package com.ll.exam.sb_jwt_exam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

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
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthTests {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("POST /member/login 은 로그인 처리 URL 이다.")
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
}

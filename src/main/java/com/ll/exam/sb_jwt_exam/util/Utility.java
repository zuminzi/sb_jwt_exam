package com.ll.exam.sb_jwt_exam.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.sb_jwt_exam.app.jwt.AppConfig;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Utility {
    private static ObjectMapper getObjectMapper() {
        // AppConfig의 context  변수를 static으로 선언해놨기 때문에 @Autowired 어노테이션 사용불가
        // 대신 @Autowired 기능 직접 구현 -> .getBean("objectMapper")
        // 이 외의 방법) new ObjectMapper로 새로 객체 생성할 수도 있지만 싱글톤 객체로 유지하기 위해 이런 방법으로 구현
        // private final ObjectMapper objectMapper로 생성자주입하지 않는 이유? json 클래스는 static이므로 static 변수 필요
        return (ObjectMapper) AppConfig.getContext().getBean("objectMapper");
    }

    public static class json {

        /* Map -> JSON String으로 변환 */
        // ex. {"name":"mkyong","age":"37"} 형태로 변환
        public static Object toStr(Map<String, Object> map) {
            try {
                // jackson.databind.ObjectMapper 이용하여 String으로 Serialization
                return getObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        /* JSON String -> Map으로 변환 */
        // ex. {name=mkyong, age=37} 형태로 변환
        public static Map<String, Object> toMap(String jsonStr) {
            try {
                return getObjectMapper().readValue(jsonStr, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }
}


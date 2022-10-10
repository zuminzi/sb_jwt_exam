package com.ll.exam.sb_jwt_exam;

import com.ll.exam.sb_jwt_exam.util.Utility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// 검증되지 않은 연산자 관련 경고를 무시해주는 어노테이션
// compiler는 사용자가 type safe한 방법으로 Collection을 사용하는지 generic으로는 체크 할 수 없음
@SuppressWarnings("unchecked")
@ActiveProfiles("test")
public class TestUtil {

    public static <T> T callMethod(Object obj, String methodName) {
        Method method = null;

        try {
            method = obj.getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        method.setAccessible(true);

        try {
            return (T) method.invoke(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Util.mapOf")
    void t1() {
        Map<String, Integer> ages = Utility.mapOf("영수", 22, "철수", 33, "영희", 44, "민수", 55);

        /* Utility에 mapOf 도입 안했다면
        Map<String, Integer> ages = new LinkedHashMap<>();
        ages.put("영수",22);
        ages.put("철수",33);
        ...
        일일히 put() 필요
         */

        assertThat(ages.get("영수")).isEqualTo(22);
        assertThat(ages.get("철수")).isEqualTo(33);
        assertThat(ages.get("영희")).isEqualTo(44);
        assertThat(ages.get("민수")).isEqualTo(55);
    }
}

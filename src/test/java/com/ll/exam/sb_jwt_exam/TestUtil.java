package com.ll.exam.sb_jwt_exam;

import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
}

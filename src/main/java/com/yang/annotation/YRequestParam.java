package com.yang.annotation;

import java.lang.annotation.*;

/**
 * @author yzy
 * @date 2020/8/23
 * @describe
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YRequestParam {
    String value() default "";
}

package com.mactavish.ephemeral.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String url() default "/";
    Method[] method() default {Method.POST,Method.GET,Method.PUT,Method.DELETE};
}

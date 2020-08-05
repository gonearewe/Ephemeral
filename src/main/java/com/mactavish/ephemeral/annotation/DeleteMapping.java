package com.mactavish.ephemeral.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteMapping {
    String url() default "/";
}

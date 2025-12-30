package com.servletstack.adapter.in.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String value() default "";

    String[] invalidate() default {};

    long duration() default 15;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}

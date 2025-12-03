package com.dev.servlet.adapter.in.web.annotation;

import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.domain.entity.enums.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
    RequestMethod method() default RequestMethod.GET;
    Class<?> jsonType() default Void.class;
    boolean requestAuth() default true;
    String apiVersion() default "v1";
    RoleType[] roles() default {};
}

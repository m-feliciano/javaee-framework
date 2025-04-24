package com.dev.servlet.interfaces;

import com.dev.servlet.pojo.enums.PerfilEnum;
import com.dev.servlet.pojo.enums.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();

    RequestMethod method() default RequestMethod.GET;

    boolean requestAuth() default true;

    Validator[] validators() default {};

    String apiVersion() default "v1";

    PerfilEnum[] roles() default {};
}

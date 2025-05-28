package com.dev.servlet.core.annotation;
public @interface Constraints {
    String message() default "Invalid value";
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    int minLength() default 0;
    int maxLength() default 512;
    boolean notNull() default false;
    boolean notEmpty() default false;
    boolean notNullOrEmpty() default false;
    boolean isEmail() default false;
    boolean isDate() default false;
}

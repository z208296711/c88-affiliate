package com.c88.affiliate.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForUpdate {
    String fieldName() default "";
}

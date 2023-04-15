package com.example.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Autowired {

    /**
     * 注入的名称
     */
    String name() default "";

    /**
     * 是否检查，如果数值为 null 将会抛出异常 crash
     */
    boolean required() default false;
}

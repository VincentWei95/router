package com.example.router.template;

import java.util.Map;

/**
 * 拦截器组
 */
public interface IInterceptorGroup {

    /**
     * @param interceptorClassMap 拦截器 class 映射表，会通过 ASM 将拦截器 class 添加到该映射表
     */
    void loadInto(Map<String, Class<? extends IInterceptor>> interceptorClassMap);
}

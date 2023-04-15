package com.example.router.template;

/**
 * 依赖注入界面 Intent 变量接口
 */
public interface IAutowired {

    /**
     * 注入对象，Activity 或 Fragment
     */
    void inject(Object target);
}

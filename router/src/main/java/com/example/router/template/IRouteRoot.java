package com.example.router.template;

import java.util.Map;

/**
 * 路由根节点
 */
public interface IRouteRoot {

    /**
     * @param routeGroupClassMap 路由组 class 映射表，会通过 ASM 将路由组 class 添加到该映射表
     */
    void loadInto(Map<String, Class<? extends IRouteGroup>> routeGroupClassMap);
}

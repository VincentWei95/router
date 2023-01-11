package com.example.router.template;

import com.example.router.model.RouteMeta;

import java.util.Map;

/**
 * 路由组
 */
public interface IRouteGroup {

    /**
     * @param routeMap 总路由表，外部传入将路由组中的路由表添加到总路由表
     */
    void loadInto(Map<String, RouteMeta> routeMap);
}

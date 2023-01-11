package com.example.router.callback;

import com.example.router.core.RouteRequest;

/**
 * 路由处理回调
 */
public interface OnNavigationCallback {

    /**
     * 回调查找到目标跳转路由
     */
    void onFound(RouteRequest request);

    /**
     * 回调没有找到路由
     */
    void onLost(RouteRequest request);

    /**
     * 回调已经处理路由
     */
    void onArrival(RouteRequest request);

    /**
     * 回调拦截了路由
     */
    void onInterrupt(RouteRequest request);
}

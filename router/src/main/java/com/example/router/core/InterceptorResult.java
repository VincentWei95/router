package com.example.router.core;

/**
 * 拦截器处理结果
 */
public class InterceptorResult {
    private final InterceptorParam param;
    private final boolean isInterrupt;

    public InterceptorResult(InterceptorParam param, boolean isInterrupt) {
        this.param = param;
        this.isInterrupt = isInterrupt;
    }

    public RouteRequest getRequest() {
        return RouteDataTransformer.toRequest(param);
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }
}

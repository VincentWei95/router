package com.example.router.core;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.router.callback.OnNavigationCallback;
import com.example.router.template.IInterceptorGroup;
import com.example.router.template.IRouteRoot;

public class Router {
    private static boolean initialized;

    private static final RouterManager routerManager = new RouterManager();
    private static final InterceptorManager interceptorManager = new InterceptorManager();
    private static final AutowiredManager autowiredManager = new AutowiredManager();

    private Router() {
    }

    public static synchronized void init() {
        if (!initialized) {
            initialized = true;

            register();
        }
    }

    // ASM 字节码注入方法，不要修改名称！！！
    private static void register() {
        // ASM 插入代码自动注册
        // registerRouteRoot(new appRouteRoot());
        // registerRouteRoot(new loginRouteRoot());

        // registerInterceptor(new appInterceptorGroup());
        // registerInterceptor(new loginInterceptorGroup());
    }

    private static void registerRouteRoot(IRouteRoot routeRoot) {
        routerManager.registerRouteRoot(routeRoot);
    }

    private static void registerInterceptor(IInterceptorGroup interceptorGroup) {
        interceptorManager.registerInterceptor(interceptorGroup);
    }

    public static void inject(Object target) {
        autowiredManager.inject(target);
    }

    public static RouteRequest.Builder request(String path) {
        return request(path, "");
    }

    public static RouteRequest.Builder request(String path, String group) {
        return new RouteRequest.Builder(path, group);
    }

    static void navigation(@NonNull Context context, RouteRequest request, OnNavigationCallback callback) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("navigation must be called in main thread");
        }

        InterceptorResult interceptorResult = interceptorManager.intercept(context, request);
        boolean isInterrupt = interceptorResult.isInterrupt();

        if (isInterrupt && callback != null) {
            callback.onInterrupt(interceptorResult.getRequest());
        }

        try {
            if (!isInterrupt) {
                routerManager.navigation(context, interceptorResult.getRequest(), callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.router.core;

import androidx.annotation.NonNull;

public class RouteDataTransformer {
    private RouteDataTransformer() {
        throw new AssertionError();
    }

    static InterceptorParam toInterceptorParam(@NonNull RouteRequest request) {
        return new InterceptorParam(request.getPath(), request.getGroup())
                .putBundle(request.getBundle())
                .addFlags(request.getFlags())
                .setRequestCode(request.getRequestCode())
                .setFragment(request.getFragment());
    }

    static RouteRequest toRequest(@NonNull InterceptorParam param) {
        return new RouteRequest.Builder(param.getPath(), param.getGroup())
                .putBundle(param.getBundle())
                .addFlags(param.getFlags())
                .setRequestCode(param.getRequestCode())
                .setFragment(param.getFragment())
                .build();
    }
}

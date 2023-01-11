package com.example.router.template;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.router.core.InterceptorParam;
import com.example.router.core.InterceptorResult;
import com.example.router.core.RouteRequest;

/**
 * 路由拦截器接口
 * 建议只在拦截器处理路由参数补充及非耗时任务，避免出现 ANR
 */
public interface IInterceptor {

    /**
     * @param param     路由参数
     * @return          拦截结果，可以通过 {@link InterceptorResult#isInterrupt()} 判断是否该拦截器要拦截
     */
    @NonNull
    InterceptorResult proceed(Context context, InterceptorParam param);
}

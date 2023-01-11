package com.example.router.core;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.router.template.IInterceptor;
import com.example.router.template.IInterceptorGroup;
import com.example.router.util.UniqueHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 拦截器管理类
 */
public class InterceptorManager {
    private static final String TAG = "InterceptorManager";

    private static final Map<String, Class<? extends IInterceptor>> interceptorClassMap = new UniqueHashMap<>();

    InterceptorManager() {
    }

    void registerInterceptor(IInterceptorGroup interceptorGroup) {
        interceptorGroup.loadInto(interceptorClassMap);
    }

    @NonNull
    InterceptorResult intercept(Context context, RouteRequest request) {
        InterceptorParam interceptorParam = RouteDataTransformer.toInterceptorParam(request);
        List<String> interceptors = request.getInterceptors();
        if (interceptors.isEmpty()) {
            return new InterceptorResult(interceptorParam, false);
        }

        List<IInterceptor> proceedInterceptors = new ArrayList<>(interceptors.size());
        for (String interceptorName : interceptors) {
            Class<? extends IInterceptor> interceptorClass = interceptorClassMap.get(interceptorName);
            if (interceptorClass == null) {
                Log.w(TAG, "interceptor = " + interceptorName + " class not found");
                continue;
            }

            try {
                // 每次都创建，避免处理不当可能导致的内存泄漏
                // 反射创建拦截器会牺牲一定性能，一次路由上层不要过多调用拦截器及在拦截器处理耗时操作避免 ANR
                IInterceptor interceptor = interceptorClass.getConstructor().newInstance();
                proceedInterceptors.add(interceptor);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (proceedInterceptors.isEmpty()) {
            return new InterceptorResult(interceptorParam, false);
        }

        int size = proceedInterceptors.size();
        InterceptorResult result;
        int index = 0;
        do {
            result = execInterceptor(context, interceptorParam, proceedInterceptors, index);
            index++;
        } while (index < size && !result.isInterrupt());

        return result;
    }

    private InterceptorResult execInterceptor(Context context,
                                              InterceptorParam param,
                                              List<IInterceptor> interceptors,
                                              int index) {
        IInterceptor interceptor = interceptors.get(index);
        return interceptor.proceed(context, param);
    }
}

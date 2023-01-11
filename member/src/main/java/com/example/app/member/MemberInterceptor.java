package com.example.app.member;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.router.annotation.Interceptor;
import com.example.router.core.InterceptorParam;
import com.example.router.core.InterceptorResult;
import com.example.router.template.IInterceptor;

/**
 * 测试拦截器：添加一个参数到目标界面打印
 */
@Interceptor(name = "member")
public class MemberInterceptor implements IInterceptor {
    @NonNull
    @Override
    public InterceptorResult proceed(Context context, InterceptorParam param) {
        param.putString("member", "example");
        return new InterceptorResult(param, false);
    }
}

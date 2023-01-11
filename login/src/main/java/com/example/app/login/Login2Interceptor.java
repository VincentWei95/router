package com.example.app.login;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.router.annotation.Interceptor;
import com.example.router.core.InterceptorParam;
import com.example.router.core.InterceptorResult;
import com.example.router.template.IInterceptor;

@Interceptor(name = "login2")
public class Login2Interceptor implements IInterceptor {
    @NonNull
    @Override
    public InterceptorResult proceed(Context context, InterceptorParam param) {
        param.putBoolean("is_login2", true);
        return new InterceptorResult(param, false);
    }
}

package com.example.app.login;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.router.annotation.Interceptor;
import com.example.router.core.InterceptorParam;
import com.example.router.core.InterceptorResult;
import com.example.router.core.Router;
import com.example.router.template.IInterceptor;

/**
 * 测试登陆拦截器：未登陆跳转到登陆界面拦截后续处理，否则添加已登陆参数在目标界面打印
 */
@Interceptor(name = "login")
public class LoginInterceptor implements IInterceptor {
    @NonNull
    @Override
    public InterceptorResult proceed(Context context, InterceptorParam param) {
        if (!isLogin(context)) {
            Router.request("/login/login").build().navigation(context);

            return new InterceptorResult(param, true);
        }

        param.putBoolean("is_login", true);
        return new InterceptorResult(param, false);
    }

    private boolean isLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        return sp.getBoolean("is_login", false);
    }
}

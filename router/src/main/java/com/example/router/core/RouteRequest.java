package com.example.router.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.router.callback.OnNavigationCallback;
import com.example.router.enums.RouteType;
import com.example.router.util.ExtractRouteGroupUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 上层路由信息数据包装类
 */
public class RouteRequest {
    private final String path;
    private final String group;
    private final Bundle bundle;
    private final int flags;
    private final int requestCode;
    private final Fragment fragment;
    private final List<String> interceptors;

    private RouteType type;
    private Class<?> destination;

    private RouteRequest(RouteRequest.Builder builder) {
        path = builder.path;
        group = builder.group;
        bundle = builder.bundle;
        flags = builder.flags;
        requestCode = builder.requestCode;
        fragment = builder.fragment;
        interceptors = builder.interceptors;
    }

    String getPath() {
        return path;
    }

    String getGroup() {
        return group;
    }

    Bundle getBundle() {
        return bundle;
    }

    int getFlags() {
        return flags;
    }

    int getRequestCode() {
        return requestCode;
    }

    Fragment getFragment() {
        return fragment;
    }

    List<String> getInterceptors() {
        return interceptors;
    }

    RouteType getType() {
        return type;
    }

    void setType(RouteType type) {
        this.type = type;
    }

    Class<?> getDestination() {
        return destination;
    }

    void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public void navigation(@NonNull Context context) {
        navigation(context, null);
    }

    public void navigation(@NonNull Context context, OnNavigationCallback callback) {
        Router.navigation(context, this, callback);
    }

    public static final class Builder {
        private final String path;
        private final String group;
        private Bundle bundle = new Bundle();
        private int flags;
        private int requestCode;
        private Fragment fragment;
        private final List<String> interceptors = new ArrayList<>();

        public Builder(String path) {
            this(path, "");
        }

        public Builder(String path, String group) {
            if (TextUtils.isEmpty(path)) {
                throw new IllegalArgumentException("RouteRequest path is empty");
            }
            if (!path.startsWith("/")) {
                throw new IllegalArgumentException("path = " + path + " start must be '/' and contain more than 2 '/'");
            }
            if (TextUtils.isEmpty(group)) {
                group = ExtractRouteGroupUtils.extract(path);
            }

            this.path = path;
            this.group = group;
        }

        public Builder putBoolean(@Nullable String key, boolean value) {
            bundle.putBoolean(key, value);
            return this;
        }

        public Builder putByte(@Nullable String key, byte value) {
            bundle.putByte(key, value);
            return this;
        }

        public Builder putChar(@Nullable String key, char value) {
            bundle.putChar(key, value);
            return this;
        }

        public Builder putShort(@Nullable String key, short value) {
            bundle.putShort(key, value);
            return this;
        }

        public Builder putInt(@Nullable String key, int value) {
            bundle.putInt(key, value);
            return this;
        }

        public Builder putLong(@Nullable String key, long value) {
            bundle.putLong(key, value);
            return this;
        }

        public Builder putFloat(@Nullable String key, float value) {
            bundle.putFloat(key, value);
            return this;
        }

        public Builder putDouble(@Nullable String key, double value) {
            bundle.putDouble(key, value);
            return this;
        }

        public Builder putString(@Nullable String key, @Nullable String value) {
            bundle.putString(key, value);
            return this;
        }

        public Builder putCharSequence(@Nullable String key, @Nullable CharSequence value) {
            bundle.putCharSequence(key, value);
            return this;
        }

        public Builder putParcelable(@Nullable String key, @Nullable Parcelable value) {
            bundle.putParcelable(key, value);
            return this;
        }

        public Builder putSerializable(@Nullable String key, @Nullable Serializable value) {
            bundle.putSerializable(key, value);
            return this;
        }

        public Builder putBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder addFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setFragment(@NonNull Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public Builder addInterceptor(String name) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("add interceptor name is empty");
            }
            interceptors.add(name);
            return this;
        }

        public RouteRequest build() {
            return new RouteRequest(this);
        }
    }
}

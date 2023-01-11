package com.example.router.core;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * 拦截器传递参数
 */
public class InterceptorParam {
    private final String path;
    private final String group;
    private Bundle bundle = new Bundle();
    private int flags;
    private int requestCode;
    private Fragment fragment;

    public InterceptorParam(String path, String group) {
        this.path = path;
        this.group = group;
    }

    public InterceptorParam putBoolean(@Nullable String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public InterceptorParam putByte(@Nullable String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public InterceptorParam putChar(@Nullable String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    public InterceptorParam putShort(@Nullable String key, short value) {
        bundle.putShort(key, value);
        return this;
    }

    public InterceptorParam putInt(@Nullable String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public InterceptorParam putLong(@Nullable String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public InterceptorParam putFloat(@Nullable String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public InterceptorParam putDouble(@Nullable String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public InterceptorParam putString(@Nullable String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public InterceptorParam putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    public InterceptorParam putParcelable(@Nullable String key, @Nullable Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    public InterceptorParam putSerializable(@Nullable String key, @Nullable Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public InterceptorParam putBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public InterceptorParam addFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public InterceptorParam setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public InterceptorParam setFragment(@NonNull Fragment fragment) {
        this.fragment = fragment;
        return this;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public int getFlags() {
        return flags;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public Fragment getFragment() {
        return fragment;
    }
}

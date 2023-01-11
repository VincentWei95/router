package com.example.router.enums;

public enum RouteType {
    ACTIVITY("android.app.Activity");

    private final String className;

    RouteType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}

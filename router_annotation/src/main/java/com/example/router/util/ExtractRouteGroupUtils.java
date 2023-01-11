package com.example.router.util;

public class ExtractRouteGroupUtils {
    private ExtractRouteGroupUtils() {
        throw new AssertionError();
    }

    public static String extract(String path) {
        // /login/login -> login
        return path.substring(1, path.indexOf("/", 1));
    }
}

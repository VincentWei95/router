package com.example.router.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.router.callback.OnNavigationCallback;
import com.example.router.model.RouteMeta;
import com.example.router.template.IRouteGroup;
import com.example.router.template.IRouteRoot;
import com.example.router.util.UniqueHashMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 路由跳转管理类
 */
public class RouterManager {
    private static final String TAG = "RouterManager";

    private static final Map<String, Class<? extends IRouteGroup>> routeGroupClassMap = new UniqueHashMap<>();
    // 总路由表，分段加载从 group 加载路由表如果遇到相同的路由 path 将抛出异常，path 在项目必须唯一
    private static final Map<String, RouteMeta> routeMap = new UniqueHashMap<>();

    RouterManager() {
    }

    void registerRouteRoot(IRouteRoot routeRoot) {
        routeRoot.loadInto(routeGroupClassMap);
    }

    void navigation(Context context, RouteRequest request, OnNavigationCallback callback) {
        String path = request.getPath();
        RouteMeta routeMeta = routeMap.get(path);
        if (routeMeta == null) {
            // 没找到路由信息，说明该路径下的路由组也还未被加载到总路由表，将路由组中所在路由信息加载到路由表
            addRouteGroupDynamic(request.getGroup());

            routeMeta = routeMap.get(path);
            if (routeMeta == null) {
                callbackLost(request, callback);

                throw new RuntimeException("path = " + path + " not found routeMeta");
            }
        }
        request.setType(routeMeta.getType());
        request.setDestination(routeMeta.getDestination());

        callbackFound(request, callback);

        Intent intent = new Intent(context, request.getDestination());
        intent.putExtras(request.getBundle());

        int flags = request.getFlags();
        if (flags != 0) {
            intent.setFlags(flags);
        }

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (!resolveActivity(context, intent)) {
            Log.e(TAG, "path = " + path + ", group = " + request.getGroup() + ", cannot resolve activity");
            return;
        }

        int requestCode = request.getRequestCode();
        if (requestCode != 0) {
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else if (request.getFragment() != null) {
                request.getFragment().startActivityForResult(intent, requestCode);
            } else {
                Log.e(TAG, "context need Activity type to call startActivityForResult if requestCode != 0");
            }
        } else {
            context.startActivity(intent);
        }

        callbackArrival(request, callback);
    }

    boolean resolveActivity(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }

    /**
     * 路由分段加载
     */
    void addRouteGroupDynamic(String group) {
        Class<? extends IRouteGroup> routeGroupClass = routeGroupClassMap.get(group);
        if (routeGroupClass == null) {
            throw new RuntimeException("group = " + group + " not found routeGroupClass");
        }

        try {
            IRouteGroup routeGroup = routeGroupClass.getConstructor().newInstance();
            routeGroup.loadInto(routeMap);

            routeGroupClassMap.remove(group);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void callbackFound(RouteRequest request, OnNavigationCallback callback) {
        if (callback != null) {
            callback.onFound(request);
        }
    }

    private void callbackLost(RouteRequest request, OnNavigationCallback callback) {
        if (callback != null) {
            callback.onLost(request);
        }
    }

    private void callbackArrival(RouteRequest request, OnNavigationCallback callback) {
        if (callback != null) {
            callback.onArrival(request);
        }
    }
}

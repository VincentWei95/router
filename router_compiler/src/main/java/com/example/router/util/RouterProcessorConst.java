package com.example.router.util;

public class RouterProcessorConst {
    // Generate
    // 路由根节点 RouteRoot 生成的全类名位置：com.example.router.root.moduleNameRouteRoot
    // 路由组 RouteGroup 生成的全类名位置：com.example.router.group.groupNameRouteGroup
    // 拦截器组 InterceptorGroup 生成的全类名位置：com.example.router.interceptor.moduleNameInterceptorGroup
    public static final String GENERATE_ROUTE_ROOT_PACKAGE = "com.example.router.root";
    public static final String GENERATE_ROUTE_GROUP_PACKAGE = "com.example.router.group";
    public static final String GENERATE_INTERCEPTOR_GROUP_PACKAGE = "com.example.router.interceptor";

    public static final String METHOD_LOAD_INTO = "loadInto";

    public static final String ROUTE_ROOT_CLASS_NAME = "{0}RouteRoot";
    public static final String ROUTE_ROOT_PARAMETER_NAME = "routeGroupClassMap";

    public static final String ROUTE_GROUP_CLASS_NAME = "{0}RouteGroup";
    public static final String ROUTE_GROUP_PARAMETER_NAME = "routeMap";

    public static final String INTERCEPTOR_GROUP_CLASS_NAME = "{0}InterceptorGroup";
    public static final String INTERCEPTOR_PARAMETER_NAME = "interceptorClassMap";

    // System interface
    public static final String TYPE_ACTIVITY = "android.app.Activity";

    // Custom interface
    public static final String ROUTER_PACKAGE = "com.example.router";
    private static final String TEMPLATE_PACKAGE = ".template";
    public static final String TYPE_IROUTE_ROOT = ROUTER_PACKAGE + TEMPLATE_PACKAGE + ".IRouteRoot";
    public static final String TYPE_IROUTE_GROUP = ROUTER_PACKAGE + TEMPLATE_PACKAGE + ".IRouteGroup";
    public static final String TYPE_IINTERCEPTOR_GROUP = ROUTER_PACKAGE + TEMPLATE_PACKAGE + ".IInterceptorGroup";
    public static final String TYPE_IINTERCEPTOR = ROUTER_PACKAGE + TEMPLATE_PACKAGE + ".IInterceptor";

    // Log
    static final String LOG_TAG = "RouterCompiler";
    public static final String NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    // Options of processor
    public static final String KEY_MODULE_NAME = "MODULE_NAME";
}

package com.example.router.util;

public class RouterSettings {
    public static final String PLUGIN_NAME = "com.example.router";

    // 需要扫描的包名
    public static final String GENERATE_ROUTE_ROOT_PACKAGE = "com/example/router/root/";
    public static final String GENERATE_INTERCEPTOR_PACKAGE = "com/example/router/interceptor/";

    // 用于扫描文件查找实现类
    private static final String INTERFACE_PACKAGE = "com/example/router/template/";
    public static final String IROUTE_ROOT_INTERFACE_NAME = INTERFACE_PACKAGE + "IRouteRoot";
    public static final String IINTERCEPTOR_GROUP_INTERFACE_NAME = INTERFACE_PACKAGE + "IInterceptorGroup";

    // 扫描插入字节码的目标类
    public static final String ROUTER_MANAGER_FILE_NAME = "com/example/router/core/Router";
    public static final String ROUTER_MANAGER_CLASS_FILE_NAME = ROUTER_MANAGER_FILE_NAME + ".class";

    // 插入字节码的方法名
    public static final String HACK_REGISTER_METHOD_NAME = "register";
    public static final String HACK_REGISTER_ROUTE_ROOT_METHOD_NAME = "registerRouteRoot";
    public static final String HACK_REGISTER_INTERCEPTOR_METHOD_NAME = "registerInterceptor";

    // 方法描述符
    public static final String IROUTE_ROOT_INTERFACE_DESCRIPTOR = "(Lcom/example/router/template/IRouteRoot;)V";
    public static final String IINTERCEPTOR_INTERFACE_DESCRIPTOR = "(Lcom/example/router/template/IInterceptorGroup;)V";
}

package com.example.router.core;

import com.example.router.template.IAutowired;

/**
 * 属性注入管理类
 */
public class AutowiredManager {

    AutowiredManager() {
    }

    void inject(Object target) {
        inject(target, null);
    }

    void inject(Object instance, Class<?> parent) {
        Class<?> clazz = parent == null ? instance.getClass() : parent;

        IAutowired iAutowired = getAutowired(clazz);
        iAutowired.inject(instance);

        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && !superClazz.getName().startsWith("android")) {
            inject(instance, superClazz);
        }
    }

    private IAutowired getAutowired(Class<?> clazz) {
        String autowiredClassName = clazz.getName() + "Autowired";
        try {
            return (IAutowired) Class.forName(autowiredClassName)
                    .getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create " + autowiredClassName + " failed");
        }
    }
}

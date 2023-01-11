package com.example.router.model;

import com.example.router.enums.RouteType;

import javax.lang.model.element.Element;

/**
 * 路由信息数据类
 */
public class RouteMeta {
    // 路由类型
    private final RouteType type;
    // 路由跳转目标类
    private final Class<?> destination;
    // 路由路径
    private final String path;
    // 路由组
    private String group;

    // 路由原始类型，仅用于 processor 取值使用
    private Element rawType;

    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group) {
        return new RouteMeta(type, destination, path, group);
    }

    public RouteMeta(RouteType type, Class<?> destination, String path, String group) {
        this.type = type;
        this.destination = destination;
        this.path = path;
        this.group = group;
    }

    public RouteType getType() {
        return type;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public String getPath() {
        return path;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

    public Element getRawType() {
        return rawType;
    }

    @Override
    public String toString() {
        return "RouteMeta{" +
                "type=" + type +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}

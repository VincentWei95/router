package com.example.router.enums;

/**
 * 默认的 TypeKind 没有对象数据类型，在默认 TypeKind 基础上新增类型
 */
public enum TypeKind {
    // Base type
    BOOLEAN,
    BYTE,
    SHORT,
    INT,
    LONG,
    CHAR,
    FLOAT,
    DOUBLE,

    // Other type
    STRING,
    SERIALIZABLE,
    PARCELABLE
}

package com.example.router.util;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class UniqueHashMap<K, V> extends HashMap<K, V> {

    @Nullable
    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            throw new IllegalArgumentException("container contains key = " + key + ", you need define unique key");
        }
        return super.put(key, value);
    }
}

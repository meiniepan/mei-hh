package com.wuyou.mongo;

/**
 */
public interface WithId<T> {

    String NAME = "id";
    T getId();
}

package com.wuyou.server.entities;

/**
 */
public interface WithId<T> {

    String NAME = "id";
    T getId();
}

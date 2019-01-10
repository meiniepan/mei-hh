package com.wuyou.mongo;

/**
 * @author hjn
 * @created 2019-01-09
 **/
public interface WithVersion {
    String NAME = "version";

    long getVersion();
}

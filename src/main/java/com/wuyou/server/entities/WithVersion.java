package com.wuyou.server.entities;

/**
 * @author hjn
 * @created 2019-01-09
 **/
public interface WithVersion {
    String NAME = "version";

    long getVersion();
}

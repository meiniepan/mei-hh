package com.wuyou.base.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 */
public class MD5Utils {

    public static String encoderByMd5(String data) {
        return data != null ? DigestUtils.md5Hex(data) : null;
    }
}

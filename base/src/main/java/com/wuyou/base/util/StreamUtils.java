package com.wuyou.base.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author hjn
 * @created 2019-01-07
 **/
public class StreamUtils {
    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}

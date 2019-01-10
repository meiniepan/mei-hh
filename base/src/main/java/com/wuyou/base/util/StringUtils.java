package com.wuyou.base.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author hjn
 * @created 2019-01-07
 **/
public class StringUtils {
    public StringUtils() {
    }

    public static String readFromInputStream(InputStream inputStream) {
        return readFromInputStream(inputStream, true);
    }

    public static String readFromInputStream(InputStream inputStream, boolean closeAfterRead) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        String result = null;

        try {
            int size;
            while((size = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
            }

            result = outputStream.toString("UTF-8");
        } catch (IOException var10) {
            var10.printStackTrace();
        } finally {
            if (closeAfterRead) {
                StreamUtils.closeStream(inputStream);
            }

            StreamUtils.closeStream(outputStream);
        }

        return result;
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static int convert2Int(String text, int defaultValue) {
        if (text != null && text.length() > 0) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException var3) {
                var3.printStackTrace();
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static String getRandomString() {
        return MD5Utils.encoderByMd5(String.valueOf(Math.random()));
    }
}

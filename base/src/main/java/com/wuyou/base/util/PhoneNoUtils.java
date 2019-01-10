package com.wuyou.base.util;

import java.util.regex.Pattern;

/**
 */
public class PhoneNoUtils {

    private static final String MOBILE_REGX = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";

    private static final String CONCEAL_STR = "****";

    public static boolean isValidPhoneNo(String phone) {

        if (phone != null && phone.length() == 11 && phone.charAt(0) == '1') {
            for (int i = 1; i < phone.length(); i++) {
                char c = phone.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    //验证手机号的正则表达式
    public static boolean checkPhone(String phone){

        return phone != null && Pattern.matches(MOBILE_REGX, phone);
    }

    public static String concealPhone(String phone) {

        if (isValidPhoneNo(phone)) {
            return phone.substring(0, 3) + CONCEAL_STR + phone.substring(7);
        } else {
            return null;
        }
    }
}

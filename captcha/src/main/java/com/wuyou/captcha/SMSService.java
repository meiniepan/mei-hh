package com.wuyou.captcha;

public interface SMSService {
    boolean send(String targetPhone,String appSignName, String templateCode, String parameters);
}

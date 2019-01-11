package com.wuyou.captcha.service;

public interface SMSService {
    boolean send(String targetPhone,String appSignName, String templateCode, String parameters);
}

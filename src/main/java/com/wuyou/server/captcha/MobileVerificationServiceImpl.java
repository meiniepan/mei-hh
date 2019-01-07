package com.wuyou.server.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MobileVerificationServiceImpl implements MobileVerificationService {

    private static final int VCODE_BASE = 1000000;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SMSService service;

    private final String appSignName = "来到";
    private final String templateCode = "SMS_126361106";


    @Override
    public Boolean send(String phone) {
        String vCode = String.valueOf(VCODE_BASE + Math.round(Math.random() * VCODE_BASE)).substring(1);
        if (service.send(phone, appSignName, templateCode, "{'code':" + vCode + "}")) {
//            redisTemplate.opsForValue().set(phone, MD5Utils.encoderByMd5(vCode), 5, TimeUnit.MINUTES);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean verify(String phone, String vCode) {
        //TODO
//        String verificationCode = redisTemplate.opsForValue().get(phone);
//        if (vCode != null && MD5Utils.encoderByMd5(vCode).equals(verificationCode)) {
//            redisTemplate.delete(phone);
        return true;
//        }
//        return false;
    }
}

package com.wuyou.captcha.service;

import com.wuyou.captcha.repository.Captcha;
import com.wuyou.captcha.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class MobileVerificationServiceImpl implements MobileVerificationService {

    private static final int VCODE_BASE = 1000000;

    @Autowired
    SMSService service;

    @Autowired
    CaptchaRepository repository;

    private final String appSignName = "来到";
    private final String templateCode = "SMS_126361106";


    @Override
    public Boolean send(String phone) {
        String vCode = String.valueOf(VCODE_BASE + Math.round(Math.random() * VCODE_BASE)).substring(1);
        if (service.send(phone, appSignName, templateCode, "{'code':" + vCode + "}")) {
            Captcha captcha = new Captcha(phone, vCode, Calendar.getInstance().getTime());
            repository.save(captcha);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean verify(String phone, String vCode) {
        Captcha captcha = repository.findOneByMobile(phone);
        if (captcha != null && captcha.getCaptcha().equals(vCode)) {
            repository.delete(captcha);
            return true;
        }
        return false;
//        String verificationCode = redisTemplate.opsForValue().get(phone);
//        if (vCode != null && MD5Utils.encoderByMd5(vCode).equals(verificationCode)) {
//            redisTemplate.delete(phone);
//        }
//        return false;
    }
}

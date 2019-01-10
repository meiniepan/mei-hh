package com.wuyou.captcha;

public interface MobileVerificationService {
    /**
     * 向指定手机号发送验证码
     *
     * @param phone 手机号码
     * @return 是否发送成功
     */
    Boolean send(String phone);

    /**
     * 通过手机号和短信验证码核验
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 是否匹配
     */
    Boolean verify(String phone, String verificationCode);

}

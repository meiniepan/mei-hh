package com.wuyou.server.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author hjn
 * @created 2019-01-09
 **/
@Document
public class Captcha {


    @Field
    @Indexed(name = "_date_", expireAfterSeconds = 10)
    private Date sendDate;

    private String captcha;

    @Id
    private String mobile;

    public Captcha() {
    }

    @PersistenceConstructor
    public Captcha(String mobile, String captcha, Date sendDate) {
        this.mobile = mobile;
        this.captcha = captcha;
        this.sendDate = sendDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}

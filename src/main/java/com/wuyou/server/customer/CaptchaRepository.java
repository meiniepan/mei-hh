package com.wuyou.server.customer;


import com.wuyou.server.entities.Captcha;
import com.wuyou.server.repository.BaseRepository;
import org.bson.types.ObjectId;


public interface CaptchaRepository extends BaseRepository<Captcha, ObjectId> {
    Captcha findOneByMobile(String mobile);
}

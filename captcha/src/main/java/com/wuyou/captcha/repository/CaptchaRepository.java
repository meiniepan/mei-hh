package com.wuyou.captcha.repository;


import com.wuyou.mongo.repository.BaseRepository;
import org.bson.types.ObjectId;

public interface CaptchaRepository extends BaseRepository<Captcha, ObjectId> {
    Captcha findOneByMobile(String mobile);
}

package com.wuyou.payment.wechat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author hjn
 * @created 2019-01-08
 **/
public interface WechatPaymentRepository extends MongoRepository<WechatPaymentAction, ObjectId> {
    WechatPaymentAction findOneByPrepayId(String prepayId);
}

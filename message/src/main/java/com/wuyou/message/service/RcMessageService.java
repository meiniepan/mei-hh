package com.wuyou.message.service;

import io.rong.models.response.ResponseResult;

/**
 * @author hjn
 * @created 2019-01-17
 **/
public interface RcMessageService {

    String registerRcUser(String uid, String name, String avatar);

    ResponseResult sendSingleMessage(String fromUid, String toUid, String content,String extraData);

}

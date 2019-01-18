package com.wuyou.message;

import com.wuyou.base.BaseResponse;
import com.wuyou.base.HttpCodeMessage;
import com.wuyou.message.entities.SendMessageRequest;
import com.wuyou.message.entities.TokenRequest;
import com.wuyou.message.service.RcMessageService;
import io.rong.models.response.ResponseResult;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hjn
 * @created 2019-01-17
 **/
@RestController
@RequestMapping(path = "/message")
public class MessageEndPoint {

    @Autowired
    RcMessageService messageService;

    @RequestMapping(path = "/token", method = RequestMethod.POST)
    public BaseResponse getUserRcToken(@RequestBody TokenRequest request) {
        String token = messageService.registerRcUser(request.uid, request.name, request.avatar);
        if (TextUtils.isEmpty(token)) {
            return new BaseResponse(HttpStatus.BAD_REQUEST, HttpCodeMessage.TC020048);
        }
        return new BaseResponse(HttpStatus.OK, token);
    }


    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public BaseResponse sendSingleMessage(@RequestBody SendMessageRequest request) {
        ResponseResult responseResult = messageService.sendSingleMessage(request.from, request.to, request.content);
        if (responseResult != null && responseResult.code == 200) {
            return new BaseResponse(HttpStatus.OK);
        }
        return new BaseResponse(HttpStatus.BAD_REQUEST, responseResult == null ? HttpCodeMessage.TC020048 : responseResult.msg);
    }
}

package com.wuyou.message.service;

import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.user.User;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author hjn
 * @created 2019-01-17
 **/
@Service
public class RcMessageServiceImpl implements RcMessageService {

    @Value("${rc.appKey}")
    String appKey;
    @Value("${rc.appSecret}")
    String appSecret;

    @Override
    public String registerRcUser(String uid, String name, String avatar) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        User User = rongCloud.user;
        UserModel user = new UserModel()
                .setId(uid)
                .setName(name)
                .setPortrait(TextUtils.isEmpty(avatar) ? "http://www.rongcloud.cn/images/logo.png" : avatar);
        TokenResult result = null;
        try {
            result = User.register(user);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 自定义api地址
     */
    private static final String api = "http://api.cn.ronghub.com";

    @Override
    public ResponseResult sendSingleMessage(String fromUid, String toUid, String content, String extraData) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Private privateService = rongCloud.message.msgPrivate;
        String[] targetIds = {toUid};
        TxtMessage txtMessage = new TxtMessage(content, "{'pushData':" + extraData + "}");
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(fromUid)
                .setTargetId(targetIds)
                .setObjectName(txtMessage.getType())
                .setContent(txtMessage)
                .setPushContent(content)
                .setPushData("{\"pushData\":" + content + "}")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = null;
        try {
            privateResult = privateService.send(privateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("send private message:  " + privateResult.toString());

        return privateResult;

    }
}

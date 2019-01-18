package com.wuyou.message.service;

import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
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


    public void sendMessage() throws Exception {

    }

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

    private static final TxtMessage txtMessage = new TxtMessage("hello", "helloExtra");
    /**
     * 自定义api地址
     */
    private static final String api = "http://api.cn.ronghub.com";

    @Override
    public ResponseResult sendSingleMessage(String fromUid, String toUid, String content) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        //自定义 api 地址方式
        Private privateService = rongCloud.message.msgPrivate;
        MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;

        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/system.html#send
         *
         * 发送系统消息
         *
         */
        String[] targetIds = {toUid};
        SystemMessage systemMessage = new SystemMessage()
                .setSenderId(fromUid)
                .setTargetId(targetIds)
                .setObjectName(txtMessage.getType())
                .setContent(txtMessage)
                .setPushContent(content)
                .setPushData("{'pushData':'hello'}")
                .setIsPersisted(0)
                .setIsCounted(0)
                .setContentAvailable(0);

        ResponseResult result = null;
        try {
            result = system.send(systemMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("2609751433442958892")
                .setTargetId(targetIds)
                .setObjectName(txtMessage.getType())
                .setContent(txtMessage)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setCount("4")
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

        System.out.println("send system message:  " + result.getCode()+"......."+result.getMsg());
        System.out.println("send private message:  " + privateResult.toString());

        return result;

    }
}

package com.wuyou.face.service;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cloudauth.model.v20180916.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.wuyou.customer.CustomerRepository;
import com.wuyou.customer.entities.AuthData;
import com.wuyou.customer.entities.Customer;
import com.wuyou.face.entities.AuthTokenResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author hjn
 * @created 2019-01-16
 **/
@Service
public class FaceAuthServiceImpl implements FaceAuthService {
    private final String BIZ = "customer-kyc-cert";
    private final String AUTH_ACCESS_KEY = "LTAIiyabSPNhGkYp";
    private final String AUTH_ACCESS_SECRET = "i7hKSFehHQuiLs45TqkU5IBLc6AhvO";

    @Autowired
    CustomerRepository repository;

    public void authToken() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AUTH_ACCESS_KEY, AUTH_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        //认证ID, 由使用方指定, 发起不同的认证任务需要更换不同的认证ID
        String ticketId = UUID.randomUUID().toString();
        String token = null; //认证token, 表达一次认证会话
        int statusCode = -1; //-1 未认证, 0 认证中, 1 认证通过, 2 认证不通过
        //1. 服务端发起认证请求, 获取到token
//GetVerifyToken接口文档：https://help.aliyun.com/document_detail/57050.html
        GetVerifyTokenRequest getVerifyTokenRequest = new GetVerifyTokenRequest();
        getVerifyTokenRequest.setBiz(BIZ);
        getVerifyTokenRequest.setTicketId(ticketId);
        try {
            GetVerifyTokenResponse response = client.getAcsResponse(getVerifyTokenRequest);
            token = response.getData().getVerifyToken().getToken(); //token默认30分钟时效，每次发起认证时都必须实时获取
        } catch (ClientException e) {
            e.printStackTrace();
        }

        //2. 服务端将token传递给无线客户端
//3. 无线客户端用token调起无线认证SDK
//4. 用户按照由无线认证SDK组织的认证流程页面的指引，提交认证资料
//5. 认证流程结束退出无线认证SDK，进入客户端回调函数
//6. 服务端查询认证状态(客户端回调中也会携带认证状态, 但建议以服务端调接口确认的为准)
//GetStatus接口文档：https://help.aliyun.com/document_detail/57049.html
        GetStatusRequest getStatusRequest = new GetStatusRequest();
        getStatusRequest.setBiz(BIZ);
        getStatusRequest.setTicketId(ticketId);
        try {
            GetStatusResponse response = client.getAcsResponse(getStatusRequest);
            statusCode = response.getData().getStatusCode();
        } catch (
                ClientException e) {
            e.printStackTrace();
        }

        //7. 服务端获取认证资料
//GetMaterials接口文档：https://help.aliyun.com/document_detail/57641.html
        GetMaterialsRequest getMaterialsRequest = new GetMaterialsRequest();
        getMaterialsRequest.setBiz(BIZ);
        getMaterialsRequest.setTicketId(ticketId);
        if (1 == statusCode || 2 == statusCode) { //认证通过or认证不通过
            try {
                GetMaterialsResponse response = client.getAcsResponse(getMaterialsRequest);
                //后续业务处理
                System.out.println(JSON.toJSON(response));
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
//常见问题：https://help.aliyun.com/document_detail/57640.html
    }


    @Override
    public AuthTokenResponse generateToken() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AUTH_ACCESS_KEY, AUTH_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        String ticketId = UUID.randomUUID().toString();
        String token;
        GetVerifyTokenRequest getVerifyTokenRequest = new GetVerifyTokenRequest();
        getVerifyTokenRequest.setBiz(BIZ);
        getVerifyTokenRequest.setTicketId(ticketId);
        try {
            GetVerifyTokenResponse response = client.getAcsResponse(getVerifyTokenRequest);
            token = response.getData().getVerifyToken().getToken();
            return new AuthTokenResponse(token, ticketId);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AuthData getAuthInfo(ObjectId uid, String ticketId) {
        int statusCode = -1;
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AUTH_ACCESS_KEY, AUTH_ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        GetStatusRequest getStatusRequest = new GetStatusRequest();
        getStatusRequest.setBiz(BIZ);
        getStatusRequest.setTicketId(ticketId);
        try {
            GetStatusResponse response = client.getAcsResponse(getStatusRequest);
            statusCode = response.getData().getStatusCode();
        } catch (
                ClientException e) {
            e.printStackTrace();
        }
        GetMaterialsRequest getMaterialsRequest = new GetMaterialsRequest();
        getMaterialsRequest.setBiz(BIZ);
        getMaterialsRequest.setTicketId(ticketId);
        if (1 == statusCode || 2 == statusCode) { //认证通过or认证不通过
            try {
                GetMaterialsResponse response = client.getAcsResponse(getMaterialsRequest);
                AuthData authData = new AuthData();
                BeanUtils.copyProperties(response.getData(), authData);
                if (saveAuthData(uid, authData)) {
                    return authData;
                }
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean saveAuthData(ObjectId uid, AuthData data) {
        Customer customer = repository.findById(uid);
        if (uid == null) {
            return false;
        }

        customer.setAuthData(data);
        return repository.save(customer) != null;
    }
}
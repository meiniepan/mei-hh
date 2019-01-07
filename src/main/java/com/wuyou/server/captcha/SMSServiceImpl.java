package com.wuyou.server.captcha;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.stereotype.Service;

@Service
public class SMSServiceImpl implements SMSService {
    private static final String product = "Dysmsapi";
    private static final String domain = "dysmsapi.aliyuncs.com";
    private static final String accessKeyId = "LTAI8spYsoFKCSBt";
    private static final String accessKeySecret = "UkAZ6CoXy6ANovVs3tqQfYXOTXzT0T";

    public SMSServiceImpl() {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    }

    @Override
    public boolean send(String targetPhone, String appSignName, String templateCode, String parameters) {
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(targetPhone);
            request.setSignName(appSignName);
            request.setTemplateCode(templateCode);
            request.setTemplateParam(parameters);
            SendSmsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
            return querySendDetailsResponse != null && ("OK".equals(querySendDetailsResponse.getCode()));
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }


}

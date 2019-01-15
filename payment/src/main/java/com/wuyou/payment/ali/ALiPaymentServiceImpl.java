package com.wuyou.payment.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.wuyou.base.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author hjn
 * @created 2019-01-10
 **/

@Service
public class ALiPaymentServiceImpl implements ALiPaymentService {
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";
    private static final String ALI_ENDPOINT = "https://openapi.alipay.com/gateway.do";

    @Value("${alipay.public_key}")
    private String ALIPAY_PUBLIC_KEY;
    @Value("${alipay.app_id}")
    private String appId;
    @Value("${alipay.app_key}")
    private String appKey;
    @Value("${alipay.time_out_express}")
    private String timeOutExpress;
    @Value("{alipay.notify_url}")
    private String notifyUrl;

    @Override
    public BaseResponse<?> generatePayment(String paymentId, String targetId, BigDecimal totalFee) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(ALI_ENDPOINT,
                appId, appKey, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("serve standard ");
        model.setSubject("serve title");
        model.setOutTradeNo(paymentId);
        model.setTimeoutExpress(timeOutExpress);
        model.setTotalAmount(String.valueOf(totalFee));
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
            return new BaseResponse<>(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean verifyPayment(Map<String, String> params) {
        try {
            boolean checkV1 = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE);
            if (checkV1) {
                //TODO  modify the order
            }
            return checkV1;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return false;
    }


}

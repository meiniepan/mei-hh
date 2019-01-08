package com.wuyou.server.payment;

import com.wuyou.server.BaseRequest;
import com.wuyou.server.BaseResponse;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * aaa
 *
 * @author hjn
 * @created 2019-01-07
 **/
@RestController
@RequestMapping(path = "/payment/wechat")
public class WechatPaymentEndpoint {

    @Autowired
    private WechatPaymentService wechatPaymentService;

    @RequestMapping(path = "/orderquery",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse<?> orderQuery(@RequestParam("me") String id, @RequestBody BaseRequest<String> requestBody) {
        String prepayId = requestBody.getValue();
        WechatPaymentAction wechatPaymentAction = wechatPaymentService.getPaymentActionByPrepayId(prepayId);
        if (wechatPaymentAction != null && wechatPaymentAction.getOwnerId().equals(id)) {
            if (wechatPaymentAction.getResultCode() == null) {
                try {
                    wechatPaymentAction = wechatPaymentService.orderquery(wechatPaymentAction);
                    if (null != wechatPaymentAction) {
                        if (WechatPaymentService.Code.SUCCESS.equals(wechatPaymentAction.getResultCode())) {
                            return new BaseResponse(HttpStatus.OK);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return new BaseResponse(HttpStatus.EXPECTATION_FAILED);
            } else if (WechatPaymentService.Code.SUCCESS.equals(wechatPaymentAction.getResultCode())) {
                return new BaseResponse(HttpStatus.OK);
            } else {
                return new BaseResponse(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new BaseResponse(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/unifiedorder",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse<?> unifiedOrder(@RequestParam("me") String selfId,
                                        @RequestBody WeChatUnifiedOrderRequest weChatUnifiedOrderRequest) {

        BigDecimal totalFee = weChatUnifiedOrderRequest.getTotalFee();
        if (totalFee == null || totalFee.floatValue() <= 0) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST);
        }

        totalFee = totalFee.setScale(2, RoundingMode.FLOOR);        // 保留小数点后两位

        String paymentId = weChatUnifiedOrderRequest.getPaymentId();

        String targetId = weChatUnifiedOrderRequest.getTargetId();
        if (null == targetId || targetId.trim().isEmpty()) {
            targetId = selfId;
        }

        try {
            WechatPaymentPrepay unifiedorder = wechatPaymentService.unifiedorder(selfId, targetId, paymentId, totalFee, "");

            if (null != unifiedorder) {
                return new BaseResponse<>(unifiedorder, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(path = "/notify", method = RequestMethod.POST)
    public String notifyResult(@RequestBody String notifyBody) {
        boolean notifySuccess = false;
        try {
            notifySuccess = wechatPaymentService.notify(notifyBody);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Document resultDocument = DocumentHelper.createDocument();
        Element resultRoot = resultDocument.addElement(WechatPaymentService.Field.ROOT);

        if (notifySuccess) {
            resultRoot.addElement(WechatPaymentService.Field.RETURN_CODE).addCDATA(WechatPaymentService.Code.SUCCESS);
            resultRoot.addElement(WechatPaymentService.Field.RETURN_MSG).addCDATA(WechatPaymentService.Code.OK);
        } else {
            resultRoot.addElement(WechatPaymentService.Field.RETURN_CODE).addCDATA(WechatPaymentService.Code.FAIL);
            resultRoot.addElement(WechatPaymentService.Field.RETURN_MSG).addCDATA("处理失败");
        }

        return resultDocument.asXML();
    }
}

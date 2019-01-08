package com.wuyou.server.payment;

import java.math.BigDecimal;

/**
 * 微信支付服务
 *
 * @author hjn
 * @created 2019-01-07 14:44
 **/
public interface WechatPaymentService {
    WechatPaymentAction getPaymentActionByPrepayId(String prepayId);

    WechatPaymentPrepay unifiedorder(String ownerId, String targetId, String paymentId, BigDecimal totalFee, String clientIp) throws Exception;

    WechatPaymentAction orderquery(WechatPaymentAction wechatRechargeAction) throws Exception;

    boolean notify(String notifyXML) throws Exception;

    interface Field {

        String ROOT = "xml";

        String SIGN = "sign";

        String APP_ID = "appid";

        String MCH_ID = "mch_id";

        String PREPAY_ID = "prepayid";

        String NONCESTR = "noncestr";

        String PACKAGE = "package";

        String TIMESTAMP = "timestamp";

        String PARTNER_ID = "partnerid";

        String ATTACH = "attach";

        String RESULT_CODE = "result_code";

        String RETURN_CODE = "return_code";

        String RETURN_MSG = "return_msg";

        String OUT_TRADE_NO = "out_trade_no";

        String NONCE_STR = "nonce_str";
    }

    interface Code {

        String SUCCESS = "SUCCESS";

        String FAIL = "FAIL";

        String OK = "OK";
    }
}

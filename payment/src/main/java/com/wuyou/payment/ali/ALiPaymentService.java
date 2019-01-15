package com.wuyou.payment.ali;

import com.wuyou.base.BaseResponse;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author hjn
 * @created 2019-01-10
 **/
public interface ALiPaymentService {

    BaseResponse generatePayment(String paymentId, String targetId, BigDecimal totalFee);

    boolean verifyPayment(Map params);

    void queryPayment(String outTradeNo, String tradeNo);
}

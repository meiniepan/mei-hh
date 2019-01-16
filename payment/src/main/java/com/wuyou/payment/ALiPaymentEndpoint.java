package com.wuyou.payment;

import com.wuyou.base.BaseResponse;
import com.wuyou.payment.ali.ALiPaymentService;
import com.wuyou.payment.entities.PaymentUnifiedOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hjn
 * @created 2019-01-11
 **/
@RestController
@RequestMapping(path = "/payment/ali")
public class ALiPaymentEndpoint {

    @Autowired
    ALiPaymentService aLiPaymentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    BaseResponse<String> getEnv() {
        return new BaseResponse<>("6666666");
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public BaseResponse<?> generatePayment(@RequestBody PaymentUnifiedOrderRequest request) {
        return aLiPaymentService.generatePayment(request.getPaymentId(), request.getTargetId(), request.getTotalFee());
    }


    @RequestMapping(value = "/notify", method = RequestMethod.POST,
            consumes = "application/x-www-form-urlencoded;charset=UTF-8",
            produces = "application/x-www-form-urlencoded;charset=UTF-8")
    public void notifyResult(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            System.out.println(name + "..........." + valueStr);
            params.put(name, valueStr);
        }
//        gmt_create.............2019-01-15 15:49:10
//        charset.............UTF-8
//        seller_email.............pay@ulaidao.com
//        subject.............serve title
//        sign.............Ift51dUf2EVJ7KT+2DdP/UzDP/2YxcBHVJ45kGrEpSlG8qKQTCKom3Wm/FJh+azKIR5FXIzj6IybHof9J54wciPyXVFCqB4mUIczAksqRRc3q5zgZt8TzasLh9DyC2ron+hujlu3JYjG3kX4VrAP0JA3x8f6Wf7fvNZ9oxJj60V+QEoIOzgMwoiABynecQcvbFu2Un3v146y6bkRzyj8h9S0YWkIuJJ9kGjIb5QaQZQj/Tsf/Q3PaZS0L7Bj12fyvdpsEHwspI1WFjtCYffJQuJrg/ZO6YLUqUn0PkbD8cUSvjvKjMzOpRzhhA0ETT60Jfb3uiy07ZNbIakxCl4IOw==
//                body.............serve standard
//        buyer_id.............2088702624437580
//        invoice_amount.............0.01
//        notify_id.............2019011500222154911037581032036833
//        fund_bill_list.............[{"amount":"0.01","fundChannel":"PCREDIT"}]
//        notify_type.............trade_status_sync
//        trade_status.............TRADE_SUCCESS
//        receipt_amount.............0.01
//        app_id.............2018071760726311
//        buyer_pay_amount.............0.01
//        sign_type.............RSA2
//        seller_id.............2088031857228103
//        gmt_payment.............2019-01-15 15:49:10
//        notify_time.............2019-01-15 15:49:11
//        version.............1.0
//        out_trade_no.............1317
//        total_amount.............0.01
//        trade_no.............2019011522001437581009356248
//        auth_app_id.............2018071760726311
//        buyer_logon_id.............189****8767
//        point_amount.............0.00
        try {
            if (aLiPaymentService.verifyPayment(params)) {
                response.getWriter().print("success");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

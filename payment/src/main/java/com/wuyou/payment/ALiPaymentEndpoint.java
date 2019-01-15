package com.wuyou.payment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuyou.base.BaseResponse;
import com.wuyou.payment.ali.ALiPaymentService;
import com.wuyou.payment.entities.PaymentUnifiedOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
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
        System.out.println("11111111111111111");
        return aLiPaymentService.generatePayment(request.getPaymentId(), request.getTargetId(), request.getTotalFee());
    }


    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public BaseResponse notifyResult(@RequestBody HashMap requestParams) {
        System.out.println("!!!!!!!!!!!!!");
        System.out.println(JSON.toJSON(requestParams));
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
            System.out.println(valueStr);
            requestParams.put(name, valueStr);
        }
        if (aLiPaymentService.verifyPayment(requestParams)) {
            return new BaseResponse(HttpStatus.OK);
        }
        return new BaseResponse(HttpStatus.BAD_REQUEST);
    }
}

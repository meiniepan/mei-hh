package com.wuyou.payment.wechat;

import com.wuyou.base.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hjn
 * @created 2019-01-07
 **/
@Service
public class WechatPaymentServiceImpl implements WechatPaymentService {

    @Value("${wechat.customer_wechat_app_id}")
    private String appId;
    @Value("${wechat.customer_wechat_merchant_id}")
    private String mchId;
    @Value("${wechat.customer_wechat_app_secret}")
    private String appKey;

    @Value("${wechat.notify_url}")
    private String notifyUrl;

    @Autowired
    private WechatPaymentRepository repository;


    private final String appName = "来到";
    private String host = "https://api.mch.weixin.qq.com";
    private String placePath = "/pay/unifiedorder";
    private String queryPath = "/pay/orderquery";

    @Override
    public WechatPaymentAction getPaymentActionByPrepayId(String prepayId) {
        return repository.findOneByPrepayId(prepayId);
    }

    @Override
    public WechatPaymentPrepay unifiedorder(String ownerId, String targetId, String paymentId, BigDecimal totalFee, String clientIp) throws Exception {
        WechatPaymentAction wechatPaymentAction = new WechatPaymentAction();
        wechatPaymentAction.setId((UUIDUtils.generateObjectId()));
        wechatPaymentAction.setCreateTime(Calendar.getInstance().getTime());
        wechatPaymentAction.setBody(appName + "-账户交易");
        wechatPaymentAction.setOutTradeNo(paymentId);
        wechatPaymentAction.setOwnerId(ownerId);
        wechatPaymentAction.setTargetId(targetId);
        wechatPaymentAction.setTotalFee(totalFee);
        wechatPaymentAction.setTimeStart(getCurrentTimeForRequest(0));

        Document requestDocument = DocumentHelper.createDocument();
        Element requestRoot = requestDocument.addElement(Field.ROOT);

        requestRoot.addElement(Field.APP_ID).addText(appId);
        requestRoot.addElement(Field.MCH_ID).addText(mchId);
        requestRoot.addElement("notify_url").addText(notifyUrl);

        requestRoot.addElement(Field.NONCE_STR).addText(StringUtils.getRandomString());
        requestRoot.addElement("body").addText(wechatPaymentAction.getBody());
        requestRoot.addElement(Field.OUT_TRADE_NO).addText(wechatPaymentAction.getOutTradeNo());
        requestRoot.addElement("total_fee").addText(String.valueOf(wechatPaymentAction.getTotalFee().intValue()));
        requestRoot.addElement("spbill_create_ip").addText(clientIp);
        requestRoot.addElement("time_start").addText(wechatPaymentAction.getTimeStart());
        requestRoot.addElement("time_expire").addText(getCurrentTimeForRequest(6));
        requestRoot.addElement("trade_type").addText("APP");
        requestRoot.addElement("limit_pay").addText("no_credit");

        requestRoot.addElement(Field.ATTACH).addText(wechatPaymentAction.getId().toHexString());

        String requestSign = sign(requestRoot, Field.SIGN, appKey);
        requestRoot.addElement(Field.SIGN).addText(requestSign);

        HttpResponse response = HttpUtils.doPost(host, placePath, new HashMap<>(), null, requestDocument.asXML());
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) {
            return null;
        }

        HttpEntity httpEntity = response.getEntity();
        InputStream inputStream = httpEntity.getContent();

        SAXReader reader = new SAXReader();
        Element responseRoot = null;

        try {
            Document responseDocument = reader.read(inputStream);
            responseRoot = responseDocument.getRootElement();
        } catch (DocumentException exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeStream(inputStream);
        }

        if (responseRoot != null && checkDocument(responseRoot) && compareResultField(responseRoot, Field.RETURN_CODE, Code.SUCCESS)) {
            Document resultDocument = DocumentHelper.createDocument();
            Element resultRoot = resultDocument.addElement(Field.ROOT);
            String prepayId = responseRoot.elementText("prepay_id");

            resultRoot.addElement(Field.APP_ID).addText(appId);
            resultRoot.addElement(Field.PARTNER_ID).addText(mchId);
            resultRoot.addElement(Field.PREPAY_ID).addText(prepayId);

            resultRoot.addElement(Field.PACKAGE).addText("Sign=WXPay");
            resultRoot.addElement(Field.NONCESTR).addText(StringUtils.getRandomString());
            resultRoot.addElement(Field.TIMESTAMP).addText(getCurrentTimeForResult());

            wechatPaymentAction.setPrepayId(prepayId);
            //TODO save
            if (repository.save(wechatPaymentAction) != null) {
                return createPrepayFromDocument(resultRoot);
            }
        }
        return null;
    }

    private WechatPaymentPrepay createPrepayFromDocument(Element root) {
        WechatPaymentPrepay wechatPaymentPrepay = new WechatPaymentPrepay();
        wechatPaymentPrepay.setAppid(root.elementText(Field.APP_ID));
        wechatPaymentPrepay.setNoncestr(root.elementText(Field.NONCESTR));
        wechatPaymentPrepay.setPackage(root.elementText(Field.PACKAGE));
        wechatPaymentPrepay.setPartnerid(root.elementText(Field.PARTNER_ID));
        wechatPaymentPrepay.setPrepayid(root.elementText(Field.PREPAY_ID));
        wechatPaymentPrepay.setTimestamp(root.elementText(Field.TIMESTAMP));

        wechatPaymentPrepay.setSign(sign(root, Field.SIGN, appKey));

        return wechatPaymentPrepay;
    }

    private String sign(Element root, String exclusion, String appKey) {
        List<String> segments = new ArrayList<>();
        Iterator elementIterator = root.elementIterator();
        while (elementIterator.hasNext()) {
            Element element = (Element) elementIterator.next();
            String name = element.getName(), text = element.getText();
            if (text.length() > 0 && !name.equals(exclusion)) {
                segments.add(name + "=" + text);
            }
        }

        segments.sort((o1, o2) -> o1.compareToIgnoreCase(o2));

        StringBuilder stringBuilder = new StringBuilder();
        for (String segment : segments) {
            stringBuilder.append(segment);
            stringBuilder.append("&");
        }
        stringBuilder.append("key=");
        stringBuilder.append(appKey);

        return MD5Utils.encoderByMd5(stringBuilder.toString()).toUpperCase();
    }

    private String getCurrentTimeForRequest(int delayMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, delayMinutes);
        return new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
    }


    private String getCurrentTimeForResult() {
        String secondsStr = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        int dotIndex = secondsStr.indexOf('.');
        return dotIndex == -1 ? secondsStr : secondsStr.substring(0, dotIndex);
    }

    private boolean compareResultField(Element rootElement, String fieldName, String fieldValue) {
        Element element = rootElement.element(fieldName);
        return element != null && element.getText().equals(fieldValue);
    }

    private boolean checkDocument(Element responseRoot) {
        if (!compareResultField(responseRoot, Field.RETURN_CODE, Code.SUCCESS)) {
            // ;TODO 记录错误信息 root.element("return_msg").getText()
            return false;
        }
        return true;
    }

    @Override
    public WechatPaymentAction orderquery(WechatPaymentAction wechatPaymentAction) throws Exception {
        Document requestDocument = DocumentHelper.createDocument();
        Element requestRoot = requestDocument.addElement(Field.ROOT);

        requestRoot.addElement(Field.APP_ID).addText(appId);
        requestRoot.addElement(Field.MCH_ID).addText(mchId);

        requestRoot.addElement(Field.NONCE_STR).addText(StringUtils.getRandomString());
        requestRoot.addElement(Field.OUT_TRADE_NO).addText(wechatPaymentAction.getOutTradeNo());

        String requestSign = sign(requestRoot, Field.SIGN, appKey);
        requestRoot.addElement(Field.SIGN).addText(requestSign);

        HttpResponse response = HttpUtils.doPost(host, queryPath, new HashMap(), null, requestDocument.asXML());
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) {
            return null;
        }

        String resultXml = StringUtils.readFromInputStream(response.getEntity().getContent());
        if (null != resultXml && notify(resultXml)) {
            return repository.findOneByPrepayId(wechatPaymentAction.getPrepayId());
        }
        return null;
    }

    @Override
    public boolean notify(String notifyXML) throws Exception {
        Element responseRoot = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(notifyXML.getBytes());
        SAXReader reader = new SAXReader();

        try {
            Document responseDocument = reader.read(inputStream);
            responseRoot = responseDocument.getRootElement();
        } catch (DocumentException exception) {
            exception.printStackTrace();
        } finally {
            StreamUtils.closeStream(inputStream);
        }
        if (responseRoot != null && checkDocument(responseRoot)) {
            String actionId = responseRoot.elementText(Field.ATTACH);

            WechatPaymentAction wechatRechargeAction = repository.findOne(new ObjectId(actionId));
            if (wechatRechargeAction.getResultCode() == null) { //没处理过
                if (updatePaymentAction(responseRoot, wechatRechargeAction)) {
                    if (Code.SUCCESS.equals(wechatRechargeAction.getTradeState())) {
                        //TODO:支付成功,修改订单状态
                    } else {

                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updatePaymentAction(Element responseRoot, WechatPaymentAction wechatRechargeAction) {
        wechatRechargeAction.setResultCode(responseRoot.elementText("result_code"));
        wechatRechargeAction.setTradeState(responseRoot.elementText("trade_state"));
        wechatRechargeAction.setTradeStateDesc(responseRoot.elementText("trade_state_desc"));
        if (repository.save(wechatRechargeAction) != null) {
            return true;
        }
        return false;
    }
}

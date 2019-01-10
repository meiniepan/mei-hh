package com.wuyou.payment.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author hjn
 * @created 2019-01-07
 **/
public class WechatPaymentPrepay {
    private String appid;

    @JsonProperty("mch_id")
    private String partnerid;
    @JsonProperty("prepay_id")
    private String prepayid;

    @JsonProperty("packageValue")
    private String _package;

    @JsonProperty("nonce_str")
    private String noncestr;

    private String timestamp;

    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}

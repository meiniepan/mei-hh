package com.wuyou.databasetransfer.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Document
public class Customer {
    @Id
    @Field("id")
    public ObjectId user_id;

    public String password;
    public String account;
    public String face;
    public String nickname;
    public String reg_ip;
    public String email;
    public String mobile;
    public String wechat_id;
    public int gender;
    public String rc_id;
    public long birthday;
    public String rc_token;
    public long created_at;
    public long updated_at;
    public long deleted_at;

    public Customer(ObjectId objectId) {
        this.user_id = objectId;
    }

    public ObjectId getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReg_ip() {
        return reg_ip;
    }

    public void setReg_ip(String reg_ip) {
        this.reg_ip = reg_ip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechat_id() {
        return wechat_id;
    }

    public void setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRc_id() {
        return rc_id;
    }

    public void setRc_id(String rc_id) {
        this.rc_id = rc_id;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getRc_token() {
        return rc_token;
    }

    public void setRc_token(String rc_token) {
        this.rc_token = rc_token;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public long getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(long deleted_at) {
        this.deleted_at = deleted_at;
    }
}

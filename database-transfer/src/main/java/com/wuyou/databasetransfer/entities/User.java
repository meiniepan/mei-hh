package com.wuyou.databasetransfer.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Entity
public class User {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long user_id;
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
}

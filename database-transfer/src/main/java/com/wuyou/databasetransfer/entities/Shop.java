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
public class Shop {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int shop_id;
    public String cate_id;
    public String area_id;
    public String city_id;
    public String shop_name;
    public String logo;
    public String photo;
    public String tel;
    public String mobile;
    public String contacter;
    public String address;
    public double lng;
    public double lat;
    public String start_at;
    public String end_at;
    public String rc_id;
    public String rc_token;
    public long created_at;
    public long updated_at;
    public long deleted_at;
    public String auth_token;


}

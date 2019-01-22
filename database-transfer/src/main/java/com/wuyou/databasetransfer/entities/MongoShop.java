package com.wuyou.databasetransfer.entities;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Document
public class MongoShop {
    @Id
    @Field("shop_id")
    public String id;

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

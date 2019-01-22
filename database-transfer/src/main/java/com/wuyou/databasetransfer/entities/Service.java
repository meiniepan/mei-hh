package com.wuyou.databasetransfer.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@Entity
public class Service {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;
    public String cate_id;
    public String city_id;
    public String shop_id;
    public double lng;
    public double lat;
    public String title;
    public String intro;
    public float price;
    public float visiting_fee;
    public String unit;
    public String photo;
//    public List<String> images;
    public String notice;
    public String start_at;
    public String end_at;
    public long sales;
    public String advert_word;
    public String market_price;
    public int status;
    public long created_at;
    public long updated_at;
    public long deleted_at;


}


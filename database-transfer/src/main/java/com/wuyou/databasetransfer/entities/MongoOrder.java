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
public class MongoOrder {

    @Id
    @Field("order_id")
    public ObjectId id;

    public String order_no;
    public String city_id;
    public String shop_id;
    public String user_id;
    public String worker_id;
    public String service_date;
    public String service_time;
    public int status;
    public int pay_type;
    public String remark;
    public float amount;
    public long pay_time;
    public long created_at;
    public long updated_at;
    public long deleted_at;
}

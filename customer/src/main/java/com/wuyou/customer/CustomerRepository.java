package com.wuyou.customer;


import com.wuyou.customer.entities.Customer;
import com.wuyou.mongo.repository.BaseRepository;
import org.bson.types.ObjectId;

public interface CustomerRepository extends BaseRepository<Customer, ObjectId> {

    Customer findOneByMobile(String mobile);

    Customer findById(ObjectId id);


}

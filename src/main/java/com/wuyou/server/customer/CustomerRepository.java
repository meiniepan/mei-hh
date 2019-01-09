package com.wuyou.server.customer;


import com.wuyou.server.entities.Customer;
import com.wuyou.server.repository.BaseRepository;
import org.bson.types.ObjectId;

import java.util.List;


public interface CustomerRepository extends BaseRepository<Customer, ObjectId> {

    Customer findOneByMobile(String mobile);

    Customer findById(ObjectId id);


}

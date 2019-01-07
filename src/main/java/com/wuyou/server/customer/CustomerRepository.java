package com.wuyou.server.customer;


import com.wuyou.server.entities.Customer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface CustomerRepository extends MongoRepository<Customer, ObjectId> {

    Customer findOneByMobile(String mobile);

    Customer findById(ObjectId id);

    boolean setMultiFieldsByID(ObjectId id, List<String> propNames,
                               Customer valueBox) throws NoSuchFieldException, IllegalAccessException;
}

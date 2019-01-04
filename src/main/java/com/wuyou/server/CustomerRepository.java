package com.wuyou.server;


import com.wuyou.server.entities.Customer;
import com.wuyou.server.mongo.CommonRepositoryCustom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CustomerRepository extends MongoRepository<Customer, ObjectId> {

    Customer findOneByMobile(String mobile);

    Customer findById(ObjectId id);

}

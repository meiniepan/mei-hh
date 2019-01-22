package com.wuyou.databasetransfer;

import com.wuyou.base.BaseResponse;
import com.wuyou.base.util.UUIDUtils;
import com.wuyou.databasetransfer.entities.Customer;
import com.wuyou.databasetransfer.entities.User;
import com.wuyou.databasetransfer.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hjn
 * @created 2019-01-22
 **/
@RestController
@RequestMapping(path = "/database")
public class DataEndPoint {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MongoOrderRepository mongoOrderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoUserRepository mongoUserRepository;

    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ServiceRepository serviceRepository;


    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<String> getEnv() {
        doTransfer();
        return new BaseResponse<>("ok");
    }


    private void doTransfer() {
        List<User> all = userRepository.findAll();
        List<Customer> mongoUsers = new ArrayList<>();
        Customer mongoUser;
        for (User user : all) {
            mongoUser = new Customer(UUIDUtils.generateObjectId());
            BeanUtils.copyProperties(user, mongoUser);
            System.out.println(user.user_id + "............" + mongoUser.user_id);
            mongoUsers.add(mongoUser);
        }
        mongoUserRepository.save(mongoUsers);
    }
}

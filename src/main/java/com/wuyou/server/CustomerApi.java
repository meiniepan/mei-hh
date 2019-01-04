package com.wuyou.server;

import com.wuyou.server.entities.Customer;
import com.wuyou.server.entities.RegisterBody;
import com.wuyou.server.entities.TCustomer;
import com.wuyou.server.util.BeanUtils;
import com.wuyou.server.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomerApi {

    @Autowired
    private CustomerRepository repository;

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
    public @ResponseBody
    BaseResponse<TCustomer> getCustomer(@PathVariable ObjectId id) {
        Customer cus = repository.findById(id);
        if (cus == null) {
            return new BaseResponse<>(HttpStatus.NOT_FOUND);
        }
        TCustomer tCustomer = new TCustomer();
        BeanUtils.copyProperties(cus, tCustomer);
        return new BaseResponse<>(tCustomer);
    }

    @RequestMapping(value = "/customer/update/{id}/{propNames}", method = RequestMethod.PUT)
    public BaseResponse updateCustomer(@PathVariable String id, @RequestBody() TCustomer customerTemplate) {
        Customer c = repository.findById(new ObjectId(id));
        if (c == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND);
        }
        try {
            BeanUtils.copyProperties(customerTemplate, c);
            return new BaseResponse(repository.save(c) != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST, MemberHttpCodeMessage.TC020004);
        }
    }


    public Customer loginByPhone(String phone) {
        Customer personMember = repository.findOneByMobile(phone);
        // TODO 处理其他登录环节

        return personMember;
    }

    @RequestMapping(value = "/customer/create", method = RequestMethod.POST)
    public Map<String, Object> registerByPhone(@RequestBody RegisterBody body) {
        ObjectId personMemberId = UUIDUtils.generateObjectId();
        Customer personMember = new Customer(personMemberId, Calendar.getInstance().getTime(), body.getMobile());
        personMember.setName("用户" + body.getMobile());
        repository.save(personMember);
        Map<String, Object> result = new HashMap<>();
        result.put("uid", personMemberId.toHexString());
        result.put("token", "token");
        result.put("expired", "expired");
        return result;
    }
}
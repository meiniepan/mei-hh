package com.wuyou.customer;

import com.wuyou.base.BaseResponse;
import com.wuyou.base.HttpCodeMessage;
import com.wuyou.base.util.BeanUtils;
import com.wuyou.base.util.UUIDUtils;
import com.wuyou.captcha.service.MobileVerificationService;
import com.wuyou.customer.entities.Customer;
import com.wuyou.customer.entities.RegisterBody;
import com.wuyou.customer.entities.TCustomer;
import com.wuyou.customer.entities.UserToken;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(path = "/customer")
public class CustomerEndPoint {

    @Autowired
    private CustomerRepository repository;

//    @Autowired
//    private RcMessageService messageService;

    @Value("${app.env}")
    String appEnv;

    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<String> getEnv() {
        return new BaseResponse<>(appEnv);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BaseResponse<TCustomer> getCustomer(@PathVariable ObjectId id) {
        Customer cus = repository.findById(id);
        if (cus == null) {
            return new BaseResponse<>(HttpStatus.NOT_FOUND, HttpCodeMessage.TC020009);
        }
        TCustomer tCustomer = new TCustomer();
        BeanUtils.copyProperties(cus, tCustomer);
        return new BaseResponse<>(tCustomer);
    }

    @RequestMapping(value = "/update/{id}/{propNames}", method = RequestMethod.PUT)
    public BaseResponse updateCustomerByProps(@PathVariable ObjectId id, @RequestBody TCustomer customerTemplate, @PathVariable List<String> propNames) {
        Customer c = repository.findById(id);
        if (c == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND, HttpCodeMessage.TC020009);
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerTemplate, customer);
        try {
            return new BaseResponse(repository.setMultiFieldsByID(id, propNames, customer) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST, HttpCodeMessage.TC020004);
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse updateCustomer(@PathVariable ObjectId id, @RequestBody TCustomer customerTemplate) {
        Customer c = repository.findById(id);
        if (c == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND, HttpCodeMessage.TC020009);
        }
        BeanUtils.copyProperties(customerTemplate, c);
        repository.save(c);

        BeanUtils.copyProperties(c, customerTemplate);
        try {
            return new BaseResponse<>(customerTemplate);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST, HttpCodeMessage.TC020047);
        }
    }

    @Autowired
    MobileVerificationService mobileVerificationService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BaseResponse registerByPhone(@RequestBody RegisterBody body) {
        if (mobileVerificationService.verify(body.getMobile(), body.getCaptcha())) {
            ObjectId personMemberId = UUIDUtils.generateObjectId();
            Customer personMember = new Customer(personMemberId, Calendar.getInstance().getTime(), body.getMobile(), "");
            personMember.setName("用户" + body.getMobile());
            repository.save(personMember);

            UserToken token = new UserToken();
            token.id = personMemberId.toHexString();
            return new BaseResponse<>(token);
        } else {
            return new BaseResponse(HttpStatus.BAD_REQUEST, HttpCodeMessage.TC020001);
        }
    }
}
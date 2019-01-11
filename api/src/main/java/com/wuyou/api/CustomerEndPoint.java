package com.wuyou.api;

import com.wuyou.base.BaseRequest;
import com.wuyou.base.BaseResponse;
import com.wuyou.base.HttpCodeMessage;
import com.wuyou.base.util.BeanUtils;
import com.wuyou.base.util.PhoneNoUtils;
import com.wuyou.base.util.UUIDUtils;
import com.wuyou.captcha.service.MobileVerificationService;
import com.wuyou.customer.CustomerRepository;
import com.wuyou.customer.entities.Customer;
import com.wuyou.customer.entities.RegisterBody;
import com.wuyou.customer.entities.TCustomer;
import com.wuyou.customer.entities.UserToken;
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

    @Value("${app.env}")
    String appEnv;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    BaseResponse<String> getEnv() {
        return new BaseResponse<>(appEnv);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/update/{id}/{propNames}", method = RequestMethod.PUT)
    public BaseResponse updateCustomerByProps(@PathVariable ObjectId id, @RequestBody TCustomer customerTemplate, @PathVariable List<String> propNames) {
        Customer c = repository.findById(id);
        if (c == null) {
            return new BaseResponse(HttpStatus.NOT_FOUND);
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
            return new BaseResponse(HttpStatus.NOT_FOUND);
        }
        repository.save(BeanUtils.copyProperties(customerTemplate, c));
        try {
            return new BaseResponse<>(c);
        } catch (Exception e) {
            return new BaseResponse(HttpStatus.BAD_REQUEST, HttpCodeMessage.TC020004);
        }
    }

    @Autowired
    MobileVerificationService mobileVerificationService;

    @RequestMapping(value = "/customer/captcha", method = RequestMethod.POST)
    public BaseResponse applyVerificationCode(@RequestBody BaseRequest<String> request) {
        if (!PhoneNoUtils.isValidPhoneNo(request.getValue())) {
            return new BaseResponse(HttpStatus.BAD_REQUEST);
        }
        if (mobileVerificationService.send(request.getValue())) {
            return new BaseResponse(HttpStatus.OK);
        } else {
            return new BaseResponse(HttpStatus.BAD_GATEWAY);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BaseResponse registerByPhone(@RequestBody RegisterBody body) {
        if (mobileVerificationService.verify(body.getMobile(), body.getCaptcha())) {
            ObjectId personMemberId = UUIDUtils.generateObjectId();
            Customer personMember = new Customer(personMemberId, Calendar.getInstance().getTime(), body.getMobile());
            personMember.setName("用户" + body.getMobile());
            repository.save(personMember);
            UserToken token = new UserToken();
            token.id = personMemberId.toHexString();
            return new BaseResponse<>(token);
        } else {
            return new BaseResponse(HttpStatus.BAD_REQUEST, "验证码错误");
        }
    }
}
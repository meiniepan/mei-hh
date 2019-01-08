package com.wuyou.server.customer;

import com.wuyou.server.BaseRequest;
import com.wuyou.server.BaseResponse;
import com.wuyou.server.HttpCodeMessage;
import com.wuyou.server.captcha.MobileVerificationService;
import com.wuyou.server.entities.Customer;
import com.wuyou.server.entities.RegisterBody;
import com.wuyou.server.entities.TCustomer;
import com.wuyou.server.entities.UserToken;
import com.wuyou.server.util.BeanUtils;
import com.wuyou.server.util.PhoneNoUtils;
import com.wuyou.server.util.UUIDUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(path = "/customer")
public class CustomerEndPoint {

    @Autowired
    private CustomerRepository repository;

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


    @RequestMapping(value = "/captcha", method = RequestMethod.POST)
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
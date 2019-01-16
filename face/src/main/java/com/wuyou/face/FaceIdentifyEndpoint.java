package com.wuyou.face;

import com.wuyou.base.BaseResponse;
import com.wuyou.customer.entities.AuthData;
import com.wuyou.face.entities.AuthTokenResponse;
import com.wuyou.face.service.FaceAuthService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author hjn
 * @created 2019-01-15
 **/
@RestController
@RequestMapping(path = "/face")
public class FaceIdentifyEndpoint {
    @Autowired
    FaceAuthService authService;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public BaseResponse<?> generateAuthToken() {
        AuthTokenResponse token = authService.generateToken();
        return token == null ? new BaseResponse(HttpStatus.BAD_REQUEST) : new BaseResponse<>(token);
    }

    @RequestMapping(value = "/authInfo/{uid}", method = RequestMethod.GET)
    public BaseResponse getAuthInfo(@PathVariable ObjectId uid, @RequestParam String ticketId) {
        AuthData authInfo = authService.getAuthInfo(uid, ticketId);
        return authInfo != null ? new BaseResponse<>(authInfo) : new BaseResponse(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "property")
    public BaseResponse getFacePropertyInfo(@RequestParam String picturePath){

        return new BaseResponse(HttpStatus.OK);
    }



}

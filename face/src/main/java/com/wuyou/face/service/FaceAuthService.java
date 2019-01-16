package com.wuyou.face.service;

import com.wuyou.customer.entities.AuthData;
import com.wuyou.face.entities.AuthTokenResponse;
import com.wuyou.face.entities.FaceCompareData;
import org.bson.types.ObjectId;

/**
 * @author hjn
 * @created 2019-01-16
 **/
public interface FaceAuthService {

    AuthTokenResponse generateToken();

    AuthData getAuthInfo(ObjectId uid, String id);


    FaceCompareData faceComparation(String pictureUrl);

    

}

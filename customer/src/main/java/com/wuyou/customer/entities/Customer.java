package com.wuyou.customer.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class Customer implements WithId<ObjectId> {
    @Id
    private ObjectId id;

    @Indexed
    private Date registrationDate;

    private String avatar;

    private String name;

    private String idNo;

    @Indexed
    private String mobile;

    private String email;

    private String birthday;

    private String gender;

    private List<Address> address;

    private AuthData authData;

    private String rcToken;

    @PersistenceConstructor
    public Customer(ObjectId id, Date registrationDate, String mobile, String rcToken) {
        this.id = id;
        this.mobile = mobile;
        this.registrationDate = registrationDate;
        this.rcToken = rcToken;
    }

    public Customer() {

    }

    public String getRcToken() {
        return rcToken;
    }

    private void setRcToken(String rcToken) {
        this.rcToken = rcToken;
    }

    public ObjectId getId() {
        return id;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    private void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getMobile() {
        return mobile;
    }

    private void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }
}

package com.wuyou.face.entities;

/**
 * @author hjn
 * @created 2019-01-16
 **/
public class AuthTokenResponse {
    public String token;
    public String ticketId;
    public String message;

    public AuthTokenResponse(String token, String ticketId) {
        this.token = token;
        this.ticketId = ticketId;
    }
}

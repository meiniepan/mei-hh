package com.wuyou.face.entities;

/**
 * @author hjn
 * @created 2019-01-16
 **/
public class FaceCompareData {
    public int errno;
    public String err_msg;
    public String request_id;
    public float confidence;
    public float[] thresholds;
    public int[] rectA;
    public int[] rectB;
}

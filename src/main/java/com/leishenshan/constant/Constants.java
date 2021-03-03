package com.leishenshan.constant;

public class Constants {
    //初始感染数量
    public static int ORIGINAL_COUNT=50;
    //传播率，可通过戴口罩降低
    public static float BROAD_RATE = 0.2f;
    //潜伏时间
    public static float SHADOW_TIME = 140;
    //医院收治响应时间
    public static int HOSPITAL_RECEIVE_TIME=10;
    //医院床位
    public static int BED_COUNT=1000;
    //流动意向平均值，限制出行，减少人员流动
    public static float u=-0.99f;
}



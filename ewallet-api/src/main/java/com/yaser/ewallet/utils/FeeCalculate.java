package com.yaser.ewallet.utils;


public class FeeCalculate {
    public static Double feeCalculate(Double amount) {
        return amount * (0.001);
    }
}

//This value can be read from a file.
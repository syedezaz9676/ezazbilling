package com.ezaz.ezbilling.Util;


import org.springframework.stereotype.Service;

@Service
public class PercentageUtils {


    public float getPercentageAmount(float amount,float per){
        float percentageAmount = (amount*(per/100.0f));
        return percentageAmount;
    }
}

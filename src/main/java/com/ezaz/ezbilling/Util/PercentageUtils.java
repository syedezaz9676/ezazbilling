package com.ezaz.ezbilling.Util;


import org.springframework.stereotype.Service;

@Service
public class PercentageUtils {


    public Double getPercentageAmount(Double amount,float per){
        Double percentageAmount = (amount*(per/100));
        return percentageAmount;
    }
}

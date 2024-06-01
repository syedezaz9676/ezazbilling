package com.ezaz.ezbilling.Util;


import org.springframework.stereotype.Service;

@Service
public class PercentageUtils {


    public Double getPercentageAmount(Double amount,float per){
        Double percentageAmount = (amount/100)*per;
        return percentageAmount;
    }
}

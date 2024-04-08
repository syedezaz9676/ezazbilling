package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class sumOfGst {

    private Integer gst;
    private Double sumOfGstAmount;
    private String bno;
    private String billingDate;
}

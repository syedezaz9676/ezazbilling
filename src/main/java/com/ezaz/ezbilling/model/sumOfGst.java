package com.ezaz.ezbilling.model;


import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class sumOfGst {

    private Integer gst;
    private Double sumOfGstAmount;
    private String bno;
    private String billingDate;
    private Double sumOfCessAmount;
    private Double netAmount;
}

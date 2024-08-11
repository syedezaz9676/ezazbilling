package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "soldstock")
public class BillAggregationResult {

    private double totalAmount;
    private double totalCessAmount;
    private Integer product_gst;
    private String billingDate;
    private String bno;
    private double netAmount;
    private Integer cess;

}

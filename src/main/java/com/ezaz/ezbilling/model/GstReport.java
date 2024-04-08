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
public class GstReport {

    private String customerName;
    private String gstNo;
    private List<BillGstDetails> billGstDetails;
    private Double totalTaxable;
    private Double total;

}

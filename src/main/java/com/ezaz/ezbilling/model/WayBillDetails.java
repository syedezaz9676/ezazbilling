package com.ezaz.ezbilling.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WayBillDetails {

    private String hsn_code;
    private Double amountSum;
    private Integer product_gst;
    private Integer sgst;
    private Integer cgst;
    private Double  sgstAmt;
    private Double  cgstAmt;
    private Double gstAmountSum;
}

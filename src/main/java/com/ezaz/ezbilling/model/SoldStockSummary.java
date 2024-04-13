package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SoldStockSummary {

    private String hsn_code;
    private int product_gst;
    private int totalQty;
    private Double taxAmount;
    private double totalAmount;
    private double sgst;
    private double cgst;
    private double igst;

    public double getSgst() {
        return taxAmount/2;
    }

    public double getCgst(){
        return taxAmount/2;
    }
}

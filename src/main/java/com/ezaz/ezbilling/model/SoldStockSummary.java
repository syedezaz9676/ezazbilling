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
    private double taxAmount;
    private double totalAmount;
    private double taxableAmount;
    private double sgst;
    private double cgst;
    private double igst;
    private double totalCessAmount;
    private double totalNetAmount;

//    public double getTaxableAmount(){return (totalAmount/((product_gst)+100))*100;}

    public double getSgst() {
        return taxAmount/2;
    }

    public double getCgst(){
        return taxAmount/2;
    }

//    public double getTaxAmount(){return taxableAmount-((taxableAmount/(product_gst+100))*100);}
}

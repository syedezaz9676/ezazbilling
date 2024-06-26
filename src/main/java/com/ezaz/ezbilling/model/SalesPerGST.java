package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalesPerGST {
    private Integer productGst;
    private Double totalAmountAfterDisc;
    private Double taxableAmount;

    public Double getTaxableAmount() {
        return ((totalAmountAfterDisc/(productGst+100))*100);
    }
}

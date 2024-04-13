package com.ezaz.ezbilling.model;


import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillGstDetails {
    private List<sumOfGst> sumOfGsts;
    private Double totalTaxableSum;
    private Double totalofSum;
}

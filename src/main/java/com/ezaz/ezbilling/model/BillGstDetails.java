package com.ezaz.ezbilling.model;


import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillGstDetails {
    private String bno;
    private List<sumOfGst> sumOfGsts;
}

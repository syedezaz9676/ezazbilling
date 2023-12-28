package com.ezaz.ezbilling.model;


import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemList {
    private int noOfUnits;
    private String pname;
    private int rate;
}

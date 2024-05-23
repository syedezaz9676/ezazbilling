package com.ezaz.ezbilling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SumOfBillsAmount {
    private String bno;
    private String date;
    private double amount;
    private String cno;
    private String dgst;
}

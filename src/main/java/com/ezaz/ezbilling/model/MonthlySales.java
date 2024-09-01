package com.ezaz.ezbilling.model;

import lombok.*;

import javax.persistence.Id;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySales {
    private String id;
    private Double totalAmount;
}

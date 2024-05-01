package com.ezaz.ezbilling.model;


import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemList {
    private Integer noofunites;
    private String pname;
    private Double rate;
    private Integer disc;
}

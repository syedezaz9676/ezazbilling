package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "in_stock_itemss")
public class StockDetails {

    @Id
    private String _id;
    private String pid;
    private String pname;
    private Double in_stock_units;
    private String last_updated_date;
    private Double newStock;
    private String dgst;
    private String pcom;
}

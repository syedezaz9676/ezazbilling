package com.ezaz.ezbilling.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillDetails {

    private String name;
    private String userID;
    private String date;
    private String bno;
    private List<ItemList> itemList;

}



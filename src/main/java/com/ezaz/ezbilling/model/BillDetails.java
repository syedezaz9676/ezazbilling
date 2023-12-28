package com.ezaz.ezbilling.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BillDetails {

    private String name;
    private String userID;
    private LocalDateTime date;
    private List<ItemList> itemList;
}



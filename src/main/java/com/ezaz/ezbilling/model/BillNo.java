package com.ezaz.ezbilling.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "soldstock")
public class BillNo {
    private String bno;
}

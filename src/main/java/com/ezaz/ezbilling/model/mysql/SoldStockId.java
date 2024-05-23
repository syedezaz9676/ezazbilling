package com.ezaz.ezbilling.model.mysql;
import com.sun.xml.internal.ws.developer.Serialization;
import lombok.*;

import java.io.Serializable;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Serialization
public class SoldStockId implements Serializable {
    private Long cno;
    private String bno;

    // Constructors, getters, setters, equals, and hashCode methods

    // Implement Serializable interface
}
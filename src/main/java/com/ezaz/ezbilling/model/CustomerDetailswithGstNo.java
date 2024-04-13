package com.ezaz.ezbilling.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customers")
public class CustomerDetailswithGstNo {
    private String id;
    private String cno;
    private String cname;
    private String ctno;
    private String supplyplace;
    private String isigst;
}

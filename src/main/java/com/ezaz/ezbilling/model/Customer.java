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
public class Customer {
private int cno;
private String cname;
private String ctno;
private String cpno;
private String cadd;
private String isigst;
private String supplyplace;
private String legal_name;
}

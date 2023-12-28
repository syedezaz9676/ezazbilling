package com.ezaz.ezbilling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class ProductDetails {

    @Id
    private String id;
    private String pname;
    private Float mrp;
    private Float rate;
    private String pcom;
    private Integer vatp;
    private String Hsn_code;
    private String unites_per;
    private Integer no_of_unites;
    private String  is_sp;
    private String rel_prod;
    private String dgst;

}

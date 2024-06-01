package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
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
    private String hsn_code;
    private String unites_per;
    private Integer no_of_unites;
    private String  is_sp;
    private String rel_prod;
    private Integer cess;
    private String dgst;

}

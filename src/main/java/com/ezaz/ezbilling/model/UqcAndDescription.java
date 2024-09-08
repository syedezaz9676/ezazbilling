package com.ezaz.ezbilling.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "uqcanddescription")
public class UqcAndDescription {

    private String hsnCode;
    private String hsnDescription;
    private String uqc;

}

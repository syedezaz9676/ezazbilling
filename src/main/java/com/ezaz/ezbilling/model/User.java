package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")
@ToString
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private List<String> role;
    private String prefix;
    private String firmName;
    private String Address;
    private String gstNo;
    private String contact;
    private String state;
}

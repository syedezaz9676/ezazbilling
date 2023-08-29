package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")
@ToString
public class User {
    private String username;
    private String password;
    private List<String> role;
}

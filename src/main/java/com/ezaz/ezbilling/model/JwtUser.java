package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtUser {
    @Id
    private String id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> role;
    private String prefix;
    private String firmName;
    private String Address;
    private String gstNo;
    private String contact;
    private String state;
    private String vehicalNo;
}

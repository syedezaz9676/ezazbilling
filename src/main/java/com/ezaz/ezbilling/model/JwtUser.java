package com.ezaz.ezbilling.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtUser {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> role;
}

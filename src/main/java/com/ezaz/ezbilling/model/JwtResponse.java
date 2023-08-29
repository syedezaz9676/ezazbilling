package com.ezaz.ezbilling.model;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtResponse {
    private String token;
    private JwtUser user;
}

package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwkKeyDto {
    private String kty;
    private String e;
    private String use;
    private String kid;
    private String alg;
    private String n;
}

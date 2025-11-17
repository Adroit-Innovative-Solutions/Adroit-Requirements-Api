package com.dataquadinc.dtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAssignment {

    private String userId;
    private String userName;
    private Set<String> roles;
}

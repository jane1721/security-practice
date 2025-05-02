package com.jane.securitypractice.user.dto;

import com.jane.securitypractice.user.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private Set<Role> roles;
}

package com.jane.securitypractice.auth.dto;

import com.jane.securitypractice.user.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    private String username;
    private String password;
    private Role selectedRole;
}

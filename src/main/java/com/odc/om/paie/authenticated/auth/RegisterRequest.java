package com.odc.om.paie.authenticated.auth;

import com.odc.om.paie.authenticated.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String pin;
    private Role role;
    private String telephone;
    private boolean active;

}

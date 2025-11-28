package com.odc.om.paie.authenticated.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),
    EMPLOYE_READ("employe:read"),
    EMPLOYE_UPDATE("employe:update"),
    EMPLOYE_DELETE("employe:delete"),
    EMPLOYE_CREATE("employe:create");

    private final String permission;
}

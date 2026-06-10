package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.AccessLevel;
import com.study.management.domain.model.UserRole;

import java.util.UUID;

public class GrantAccessRequest {

    private UUID userId;
    private UserRole userRole;
    private AccessLevel accessLevel;

    public UUID getUserId()                         { return userId; }
    public void setUserId(UUID v)                  { this.userId = v; }
    public UserRole getUserRole()                   { return userRole; }
    public void setUserRole(UserRole v)            { this.userRole = v; }
    public AccessLevel getAccessLevel()             { return accessLevel; }
    public void setAccessLevel(AccessLevel v)      { this.accessLevel = v; }
}

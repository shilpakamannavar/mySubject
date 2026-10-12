package com.study.management.infrastructure.web.dto;

public class SyncDetail {

    private String username;
    private String email;
    private String role;
    private String action;   // CREATED | SKIPPED | ERROR
    private String entityId; // DB UUID when CREATED
    private String message;  // reason for SKIPPED or ERROR

    public SyncDetail() {}

    public String getUsername()         { return username; }
    public void setUsername(String v)   { this.username = v; }
    public String getEmail()            { return email; }
    public void setEmail(String v)      { this.email = v; }
    public String getRole()             { return role; }
    public void setRole(String v)       { this.role = v; }
    public String getAction()           { return action; }
    public void setAction(String v)     { this.action = v; }
    public String getEntityId()         { return entityId; }
    public void setEntityId(String v)   { this.entityId = v; }
    public String getMessage()          { return message; }
    public void setMessage(String v)    { this.message = v; }
}

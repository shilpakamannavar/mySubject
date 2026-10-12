package com.study.management.infrastructure.web.dto;

public class RoleSyncSummary {

    private int created;
    private int skipped;
    private int errors;

    public RoleSyncSummary() {}

    public RoleSyncSummary(int created, int skipped, int errors) {
        this.created = created;
        this.skipped = skipped;
        this.errors = errors;
    }

    public int getCreated()        { return created; }
    public void setCreated(int v)  { this.created = v; }
    public int getSkipped()        { return skipped; }
    public void setSkipped(int v)  { this.skipped = v; }
    public int getErrors()         { return errors; }
    public void setErrors(int v)   { this.errors = v; }
}

package com.study.management.infrastructure.web.dto;

public class ClinicalVisitTypeResponse {

    private String code;
    private String displayName;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;

    public String getCode()                   { return code; }
    public void setCode(String code)          { this.code = code; }

    public String getDisplayName()            { return displayName; }
    public void setDisplayName(String n)      { this.displayName = n; }

    public String getDescription()            { return description; }
    public void setDescription(String d)      { this.description = d; }

    public Integer getSortOrder()             { return sortOrder; }
    public void setSortOrder(Integer s)       { this.sortOrder = s; }

    public Boolean getIsActive()              { return isActive; }
    public void setIsActive(Boolean a)        { this.isActive = a; }
}

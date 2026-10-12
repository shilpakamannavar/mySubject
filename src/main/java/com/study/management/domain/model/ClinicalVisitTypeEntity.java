package com.study.management.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clinical_visit_types")
public class ClinicalVisitTypeEntity {

    @Id
    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public String getCode()                      { return code; }
    public void setCode(String code)             { this.code = code; }

    public String getDisplayName()               { return displayName; }
    public void setDisplayName(String n)         { this.displayName = n; }

    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }

    public Integer getSortOrder()                { return sortOrder; }
    public void setSortOrder(Integer s)          { this.sortOrder = s; }

    public Boolean getIsActive()                 { return isActive; }
    public void setIsActive(Boolean a)           { this.isActive = a; }
}

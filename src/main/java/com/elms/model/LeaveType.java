package com.elms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Leave category domain model (Casual, Sick, Paid, Maternity, etc.).
 */
public class LeaveType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String typeCode;
    private String typeName;
    private String description;
    private int defaultDaysPerYear;
    private boolean paid;
    private LocalDateTime createdAt;

    public LeaveType() {
        this.paid = true;
        this.defaultDaysPerYear = 12;
    }

    public LeaveType(Integer id, String typeCode, String typeName, String description, int defaultDaysPerYear, boolean paid) {
        this.id = id;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.description = description;
        this.defaultDaysPerYear = defaultDaysPerYear;
        this.paid = paid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDefaultDaysPerYear() {
        return defaultDaysPerYear;
    }

    public void setDefaultDaysPerYear(int defaultDaysPerYear) {
        this.defaultDaysPerYear = defaultDaysPerYear;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

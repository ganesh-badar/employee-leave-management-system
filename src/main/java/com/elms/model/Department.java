package com.elms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Department entity model.
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String departmentCode;
    private String departmentName;
    private String description;
    private int employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Department() {
    }

    public Department(Integer id, String departmentCode, String departmentName, String description) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

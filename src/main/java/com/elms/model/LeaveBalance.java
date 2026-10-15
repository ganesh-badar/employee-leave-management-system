package com.elms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LeaveBalance entity tracking quota, approved usage, and pending deductions per employee per year.
 */
public class LeaveBalance implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer employeeId;
    private Integer leaveTypeId;
    private String leaveTypeCode;
    private String leaveTypeName;
    private boolean isPaid;
    private int year;
    private double totalAllocated;
    private double usedDays;
    private double pendingDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LeaveBalance() {
        this.totalAllocated = 0.0;
        this.usedDays = 0.0;
        this.pendingDays = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveTypeCode() {
        return leaveTypeCode;
    }

    public void setLeaveTypeCode(String leaveTypeCode) {
        this.leaveTypeCode = leaveTypeCode;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getTotalAllocated() {
        return totalAllocated;
    }

    public void setTotalAllocated(double totalAllocated) {
        this.totalAllocated = totalAllocated;
    }

    public double getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(double usedDays) {
        this.usedDays = usedDays;
    }

    public double getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(double pendingDays) {
        this.pendingDays = pendingDays;
    }

    /**
     * Remaining unreserved days available for new leave applications.
     */
    public double getRemainingDays() {
        double rem = totalAllocated - usedDays - pendingDays;
        return rem > 0 ? rem : 0.0;
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

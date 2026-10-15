package com.elms.model;

import com.elms.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Audit log entry tracking administrative actions, approvals, and employee records.
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String entityType;
    private Integer entityId;
    private String action;
    private Integer performedById;
    private String performedByName;
    private String details;
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(String entityType, Integer entityId, String action, Integer performedById, String performedByName, String details) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.performedById = performedById;
        this.performedByName = performedByName;
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getPerformedById() {
        return performedById;
    }

    public void setPerformedById(Integer performedById) {
        this.performedById = performedById;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(String performedByName) {
        this.performedByName = performedByName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        return DateUtil.formatDateTime(this.createdAt);
    }
}

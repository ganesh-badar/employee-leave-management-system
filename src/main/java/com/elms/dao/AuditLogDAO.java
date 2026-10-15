package com.elms.dao;

import com.elms.model.AuditLog;

import java.sql.Connection;
import java.util.List;

public interface AuditLogDAO {
    int insert(AuditLog log, Connection conn);
    List<AuditLog> findRecent(int limit);
}

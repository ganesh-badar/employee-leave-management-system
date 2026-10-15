package com.elms.dao;

import com.elms.model.LeaveRequest;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestDAO {
    List<LeaveRequest> findAll(LeaveRequest.Status status, Integer departmentId, Integer employeeId);
    Optional<LeaveRequest> findById(int id);
    int insert(LeaveRequest request, Connection conn);
    boolean updateStatus(int id, LeaveRequest.Status status, String remarks, int reviewerId, Connection conn);
    boolean cancel(int id, int employeeId, Connection conn);
    List<LeaveRequest> findOverlappingApprovedLeaves(int departmentId, LocalDate start, LocalDate end, int excludeRequestId);
    int countByStatus(LeaveRequest.Status status);
    int countTotal();
}

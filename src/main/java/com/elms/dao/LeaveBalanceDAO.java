package com.elms.dao;

import com.elms.model.LeaveBalance;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceDAO {
    List<LeaveBalance> findByEmployeeAndYear(int employeeId, int year);
    Optional<LeaveBalance> findByEmployeeAndType(int employeeId, int leaveTypeId, int year, Connection conn);
    int insert(LeaveBalance balance, Connection conn);
    boolean addPendingDays(int employeeId, int leaveTypeId, int year, double days, Connection conn);
    boolean commitPendingToUsed(int employeeId, int leaveTypeId, int year, double days, Connection conn);
    boolean releasePendingDays(int employeeId, int leaveTypeId, int year, double days, Connection conn);
    boolean updateAllocation(int employeeId, int leaveTypeId, int year, double totalAllocated, Connection conn);
}

package com.elms;

import com.elms.model.LeaveBalance;
import com.elms.model.LeaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LeaveBusinessLogicTest {

    @Test
    @DisplayName("Should accurately evaluate unreserved remaining days on LeaveBalance entity")
    void testBalanceCalculation() {
        LeaveBalance balance = new LeaveBalance();
        balance.setTotalAllocated(12.0);
        balance.setUsedDays(3.0);
        balance.setPendingDays(2.0);

        // 12.0 - 3.0 - 2.0 = 7.0 days remaining
        assertEquals(7.0, balance.getRemainingDays(), 0.001);

        // If used + pending exceeds allocation, remaining should not drop below zero
        balance.setUsedDays(10.0);
        balance.setPendingDays(5.0);
        assertEquals(0.0, balance.getRemainingDays(), 0.001);
    }

    @Test
    @DisplayName("Should verify LeaveRequest status transitions and flags")
    void testLeaveRequestStatus() {
        LeaveRequest req = new LeaveRequest();
        req.setStatus(LeaveRequest.Status.PENDING);
        assertTrue(req.isPending());
        assertFalse(req.isApproved());

        req.setStatus(LeaveRequest.Status.APPROVED);
        assertTrue(req.isApproved());
        assertFalse(req.isPending());

        req.setStatus(LeaveRequest.Status.REJECTED);
        assertTrue(req.isRejected());

        req.setStatus(LeaveRequest.Status.CANCELLED);
        assertTrue(req.isCancelled());
    }
}

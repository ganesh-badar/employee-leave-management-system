<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "HR Operations Dashboard");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">HR Operations &amp; Leave Administration</h1>
        <div class="page-subtitle">Workforce availability, pending leave requests, and personnel activity overview</div>
    </div>
    <div class="page-actions">
        <a href="<%= request.getContextPath() %>/admin/employees/new" class="btn btn-secondary">
            <svg style="width:14px; height:14px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Add Employee
        </a>
        <a href="<%= request.getContextPath() %>/admin/leaves" class="btn btn-primary">
            Review Leave Queue
        </a>
    </div>
</div>

<!-- Real Operational Metrics Cards (No Fake Counters!) -->
<div class="metrics-grid">
    <div class="metric-card">
        <div class="metric-header">
            <span class="metric-label">Total Headcount</span>
            <svg class="metric-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
            </svg>
        </div>
        <div class="metric-value"><c:out value="${metrics.totalEmployees}"/></div>
        <div class="metric-footer">Personnel records in database</div>
    </div>

    <div class="metric-card">
        <div class="metric-header">
            <span class="metric-label">Active Workforce</span>
            <svg class="metric-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
        </div>
        <div class="metric-value" style="color:#059669;"><c:out value="${metrics.activeEmployees}"/></div>
        <div class="metric-footer">Eligible for attendance tracking</div>
    </div>

    <div class="metric-card">
        <div class="metric-header">
            <span class="metric-label">Pending Reviews</span>
            <svg class="metric-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
        </div>
        <div class="metric-value" style="color:#d97706;"><c:out value="${metrics.pendingLeaves}"/></div>
        <div class="metric-footer">Awaiting administrative decision</div>
    </div>

    <div class="metric-card">
        <div class="metric-header">
            <span class="metric-label">Approved YTD</span>
            <svg class="metric-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
            </svg>
        </div>
        <div class="metric-value" style="color:#2563eb;"><c:out value="${metrics.approvedLeaves}"/></div>
        <div class="metric-footer">Approved leave applications</div>
    </div>
</div>

<!-- Pending Leave Requests Table -->
<div class="card">
    <div class="card-header">
        <div>
            <h2 class="card-title">Pending Leave Requests Awaiting Action</h2>
            <div class="card-description">Review submitted leave requests and record approval/rejection remarks</div>
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/admin/leaves?status=PENDING" class="btn btn-secondary btn-sm">Full Leave Queue</a>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Ref #</th>
                    <th>Employee</th>
                    <th>Department</th>
                    <th>Category</th>
                    <th>Dates</th>
                    <th>Working Days</th>
                    <th>Reason</th>
                    <th style="text-align:right;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty pendingRequests}">
                        <tr>
                            <td colspan="8" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No pending leave requests currently awaiting administrative review.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${pendingRequests}" var="req" end="4">
                            <tr>
                                <td class="table-code">#<c:out value="${req.id}"/></td>
                                <td>
                                    <strong><c:out value="${req.employeeName}"/></strong>
                                    <div class="table-code" style="font-size:0.75rem;"><c:out value="${req.employeeCode}"/></div>
                                </td>
                                <td><c:out value="${req.departmentName}"/></td>
                                <td>
                                    <strong><c:out value="${req.leaveTypeName}"/></strong>
                                    <span style="font-size:0.75rem; color:var(--color-text-muted);">(<c:out value="${req.leaveTypeCode}"/>)</span>
                                </td>
                                <td>
                                    <div><c:out value="${req.formattedStartDate}"/> &rarr;</div>
                                    <div><c:out value="${req.formattedEndDate}"/></div>
                                </td>
                                <td><strong><c:out value="${req.totalDays}"/></strong></td>
                                <td style="max-width:200px;"><c:out value="${req.reason}"/></td>
                                <td style="text-align:right; white-space:nowrap;">
                                    <button type="button" class="btn btn-success btn-sm"
                                            data-modal-target="approveModal"
                                            data-request-id="${req.id}">
                                        Approve
                                    </button>
                                    <button type="button" class="btn btn-outline-danger btn-sm"
                                            data-modal-target="rejectModal"
                                            data-request-id="${req.id}">
                                        Reject
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Recent Administrative Audit Trail -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Recent System Audit Log</h2>
        <div class="card-description">Chronological ledger of leave decisions, profile modifications, and transactions</div>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Timestamp</th>
                    <th>Action</th>
                    <th>Entity</th>
                    <th>Operator</th>
                    <th>Details</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty recentLogs}">
                        <tr>
                            <td colspan="5" style="text-align:center; padding:24px; color:var(--color-text-muted);">
                                No audit records logged.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recentLogs}" var="log">
                            <tr>
                                <td style="white-space:nowrap; font-size:0.78rem; color:var(--color-text-muted);">
                                    <c:out value="${log.formattedCreatedAt}"/>
                                </td>
                                <td>
                                    <span class="badge" style="background:#f1f5f9; color:#1e293b; border-color:#cbd5e1;">
                                        <c:out value="${log.action}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:out value="${log.entityType}"/> #<c:out value="${log.entityId}"/>
                                </td>
                                <td><strong><c:out value="${log.performedByName}"/></strong></td>
                                <td style="font-size:0.8rem; color:var(--color-text-secondary); max-width:320px;">
                                    <c:out value="${log.details}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Approve Leave -->
<div id="approveModal" class="modal-backdrop">
    <div class="modal-dialog">
        <div class="modal-header">
            <h3 class="modal-title">Approve Leave Request #<span class="modal-request-id-display"></span></h3>
            <button type="button" class="modal-close" data-modal-close>&times;</button>
        </div>
        <form action="<%= request.getContextPath() %>/admin/leaves/approve" method="POST">
            <input type="hidden" name="requestId" value="">
            <div class="modal-body">
                <p style="font-size:0.87rem; color:var(--color-text-secondary); margin-bottom:14px;">
                    Approving this request will immediately deduct the requested working days from the employee's available leave balance.
                </p>
                <div class="form-group">
                    <label class="form-label" for="approveRemarks">Administrative Approval Remarks (Optional)</label>
                    <textarea id="approveRemarks" name="adminRemarks" class="form-control" rows="3"
                              placeholder="e.g. Approved. Workload handover confirmed."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-success">Confirm Approval</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Reject Leave -->
<div id="rejectModal" class="modal-backdrop">
    <div class="modal-dialog">
        <div class="modal-header">
            <h3 class="modal-title">Reject Leave Request #<span class="modal-request-id-display"></span></h3>
            <button type="button" class="modal-close" data-modal-close>&times;</button>
        </div>
        <form action="<%= request.getContextPath() %>/admin/leaves/reject" method="POST">
            <input type="hidden" name="requestId" value="">
            <div class="modal-body">
                <p style="font-size:0.87rem; color:var(--color-text-secondary); margin-bottom:14px;">
                    Rejecting this request will release all reserved pending days back to the employee's available quota. A reason must be provided.
                </p>
                <div class="form-group">
                    <label class="form-label" for="rejectRemarks">Rejection Reason / Notes <span class="required">*</span></label>
                    <textarea id="rejectRemarks" name="adminRemarks" class="form-control" rows="3"
                              placeholder="e.g. Critical release sprint scheduled; insufficient team coverage." required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-danger">Confirm Rejection</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

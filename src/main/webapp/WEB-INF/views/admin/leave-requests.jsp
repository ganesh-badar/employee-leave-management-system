<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Manage Leave Requests");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Workforce Leave Applications</h1>
        <div class="page-subtitle">Administrative queue for inspecting, evaluating, and determining leave requests</div>
    </div>
</div>

<!-- Filters Toolbar -->
<div class="card" style="margin-bottom: 20px;">
    <div class="card-body" style="padding: 14px 20px;">
        <form action="<%= request.getContextPath() %>/admin/leaves" method="GET" style="display:flex; gap:16px; align-items:center; flex-wrap:wrap;">
            <div style="display:flex; align-items:center; gap:8px;">
                <label for="status" style="font-weight:600; font-size:0.82rem; color:var(--color-navy-800);">Status:</label>
                <select id="status" name="status" class="form-control" style="width: auto; padding: 6px 10px;" onchange="this.form.submit()">
                    <option value="ALL" ${currentStatus == 'ALL' ? 'selected' : ''}>All Statuses</option>
                    <option value="PENDING" ${currentStatus == 'PENDING' ? 'selected' : ''}>Pending Review Only</option>
                    <option value="APPROVED" ${currentStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
                    <option value="REJECTED" ${currentStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                </select>
            </div>

            <div style="display:flex; align-items:center; gap:8px;">
                <label for="departmentId" style="font-weight:600; font-size:0.82rem; color:var(--color-navy-800);">Department:</label>
                <select id="departmentId" name="departmentId" class="form-control" style="width: auto; padding: 6px 10px;" onchange="this.form.submit()">
                    <option value="">All Departments</option>
                    <c:forEach items="${departments}" var="dept">
                        <option value="${dept.id}" ${currentDept == dept.id ? 'selected' : ''}>
                            <c:out value="${dept.departmentName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <c:if test="${currentStatus != 'ALL' || not empty currentDept}">
                <a href="<%= request.getContextPath() %>/admin/leaves" class="btn btn-secondary btn-sm">Clear Filters</a>
            </c:if>
        </form>
    </div>
</div>

<!-- Leave Requests Table Card -->
<div class="card">
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Ref #</th>
                    <th>Employee</th>
                    <th>Department</th>
                    <th>Category</th>
                    <th>Dates</th>
                    <th>Days</th>
                    <th>Reason &amp; Emergency Reach</th>
                    <th>Status / Alerts</th>
                    <th>Decision Remarks</th>
                    <th style="text-align:right;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty requests}">
                        <tr>
                            <td colspan="10" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No leave applications matching the selected criteria.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${requests}" var="req">
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
                                <td style="max-width:220px;">
                                    <div><c:out value="${req.reason}"/></div>
                                    <c:if test="${not empty req.emergencyContact}">
                                        <div style="font-size:0.75rem; color:var(--color-text-muted); margin-top:2px;">
                                            Reach: <c:out value="${req.emergencyContact}"/>
                                        </div>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${req.status == 'PENDING'}">
                                            <span class="badge badge-pending">Pending</span>
                                        </c:when>
                                        <c:when test="${req.status == 'APPROVED'}">
                                            <span class="badge badge-approved">Approved</span>
                                        </c:when>
                                        <c:when test="${req.status == 'REJECTED'}">
                                            <span class="badge badge-rejected">Rejected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-cancelled">Cancelled</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <!-- Overlap Conflict Warning Indicator -->
                                    <c:if test="${req.pending and not empty conflictsMap[req.id]}">
                                        <div style="margin-top:4px;">
                                            <span class="badge badge-conflict" title="Departmental leave clash detected">
                                                Overlapping: ${conflictsMap[req.id].size()} Colleague(s)
                                            </span>
                                        </div>
                                    </c:if>
                                </td>
                                <td style="font-size:0.8rem; color:var(--color-text-secondary); max-width:200px;">
                                    <c:choose>
                                        <c:when test="${not empty req.adminRemarks}">
                                            <div><c:out value="${req.adminRemarks}"/></div>
                                            <div style="font-size:0.7rem; color:var(--color-text-muted);">
                                                By: <c:out value="${req.reviewerName}"/> (<c:out value="${req.formattedReviewedAt}"/>)
                                            </div>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align:right; white-space:nowrap;">
                                    <c:if test="${req.status == 'PENDING'}">
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
                                    </c:if>
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

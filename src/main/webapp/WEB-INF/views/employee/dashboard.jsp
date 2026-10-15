<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Employee Dashboard");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Workforce Self-Service Dashboard</h1>
        <div class="page-subtitle">
            <c:out value="${employee.fullName}"/> &bull;
            <span class="table-code"><c:out value="${employee.employeeCode}"/></span> &bull;
            <c:out value="${employee.designation}"/> (<c:out value="${employee.departmentName != null ? employee.departmentName : 'General'}"/>)
        </div>
    </div>
    <div class="page-actions">
        <a href="<%= request.getContextPath() %>/leave/apply" class="btn btn-primary">
            <svg style="width:14px; height:14px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Apply for Leave
        </a>
    </div>
</div>

<!-- Leave Quotas Cards (Real Balances for Current Year) -->
<div style="margin-bottom: 8px; font-size: 0.82rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-navy-700);">
    Current Year Leave Quotas (<c:out value="${currentYear}"/>)
</div>

<div class="balance-cards-grid">
    <c:forEach items="${balances}" var="b">
        <div class="balance-card">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px;">
                <span class="balance-type-title"><c:out value="${b.leaveTypeName}"/></span>
                <span class="badge" style="background:#f1f5f9; color:#475569; font-size:0.68rem;"><c:out value="${b.leaveTypeCode}"/></span>
            </div>
            <div class="balance-stat-row">
                <span style="color:var(--color-text-muted);">Allocated:</span>
                <span style="font-weight:600;"><c:out value="${b.totalAllocated}"/> days</span>
            </div>
            <div class="balance-stat-row">
                <span style="color:var(--color-text-muted);">Consumed:</span>
                <span style="font-weight:600; color:#dc2626;"><c:out value="${b.usedDays}"/> days</span>
            </div>
            <div class="balance-stat-row">
                <span style="color:var(--color-text-muted);">Pending Review:</span>
                <span style="font-weight:600; color:#d97706;"><c:out value="${b.pendingDays}"/> days</span>
            </div>
            <div class="balance-remaining-highlight">
                <span style="font-size:0.8rem; font-weight:600; color:var(--color-navy-800);">Available:</span>
                <span class="balance-remaining-val"><c:out value="${b.remainingDays}"/></span>
            </div>
        </div>
    </c:forEach>
</div>

<!-- Recent Applications Table -->
<div class="card">
    <div class="card-header">
        <div>
            <h2 class="card-title">Recent Leave Applications</h2>
            <div class="card-description">History of absence submissions and approval statuses</div>
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/leave/my-leaves" class="btn btn-secondary btn-sm">View All History</a>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Ref #</th>
                    <th>Category</th>
                    <th>From</th>
                    <th>To</th>
                    <th>Working Days</th>
                    <th>Status</th>
                    <th>Review Remarks</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty leaves}">
                        <tr>
                            <td colspan="8" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No leave applications submitted yet. Click "Apply for Leave" above to create your first request.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${leaves}" var="req" end="4">
                            <tr>
                                <td class="table-code">#<c:out value="${req.id}"/></td>
                                <td>
                                    <strong><c:out value="${req.leaveTypeName}"/></strong>
                                    <span style="font-size:0.75rem; color:var(--color-text-muted);">(<c:out value="${req.leaveTypeCode}"/>)</span>
                                </td>
                                <td><c:out value="${req.formattedStartDate}"/></td>
                                <td><c:out value="${req.formattedEndDate}"/></td>
                                <td><strong><c:out value="${req.totalDays}"/></strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${req.status == 'PENDING'}">
                                            <span class="badge badge-pending">Pending Review</span>
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
                                </td>
                                <td style="font-size:0.8rem; color:var(--color-text-secondary); max-width:240px;">
                                    <c:out value="${req.adminRemarks != null ? req.adminRemarks : '-'}"/>
                                </td>
                                <td>
                                    <c:if test="${req.status == 'PENDING'}">
                                        <form action="<%= request.getContextPath() %>/leave/cancel" method="POST" style="display:inline;"
                                              data-confirm="Are you sure you want to cancel this pending leave application?">
                                            <input type="hidden" name="requestId" value="${req.id}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm">Cancel</button>
                                        </form>
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

<jsp:include page="../common/footer.jsp"/>

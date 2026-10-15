<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "My Leaves & Balances");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">My Leaves &amp; Annual Quotas</h1>
        <div class="page-subtitle">Personal attendance ledger, active leave allowances, and request tracking</div>
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

<!-- Annual Quotas Table Card -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Year-to-Date Leave Entitlement</h2>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Leave Category</th>
                    <th>Code</th>
                    <th>Type</th>
                    <th>Total Allocated</th>
                    <th>Used Days</th>
                    <th>Pending Review</th>
                    <th>Remaining Unreserved</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${balances}" var="b">
                    <tr>
                        <td><strong><c:out value="${b.leaveTypeName}"/></strong></td>
                        <td class="table-code"><c:out value="${b.leaveTypeCode}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${b.paid}">
                                    <span class="badge badge-approved" style="font-size:0.65rem;">Paid Leave</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-cancelled" style="font-size:0.65rem;">Unpaid / LOP</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><c:out value="${b.totalAllocated}"/> days</td>
                        <td style="color:#dc2626; font-weight:600;"><c:out value="${b.usedDays}"/> days</td>
                        <td style="color:#d97706; font-weight:600;"><c:out value="${b.pendingDays}"/> days</td>
                        <td style="font-weight:700; color:var(--color-primary); font-size:0.95rem;">
                            <c:out value="${b.remainingDays}"/> days
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Leave Requests History Card -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">Leave History &amp; Applications</h2>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Ref #</th>
                    <th>Category</th>
                    <th>Duration</th>
                    <th>Business Days</th>
                    <th>Reason</th>
                    <th>Emergency Contact</th>
                    <th>Status</th>
                    <th>HR / Admin Remarks</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty requests}">
                        <tr>
                            <td colspan="9" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No leave applications on record.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${requests}" var="req">
                            <tr>
                                <td class="table-code">#<c:out value="${req.id}"/></td>
                                <td>
                                    <strong><c:out value="${req.leaveTypeName}"/></strong>
                                    <span style="font-size:0.75rem; color:var(--color-text-muted);">(<c:out value="${req.leaveTypeCode}"/>)</span>
                                </td>
                                <td>
                                    <div><c:out value="${req.formattedStartDate}"/> &rarr;</div>
                                    <div><c:out value="${req.formattedEndDate}"/></div>
                                </td>
                                <td><strong><c:out value="${req.totalDays}"/></strong></td>
                                <td style="max-width:200px;">
                                    <c:out value="${req.reason}"/>
                                </td>
                                <td style="font-size:0.8rem; color:var(--color-text-muted);">
                                    <c:out value="${req.emergencyContact != null ? req.emergencyContact : '-'}"/>
                                </td>
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
                                <td style="font-size:0.8rem; color:var(--color-text-secondary); max-width:200px;">
                                    <c:choose>
                                        <c:when test="${not empty req.adminRemarks}">
                                            <div><c:out value="${req.adminRemarks}"/></div>
                                            <div style="font-size:0.7rem; color:var(--color-text-muted);">
                                                By: <c:out value="${req.reviewerName}"/> on <c:out value="${req.formattedReviewedAt}"/>
                                            </div>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${req.status == 'PENDING'}">
                                        <form action="<%= request.getContextPath() %>/leave/cancel" method="POST"
                                              data-confirm="Are you sure you wish to cancel leave request #${req.id}?">
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

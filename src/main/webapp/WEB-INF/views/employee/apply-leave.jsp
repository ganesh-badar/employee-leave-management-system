<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Apply for Leave");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Submit Leave Application</h1>
        <div class="page-subtitle">Request formal authorization for scheduled or medical absence</div>
    </div>
    <div class="page-actions">
        <a href="<%= request.getContextPath() %>/leave/my-leaves" class="btn btn-secondary">
            View My Applications
        </a>
    </div>
</div>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
    <!-- Application Form Card -->
    <div class="card">
        <div class="card-header">
            <div>
                <h2 class="card-title">Leave Request Form</h2>
                <div class="card-description">All business days will be calculated automatically, omitting corporate weekends</div>
            </div>
        </div>

        <form action="<%= request.getContextPath() %>/leave/apply" method="POST">
            <div class="card-body">
                <div class="form-group">
                    <label class="form-label" for="leaveTypeId">Leave Category <span class="required">*</span></label>
                    <select id="leaveTypeId" name="leaveTypeId" class="form-control" required>
                        <option value="">-- Select Leave Category --</option>
                        <c:forEach items="${types}" var="t">
                            <c:set var="avail" value="0.0"/>
                            <c:forEach items="${balances}" var="b">
                                <c:if test="${b.leaveTypeId == t.id}">
                                    <c:set var="avail" value="${b.remainingDays}"/>
                                </c:if>
                            </c:forEach>
                            <option value="${t.id}" ${form_leaveTypeId == t.id ? 'selected' : ''}>
                                <c:out value="${t.typeName}"/> (<c:out value="${t.typeCode}"/>) &mdash; Available Quota: <c:out value="${avail}"/> days
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="startDate">Start Date <span class="required">*</span></label>
                        <input type="date" id="startDate" name="startDate" class="form-control"
                               value="<c:out value="${form_startDate}"/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="endDate">End Date <span class="required">*</span></label>
                        <input type="date" id="endDate" name="endDate" class="form-control"
                               value="<c:out value="${form_endDate}"/>" required>
                    </div>
                </div>

                <!-- Dynamic Calculation Banner -->
                <div style="margin-bottom: 16px; padding: 12px 16px; background-color: var(--color-surface-subtle); border: 1px solid var(--color-border); border-radius: var(--radius-sm); display: flex; align-items: center; justify-content: space-between;">
                    <span style="font-size: 0.85rem; color: var(--color-text-secondary); font-weight: 500;">Calculated Duration:</span>
                    <span id="calculatedDaysDisplay" style="font-size: 0.95rem; font-weight: 700; color: var(--color-primary);">0 working days</span>
                </div>

                <div class="form-group">
                    <label class="form-label" for="reason">Detailed Business Reason / Purpose <span class="required">*</span></label>
                    <textarea id="reason" name="reason" class="form-control" rows="3"
                              placeholder="Describe the operational or personal reason for this requested absence..." required><c:out value="${form_reason}"/></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label" for="emergencyContact">Emergency Contact / Handover Details</label>
                    <input type="text" id="emergencyContact" name="emergencyContact" class="form-control"
                           value="<c:out value="${form_emergencyContact}"/>"
                           placeholder="Phone number, backup colleague, or emergency reachability during absence">
                </div>
            </div>

            <div class="card-footer">
                <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">
                    Submit Request for Review
                </button>
            </div>
        </form>
    </div>

    <!-- Quota Sidebar Reference -->
    <div>
        <div class="card">
            <div class="card-header">
                <h3 class="card-title" style="font-size: 0.95rem;">Current Quota Summary</h3>
            </div>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Category</th>
                            <th style="text-align:right;">Available</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${balances}" var="b">
                            <tr>
                                <td>
                                    <strong><c:out value="${b.leaveTypeName}"/></strong>
                                    <div style="font-size:0.72rem; color:var(--color-text-muted);">Allocated: <c:out value="${b.totalAllocated}"/>d</div>
                                </td>
                                <td style="text-align:right; font-weight:700; color:var(--color-primary);">
                                    <c:out value="${b.remainingDays}"/> d
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card" style="background-color: var(--color-surface-subtle);">
            <div class="card-body" style="font-size: 0.8rem; color: var(--color-text-secondary); line-height: 1.6;">
                <div style="font-weight: 700; color: var(--color-navy-900); margin-bottom: 6px;">Leave Policy Notes:</div>
                <ul style="margin-left: 16px;">
                    <li>Corporate working days exclude Saturdays and Sundays.</li>
                    <li>Planned Paid Leaves (PL) must be submitted 5 business days in advance.</li>
                    <li>Sick leave (SL) exceeding 3 days may require medical certification.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

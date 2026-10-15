<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    boolean isNew = Boolean.TRUE.equals(request.getAttribute("isNew"));
    request.setAttribute("pageTitle", isNew ? "Add Employee" : "Edit Employee");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title"><c:out value="${isNew ? 'Create New Employee Record' : 'Modify Employee Record & Quotas'}"/></h1>
        <div class="page-subtitle">
            <c:choose>
                <c:when test="${isNew}">Register workforce personnel and provision initial leave balances</c:when>
                <c:otherwise>Personnel Code: <span class="table-code"><strong><c:out value="${employee.employeeCode}"/></strong></span> &bull; <c:out value="${employee.email}"/></c:otherwise>
            </c:choose>
        </div>
    </div>
    <div class="page-actions">
        <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">
            &larr; Back to Directory
        </a>
    </div>
</div>

<div style="display: grid; grid-template-columns: ${isNew ? '1fr' : '2fr 1fr'}; gap: 24px;">
    <!-- Employee Form Card -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Personnel Profile Information</h2>
        </div>

        <form action="<%= request.getContextPath() %>/admin/employees/${isNew ? 'new' : 'edit'}" method="POST">
            <c:if test="${!isNew}">
                <input type="hidden" name="id" value="${employee.id}">
            </c:if>

            <div class="card-body">
                <c:if test="${isNew}">
                    <div class="form-grid" style="margin-bottom:16px; padding-bottom:16px; border-bottom:1px solid var(--color-border);">
                        <div class="form-group">
                            <label class="form-label" for="username">Portal Username <span class="required">*</span></label>
                            <input type="text" id="username" name="username" class="form-control"
                                   value="<c:out value="${form_username}"/>" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="email">Corporate Email <span class="required">*</span></label>
                            <input type="email" id="email" name="email" class="form-control"
                                   value="<c:out value="${form_email}"/>" required>
                        </div>
                        <div class="form-group full-width">
                            <label class="form-label" for="password">Initial Password <span class="required">*</span></label>
                            <input type="password" id="password" name="password" class="form-control"
                                   placeholder="Minimum 6 characters" minlength="6" required>
                        </div>
                    </div>
                </c:if>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="firstName">First Name <span class="required">*</span></label>
                        <input type="text" id="firstName" name="firstName" class="form-control"
                               value="<c:out value="${isNew ? form_firstName : employee.firstName}"/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="lastName">Last Name <span class="required">*</span></label>
                        <input type="text" id="lastName" name="lastName" class="form-control"
                               value="<c:out value="${isNew ? form_lastName : employee.lastName}"/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="departmentId">Department <span class="required">*</span></label>
                        <select id="departmentId" name="departmentId" class="form-control" required>
                            <option value="">-- Select Department --</option>
                            <c:forEach items="${departments}" var="dept">
                                <option value="${dept.id}" ${(isNew ? form_departmentId == dept.id : employee.departmentId == dept.id) ? 'selected' : ''}>
                                    <c:out value="${dept.departmentName}"/> (<c:out value="${dept.departmentCode}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="designation">Job Title / Designation <span class="required">*</span></label>
                        <input type="text" id="designation" name="designation" class="form-control"
                               value="<c:out value="${isNew ? form_designation : employee.designation}"/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="phone">Phone Contact</label>
                        <input type="text" id="phone" name="phone" class="form-control"
                               value="<c:out value="${isNew ? form_phone : employee.phone}"/>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="joiningDate">Joining Date <span class="required">*</span></label>
                        <input type="date" id="joiningDate" name="joiningDate" class="form-control"
                               value="<c:out value="${isNew ? form_joiningDate : employee.joiningDate}"/>" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="salary">Base Salary ($)</label>
                        <input type="number" step="0.01" id="salary" name="salary" class="form-control"
                               value="<c:out value="${isNew ? (form_salary != null ? form_salary : '0.00') : employee.salary}"/>">
                    </div>

                    <c:if test="${!isNew}">
                        <div class="form-group">
                            <label class="form-label" for="status">Personnel Status <span class="required">*</span></label>
                            <select id="status" name="status" class="form-control" required>
                                <option value="ACTIVE" ${employee.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="INACTIVE" ${employee.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                                <option value="ON_LEAVE" ${employee.status == 'ON_LEAVE' ? 'selected' : ''}>ON_LEAVE</option>
                                <option value="TERMINATED" ${employee.status == 'TERMINATED' ? 'selected' : ''}>TERMINATED</option>
                            </select>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="card-footer">
                <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">
                    <c:out value="${isNew ? 'Create Employee Record' : 'Save Changes'}"/>
                </button>
            </div>
        </form>
    </div>

    <!-- Edit Quotas Card (Only for existing employees) -->
    <c:if test="${!isNew}">
        <div>
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title" style="font-size: 0.95rem;">Annual Quota Adjustments</h3>
                </div>
                <div class="card-body" style="padding: 16px;">
                    <p style="font-size:0.78rem; color:var(--color-text-muted); margin-bottom:12px;">
                        Adjust authorized days allocated to this employee for year ${LocalDate.now().getYear()}.
                    </p>

                    <c:forEach items="${balances}" var="b">
                        <form action="<%= request.getContextPath() %>/admin/employees/quota" method="POST"
                              style="display:flex; align-items:center; justify-content:space-between; gap:10px; margin-bottom:12px; padding-bottom:8px; border-bottom:1px solid var(--color-border);">
                            <input type="hidden" name="employeeId" value="${employee.id}">
                            <input type="hidden" name="leaveTypeId" value="${b.leaveTypeId}">
                            <input type="hidden" name="year" value="${b.year}">

                            <div style="flex:1;">
                                <div style="font-weight:600; font-size:0.82rem;"><c:out value="${b.leaveTypeName}"/></div>
                                <div style="font-size:0.72rem; color:var(--color-text-muted);">
                                    Used: <c:out value="${b.usedDays}"/>d &bull; Pending: <c:out value="${b.pendingDays}"/>d
                                </div>
                            </div>

                            <div style="display:flex; align-items:center; gap:6px;">
                                <input type="number" step="0.5" name="quota" value="${b.totalAllocated}"
                                       class="form-control" style="width:70px; padding:4px 6px; font-size:0.8rem; text-align:right;" required>
                                <button type="submit" class="btn btn-secondary btn-sm" title="Save quota">Set</button>
                            </div>
                        </form>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp"/>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Employee Directory");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Employee Directory</h1>
        <div class="page-subtitle">Master human resources record, organizational placement, and personnel status</div>
    </div>
    <div class="page-actions">
        <a href="<%= request.getContextPath() %>/admin/employees/new" class="btn btn-primary">
            <svg style="width:14px; height:14px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Add New Employee
        </a>
    </div>
</div>

<!-- Search & Filtering Toolbar -->
<div class="card" style="margin-bottom: 20px;">
    <div class="card-body" style="padding: 14px 20px;">
        <form action="<%= request.getContextPath() %>/admin/employees" method="GET" style="display:flex; gap:16px; align-items:center; flex-wrap:wrap;">
            <div style="flex:1; min-width:200px;">
                <input type="text" name="search" class="form-control" style="padding: 6px 10px;"
                       placeholder="Search by name, employee code, or email..."
                       value="<c:out value="${currentSearch}"/>">
            </div>

            <div style="display:flex; align-items:center; gap:8px;">
                <label for="departmentId" style="font-weight:600; font-size:0.82rem; color:var(--color-navy-800);">Department:</label>
                <select id="departmentId" name="departmentId" class="form-control" style="width: auto; padding: 6px 10px;">
                    <option value="">All Departments</option>
                    <c:forEach items="${departments}" var="dept">
                        <option value="${dept.id}" ${currentDept == dept.id ? 'selected' : ''}>
                            <c:out value="${dept.departmentName}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div style="display:flex; align-items:center; gap:8px;">
                <label for="status" style="font-weight:600; font-size:0.82rem; color:var(--color-navy-800);">Status:</label>
                <select id="status" name="status" class="form-control" style="width: auto; padding: 6px 10px;">
                    <option value="ALL" ${currentStatus == 'ALL' ? 'selected' : ''}>All Personnel</option>
                    <option value="ACTIVE" ${currentStatus == 'ACTIVE' ? 'selected' : ''}>Active Only</option>
                    <option value="INACTIVE" ${currentStatus == 'INACTIVE' ? 'selected' : ''}>Inactive Only</option>
                </select>
            </div>

            <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
            <c:if test="${not empty currentSearch || not empty currentDept || currentStatus != 'ALL'}">
                <a href="<%= request.getContextPath() %>/admin/employees" class="btn btn-secondary btn-sm">Reset</a>
            </c:if>
        </form>
    </div>
</div>

<!-- Employees Table Card -->
<div class="card">
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Emp Code</th>
                    <th>Full Name</th>
                    <th>Corporate Email</th>
                    <th>Department</th>
                    <th>Designation</th>
                    <th>Joining Date</th>
                    <th>Status</th>
                    <th style="text-align:right;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty employees}">
                        <tr>
                            <td colspan="8" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No employees found matching the specified filters.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${employees}" var="emp">
                            <tr>
                                <td class="table-code"><strong><c:out value="${emp.employeeCode}"/></strong></td>
                                <td>
                                    <strong><c:out value="${emp.fullName}"/></strong>
                                    <c:if test="${not empty emp.phone}">
                                        <div style="font-size:0.75rem; color:var(--color-text-muted);"><c:out value="${emp.phone}"/></div>
                                    </c:if>
                                </td>
                                <td><c:out value="${emp.email}"/></td>
                                <td>
                                    <c:out value="${emp.departmentName != null ? emp.departmentName : 'Unassigned'}"/>
                                </td>
                                <td><c:out value="${emp.designation}"/></td>
                                <td><c:out value="${emp.joiningDate}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${emp.status == 'ACTIVE'}">
                                            <span class="badge badge-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-inactive"><c:out value="${emp.status}"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align:right; white-space:nowrap;">
                                    <a href="<%= request.getContextPath() %>/admin/employees/edit?id=${emp.id}" class="btn btn-secondary btn-sm">
                                        Edit / Quotas
                                    </a>
                                    <form action="<%= request.getContextPath() %>/admin/employees/status" method="POST" style="display:inline;"
                                          data-confirm="Toggle status for employee ${emp.employeeCode}?">
                                        <input type="hidden" name="id" value="${emp.id}">
                                        <c:choose>
                                            <c:when test="${emp.status == 'ACTIVE'}">
                                                <input type="hidden" name="status" value="INACTIVE">
                                                <button type="submit" class="btn btn-outline-danger btn-sm">Deactivate</button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="status" value="ACTIVE">
                                                <button type="submit" class="btn btn-secondary btn-sm" style="color:#059669; border-color:#a7f3d0;">Activate</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
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

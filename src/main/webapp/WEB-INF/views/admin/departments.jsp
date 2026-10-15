<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Corporate Departments");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Corporate Departments</h1>
        <div class="page-subtitle">Organizational divisions, functional team allocations, and staff distribution</div>
    </div>
    <div class="page-actions">
        <button type="button" class="btn btn-primary" data-modal-target="addDeptModal">
            <svg style="width:14px; height:14px;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Add Department
        </button>
    </div>
</div>

<div class="card">
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Dept Code</th>
                    <th>Department Name</th>
                    <th>Description</th>
                    <th>Active Personnel</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty departments}">
                        <tr>
                            <td colspan="4" style="text-align:center; padding:32px; color:var(--color-text-muted);">
                                No departments registered.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${departments}" var="dept">
                            <tr>
                                <td class="table-code"><strong><c:out value="${dept.departmentCode}"/></strong></td>
                                <td><strong><c:out value="${dept.departmentName}"/></strong></td>
                                <td style="color:var(--color-text-secondary); max-width:360px;">
                                    <c:out value="${dept.description}"/>
                                </td>
                                <td>
                                    <span class="badge" style="background:#eff6ff; color:#1e40af; border-color:#bfdbfe;">
                                        <c:out value="${dept.employeeCount}"/> Staff Members
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal: Add Department -->
<div id="addDeptModal" class="modal-backdrop">
    <div class="modal-dialog">
        <div class="modal-header">
            <h3 class="modal-title">Create Corporate Department</h3>
            <button type="button" class="modal-close" data-modal-close>&times;</button>
        </div>
        <form action="<%= request.getContextPath() %>/admin/departments" method="POST">
            <input type="hidden" name="action" value="create">
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="departmentCode">Department Code <span class="required">*</span></label>
                    <input type="text" id="departmentCode" name="departmentCode" class="form-control"
                           placeholder="e.g. SEC, LEGAL, FIN" required>
                    <div class="form-hint">Unique 2-10 character uppercase code</div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="departmentName">Department Name <span class="required">*</span></label>
                    <input type="text" id="departmentName" name="departmentName" class="form-control"
                           placeholder="e.g. Information Security & Compliance" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="description">Functional Scope &amp; Responsibilities</label>
                    <textarea id="description" name="description" class="form-control" rows="3"
                              placeholder="Describe organizational responsibilities..."></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Create Department</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Personnel Profile");
%>
<jsp:include page="../common/header.jsp"/>
<jsp:include page="../common/sidebar.jsp"/>

<div class="page-header">
    <div>
        <h1 class="page-title">Personnel Profile &amp; Credentials</h1>
        <div class="page-subtitle">Personal records, contact points, and authentication security</div>
    </div>
</div>

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
    <!-- Profile Information Card -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Employee Details</h2>
        </div>
        <div class="card-body">
            <div style="display:flex; flex-direction:column; gap:14px; font-size:0.87rem;">
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Employee Code:</span>
                    <span class="table-code" style="font-weight:700;"><c:out value="${employee != null ? employee.employeeCode : 'ADMIN-ROOT'}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Full Name:</span>
                    <span style="font-weight:600;"><c:out value="${employee != null ? employee.fullName : user.username}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Corporate Email:</span>
                    <span style="font-weight:500;"><c:out value="${user.email}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Department:</span>
                    <span style="font-weight:600;"><c:out value="${employee != null && employee.departmentName != null ? employee.departmentName : 'Human Resources Operations'}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Job Title / Designation:</span>
                    <span style="font-weight:500;"><c:out value="${employee != null ? employee.designation : 'System Administrator'}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; border-bottom:1px solid var(--color-border); padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Date of Joining:</span>
                    <span style="font-weight:500;"><c:out value="${employee != null ? employee.joiningDate : '2022-01-15'}"/></span>
                </div>
                <div style="display:flex; justify-content:space-between; padding-bottom:8px;">
                    <span style="color:var(--color-text-muted);">Role Authorization:</span>
                    <span class="badge badge-approved"><c:out value="${user.role}"/></span>
                </div>
            </div>

            <c:if test="${employee != null}">
                <hr style="margin:20px 0; border:none; border-top:1px solid var(--color-border);">
                <form action="<%= request.getContextPath() %>/profile" method="POST">
                    <input type="hidden" name="action" value="updateContact">
                    <div class="form-group">
                        <label class="form-label" for="phone">Update Contact Phone</label>
                        <input type="text" id="phone" name="phone" class="form-control"
                               value="<c:out value="${employee.phone}"/>" placeholder="+1-555-0199">
                    </div>
                    <button type="submit" class="btn btn-secondary btn-sm">Save Contact Details</button>
                </form>
            </c:if>
        </div>
    </div>

    <!-- Security & Password Card -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Account Security</h2>
            <div class="card-description">Update portal login password (minimum 6 characters)</div>
        </div>
        <form action="<%= request.getContextPath() %>/profile" method="POST">
            <input type="hidden" name="action" value="changePassword">
            <div class="card-body">
                <div class="form-group">
                    <label class="form-label" for="oldPassword">Current Password <span class="required">*</span></label>
                    <input type="password" id="oldPassword" name="oldPassword" class="form-control" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="newPassword">New Password <span class="required">*</span></label>
                    <input type="password" id="newPassword" name="newPassword" class="form-control" minlength="6" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm New Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" minlength="6" required>
                </div>
            </div>
            <div class="card-footer">
                <button type="submit" class="btn btn-primary">Update Password</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>

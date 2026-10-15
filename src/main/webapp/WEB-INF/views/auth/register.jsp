<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Registration | Enterprise Leave Management</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="alternate icon" href="<%= cp %>/favicon.ico">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body class="auth-page">

<div class="auth-box" style="max-width: 600px;">
    <div class="auth-header">
        <h1 class="auth-title">Employee Portal Registration</h1>
        <p class="auth-subtitle">Create your corporate personnel account to manage leave and attendance</p>
    </div>

    <div class="auth-body">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form action="<%= cp %>/register" method="POST">
            <div class="form-grid">
                <div class="form-group">
                    <label class="form-label" for="username">Username <span class="required">*</span></label>
                    <input type="text" id="username" name="username" class="form-control"
                           value="<c:out value="${form_username}"/>"
                           placeholder="e.g. robert.smith" required>
                    <div class="form-hint">3-50 alphanumeric characters</div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Corporate Email <span class="required">*</span></label>
                    <input type="email" id="email" name="email" class="form-control"
                           value="<c:out value="${form_email}"/>"
                           placeholder="robert.smith@internal.corp" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="firstName">First Name <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" class="form-control"
                           value="<c:out value="${form_firstName}"/>" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="lastName">Last Name <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" class="form-control"
                           value="<c:out value="${form_lastName}"/>" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Minimum 6 characters" minlength="6" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password <span class="required">*</span></label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                           placeholder="Re-enter password" minlength="6" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="departmentId">Department <span class="required">*</span></label>
                    <select id="departmentId" name="departmentId" class="form-control" required>
                        <option value="">-- Select Corporate Department --</option>
                        <c:forEach items="${departments}" var="dept">
                            <option value="${dept.id}" ${form_departmentId == dept.id ? 'selected' : ''}>
                                <c:out value="${dept.departmentName}"/> (<c:out value="${dept.departmentCode}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="designation">Job Title / Designation <span class="required">*</span></label>
                    <input type="text" id="designation" name="designation" class="form-control"
                           value="<c:out value="${form_designation}"/>"
                           placeholder="e.g. Software Engineer" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="phone">Phone Contact</label>
                    <input type="text" id="phone" name="phone" class="form-control"
                           value="<c:out value="${form_phone}"/>"
                           placeholder="+1-555-0123">
                </div>

                <div class="form-group">
                    <label class="form-label" for="joiningDate">Joining Date <span class="required">*</span></label>
                    <input type="date" id="joiningDate" name="joiningDate" class="form-control"
                           value="<c:out value="${form_joiningDate}"/>" required>
                </div>
            </div>

            <div style="margin-top: 24px;">
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 10px;">
                    Register Corporate Account
                </button>
            </div>
        </form>
    </div>

    <div class="auth-footer">
        Already registered? <a href="<%= cp %>/login" style="font-weight: 600;">Sign in to existing account</a>
    </div>
</div>

<div style="margin-top: 20px; font-size: 0.75rem; color: #64748b; text-align: center;">
    <a href="<%= cp %>/privacy-policy" style="color: #94a3b8;">Privacy Policy</a>
    <span style="margin: 0 8px;">&bull;</span>
    <a href="<%= cp %>/terms-and-conditions" style="color: #94a3b8;">Terms &amp; Conditions</a>
</div>

</body>
</html>

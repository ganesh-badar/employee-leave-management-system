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
    <title>Sign In | Enterprise Leave Management System</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="alternate icon" href="<%= cp %>/favicon.ico">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body class="auth-page">

<div class="auth-box">
    <div class="auth-header">
        <svg style="width:36px; height:36px; margin-bottom:8px;" viewBox="0 0 64 64" fill="none">
            <rect width="64" height="64" rx="8" fill="#0f172a"/>
            <rect x="14" y="16" width="36" height="34" rx="4" fill="#ffffff"/>
            <path d="M14 20 C14 17.8 15.8 16 18 16 L46 16 C48.2 16 50 17.8 50 20 L50 25 L14 25 Z" fill="#2563eb"/>
            <rect x="22" y="12" width="4" height="7" rx="2" fill="#0f172a"/>
            <rect x="38" y="12" width="4" height="7" rx="2" fill="#0f172a"/>
            <circle cx="44" cy="44" r="11" fill="#10b981"/>
            <path d="M39 44 L42.5 47.5 L49 41" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h1 class="auth-title">Personnel Portal Sign In</h1>
        <p class="auth-subtitle">Enterprise Human Resources &amp; Attendance Operations</p>
    </div>

    <div class="auth-body">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <c:if test="${param.logout == 'true'}">
            <div class="alert alert-info">
                You have been securely signed out.
            </div>
        </c:if>

        <form action="<%= cp %>/login" method="POST">
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="<c:out value="${param.redirect}"/>">
            </c:if>

            <div class="form-group">
                <label class="form-label" for="usernameOrEmail">Username or Corporate Email <span class="required">*</span></label>
                <input type="text" id="usernameOrEmail" name="usernameOrEmail" class="form-control"
                       value="<c:out value="${usernameOrEmail != null ? usernameOrEmail : ''}"/>"
                       placeholder="e.g. admin or john.doe@internal.corp" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password <span class="required">*</span></label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter account password" required>
            </div>

            <div style="margin-top: 20px;">
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 10px;">
                    Authenticate Session
                </button>
            </div>
        </form>

        <!-- System Demonstration Credentials Panel -->
        <div style="margin-top: 24px; padding: 12px; background-color: var(--color-surface-subtle); border: 1px solid var(--color-border); border-radius: var(--radius-sm); font-size: 0.78rem;">
            <div style="font-weight: 700; color: var(--color-navy-800); margin-bottom: 6px;">Available Demonstration Logins:</div>
            <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                <span style="color: var(--color-text-muted);">Administrator:</span>
                <span style="font-family: var(--font-mono); font-weight: 600;">admin / Admin@123</span>
            </div>
            <div style="display: flex; justify-content: space-between;">
                <span style="color: var(--color-text-muted);">Employee:</span>
                <span style="font-family: var(--font-mono); font-weight: 600;">john.doe / Employee@123</span>
            </div>
        </div>
    </div>

    <div class="auth-footer">
        New personnel? <a href="<%= cp %>/register" style="font-weight: 600;">Register employee account</a>
    </div>
</div>

<div style="margin-top: 20px; font-size: 0.75rem; color: #64748b; text-align: center;">
    <a href="<%= cp %>/privacy-policy" style="color: #94a3b8;">Privacy Policy</a>
    <span style="margin: 0 8px;">&bull;</span>
    <a href="<%= cp %>/terms-and-conditions" style="color: #94a3b8;">Terms &amp; Conditions</a>
</div>

</body>
</html>

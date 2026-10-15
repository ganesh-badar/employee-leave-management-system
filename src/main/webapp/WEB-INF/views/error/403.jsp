<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>403 - Access Forbidden | ELMS</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body style="min-height:100vh; display:flex; align-items:center; justify-content:center; background:var(--color-bg);">
    <div style="max-width:480px; width:100%; background:#ffffff; border:1px solid var(--color-border); border-radius:var(--radius-md); padding:32px; text-align:center; box-shadow:var(--shadow-md);">
        <div style="font-size:3rem; font-weight:800; color:#dc2626; line-height:1; margin-bottom:12px;">403</div>
        <h1 style="font-size:1.25rem; font-weight:700; color:var(--color-navy-900); margin-bottom:8px;">Access Restricted</h1>
        <p style="font-size:0.87rem; color:var(--color-text-secondary); margin-bottom:24px;">
            You do not possess the required administrative clearance to access this portal section.
        </p>
        <a href="<%= cp %>/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>

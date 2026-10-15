<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - Resource Not Found | ELMS</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body style="min-height:100vh; display:flex; align-items:center; justify-content:center; background:var(--color-bg);">
    <div style="max-width:480px; width:100%; background:#ffffff; border:1px solid var(--color-border); border-radius:var(--radius-md); padding:32px; text-align:center; box-shadow:var(--shadow-md);">
        <div style="font-size:3rem; font-weight:800; color:var(--color-navy-700); line-height:1; margin-bottom:12px;">404</div>
        <h1 style="font-size:1.25rem; font-weight:700; color:var(--color-navy-900); margin-bottom:8px;">Resource Not Found</h1>
        <p style="font-size:0.87rem; color:var(--color-text-secondary); margin-bottom:24px;">
            The requested personnel record, application reference, or endpoint does not exist.
        </p>
        <a href="<%= cp %>/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>

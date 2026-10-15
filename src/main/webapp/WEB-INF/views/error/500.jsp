<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - System Exception | ELMS</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body style="min-height:100vh; display:flex; align-items:center; justify-content:center; background:var(--color-bg);">
    <div style="max-width:540px; width:100%; background:#ffffff; border:1px solid var(--color-border); border-radius:var(--radius-md); padding:32px; text-align:center; box-shadow:var(--shadow-md);">
        <div style="font-size:3rem; font-weight:800; color:#dc2626; line-height:1; margin-bottom:12px;">500</div>
        <h1 style="font-size:1.25rem; font-weight:700; color:var(--color-navy-900); margin-bottom:8px;">Internal Processing Error</h1>
        <p style="font-size:0.87rem; color:var(--color-text-secondary); margin-bottom:16px;">
            The server encountered an unexpected error processing this transaction. The incident has been noted in the server logs.
        </p>
        <p style="font-family:var(--font-mono); font-size:0.75rem; background:var(--color-surface-subtle); padding:10px; border-radius:var(--radius-sm); color:#dc2626; margin-bottom:24px; word-break:break-word;">
            <%= exception != null ? exception.getMessage() : (request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "Internal database or servlet runtime exception") %>
        </p>
        <a href="<%= cp %>/dashboard" class="btn btn-primary">Return to Dashboard</a>
    </div>
</body>
</html>

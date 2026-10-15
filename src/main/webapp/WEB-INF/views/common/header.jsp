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
    <title><c:out value="${pageTitle != null ? pageTitle : 'ELMS'}"/> | Enterprise Leave Management</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="alternate icon" href="<%= cp %>/favicon.ico">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body>

<header class="app-header">
    <div class="brand-wrapper">
        <a href="<%= cp %>/dashboard" style="display:flex; align-items:center; gap:10px; color:#ffffff; text-decoration:none;">
            <svg class="brand-logo" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="64" height="64" rx="8" fill="#1e293b"/>
                <rect x="14" y="16" width="36" height="34" rx="4" fill="#ffffff"/>
                <path d="M14 20 C14 17.8 15.8 16 18 16 L46 16 C48.2 16 50 17.8 50 20 L50 25 L14 25 Z" fill="#2563eb"/>
                <rect x="22" y="12" width="4" height="7" rx="2" fill="#0f172a"/>
                <rect x="38" y="12" width="4" height="7" rx="2" fill="#0f172a"/>
                <circle cx="44" cy="44" r="11" fill="#10b981"/>
                <path d="M39 44 L42.5 47.5 L49 41" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="brand-title">
                ELMS <span class="brand-badge">HR PORTAL</span>
            </span>
        </a>
    </div>

    <div class="header-actions">
        <span class="domain-pill" title="Configured Custom Host Domain">Host: leave.internal.corp</span>

        <c:if test="${not empty sessionScope.SESSION_USER}">
            <div class="user-nav-dropdown">
                <div class="user-meta">
                    <div class="user-name"><c:out value="${sessionScope.SESSION_USER.username}"/></div>
                    <div class="user-role">
                        <c:choose>
                            <c:when test="${sessionScope.SESSION_USER.role == 'ROLE_ADMIN'}">HR Operations Administrator</c:when>
                            <c:otherwise>Employee</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <a href="<%= cp %>/profile" class="btn btn-secondary btn-sm" style="border-color:#475569; color:#f8fafc; background:transparent;">Profile</a>
                <a href="<%= cp %>/logout" class="btn btn-outline-danger btn-sm" style="background:transparent; border-color:#ef4444; color:#ef4444;">Sign Out</a>
            </div>
        </c:if>
    </div>
</header>

<div class="app-container">

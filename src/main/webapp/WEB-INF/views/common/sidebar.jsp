<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String cp = request.getContextPath();
    String currentPath = request.getServletPath();
%>
<aside class="app-sidebar">
    <c:choose>
        <c:when test="${sessionScope.SESSION_USER.role == 'ROLE_ADMIN'}">
            <!-- Administrative Navigation -->
            <div class="sidebar-section-title">Operations</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="<%= cp %>/dashboard" class="sidebar-link <%= "/dashboard".equals(currentPath) ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <rect x="3" y="3" width="7" height="7"></rect>
                            <rect x="14" y="3" width="7" height="7"></rect>
                            <rect x="14" y="14" width="7" height="7"></rect>
                            <rect x="3" y="14" width="7" height="7"></rect>
                        </svg>
                        Dashboard
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/admin/leaves" class="sidebar-link <%= currentPath.startsWith("/admin/leaves") ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        Leave Requests
                    </a>
                </li>
            </ul>

            <div class="sidebar-section-title">Organization</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="<%= cp %>/admin/employees" class="sidebar-link <%= currentPath.startsWith("/admin/employees") ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                        </svg>
                        Employee Directory
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/admin/departments" class="sidebar-link <%= currentPath.startsWith("/admin/departments") ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M3 21h18"></path>
                            <path d="M5 21V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16"></path>
                            <line x1="9" y1="9" x2="9.01" y2="9"></line>
                            <line x1="15" y1="9" x2="15.01" y2="9"></line>
                            <line x1="9" y1="13" x2="9.01" y2="13"></line>
                            <line x1="15" y1="13" x2="15.01" y2="13"></line>
                        </svg>
                        Departments
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/profile" class="sidebar-link <%= currentPath.startsWith("/profile") ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        Administrator Profile
                    </a>
                </li>
            </ul>
        </c:when>

        <c:otherwise>
            <!-- Employee Self-Service Navigation -->
            <div class="sidebar-section-title">My Workspace</div>
            <ul class="sidebar-nav">
                <li>
                    <a href="<%= cp %>/dashboard" class="sidebar-link <%= "/dashboard".equals(currentPath) ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <rect x="3" y="3" width="7" height="7"></rect>
                            <rect x="14" y="3" width="7" height="7"></rect>
                            <rect x="14" y="14" width="7" height="7"></rect>
                            <rect x="3" y="14" width="7" height="7"></rect>
                        </svg>
                        My Overview
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/leave/apply" class="sidebar-link <%= "/leave/apply".equals(currentPath) ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="16"></line>
                            <line x1="8" y1="12" x2="16" y2="12"></line>
                        </svg>
                        Apply for Leave
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/leave/my-leaves" class="sidebar-link <%= "/leave/my-leaves".equals(currentPath) ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                            <line x1="16" y1="13" x2="8" y2="13"></line>
                            <line x1="16" y1="17" x2="8" y2="17"></line>
                            <polyline points="10 9 9 9 8 9"></polyline>
                        </svg>
                        Leave History &amp; Quotas
                    </a>
                </li>
                <li>
                    <a href="<%= cp %>/profile" class="sidebar-link <%= currentPath.startsWith("/profile") ? "active" : "" %>">
                        <svg class="sidebar-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        Employee Profile
                    </a>
                </li>
            </ul>
        </c:otherwise>
    </c:choose>

    <div style="margin-top:auto; padding:16px 20px; border-top:1px solid var(--color-border); font-size:0.75rem; color:var(--color-text-muted);">
        <div style="font-weight:600; color:var(--color-navy-800);">Internal HR Portal</div>
        <div>Version 1.0.0 Production</div>
    </div>
</aside>

<main class="app-main">
    <!-- Flash Notifications -->
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success alert-dismissible">
            <svg style="width:16px; height:16px; flex-shrink:0;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <div><c:out value="${sessionScope.flashSuccess}"/></div>
        </div>
        <% session.removeAttribute("flashSuccess"); %>
    </c:if>

    <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-danger alert-dismissible">
            <svg style="width:16px; height:16px; flex-shrink:0;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <div><c:out value="${sessionScope.flashError}"/></div>
        </div>
        <% session.removeAttribute("flashError"); %>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible">
            <svg style="width:16px; height:16px; flex-shrink:0;" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <div><c:out value="${errorMessage}"/></div>
        </div>
    </c:if>

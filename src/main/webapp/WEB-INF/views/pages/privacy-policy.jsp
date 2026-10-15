<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Privacy Policy | Enterprise Leave Management System</title>
    <link rel="icon" type="image/svg+xml" href="<%= cp %>/favicon.svg">
    <link rel="alternate icon" href="<%= cp %>/favicon.ico">
    <link rel="stylesheet" href="<%= cp %>/static/css/main.css">
</head>
<body style="background-color: var(--color-bg);">

<header class="app-header">
    <div class="brand-wrapper">
        <a href="<%= cp %>/dashboard" style="display:flex; align-items:center; gap:10px; color:#ffffff; text-decoration:none;">
            <svg class="brand-logo" viewBox="0 0 64 64" fill="none">
                <rect width="64" height="64" rx="8" fill="#1e293b"/>
                <rect x="14" y="16" width="36" height="34" rx="4" fill="#ffffff"/>
                <path d="M14 20 C14 17.8 15.8 16 18 16 L46 16 C48.2 16 50 17.8 50 20 L50 25 L14 25 Z" fill="#2563eb"/>
                <circle cx="44" cy="44" r="11" fill="#10b981"/>
                <path d="M39 44 L42.5 47.5 L49 41" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="brand-title">ELMS <span class="brand-badge">HR PORTAL</span></span>
        </a>
    </div>
    <div>
        <a href="<%= cp %>/dashboard" class="btn btn-secondary btn-sm" style="color:#ffffff; border-color:#475569; background:transparent;">Return to Portal</a>
    </div>
</header>

<div class="policy-container">
    <h1 class="policy-title">Corporate Employee Data Privacy Policy</h1>
    <div class="policy-meta">
        Document Identifier: ELMS-POL-PRIV-2026-V1 &bull; Effective Date: January 1, 2026 &bull; Classification: Internal Corporate Policy
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">1. Operational Scope &amp; Purpose</h2>
        <p class="policy-text">
            This Policy sets out the standards governing how personal workforce data, medical absence records, emergency reachability details, and attendance metrics are collected, processed, and safeguarded within the Employee Leave Management System (ELMS).
        </p>
        <p class="policy-text">
            ELMS operates strictly as an internal Human Resources management utility intended for authorized enterprise workforce personnel and designated HR Operations staff.
        </p>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">2. Data Elements Processed</h2>
        <p class="policy-text">The system collects and stores only the minimum operational records necessary to administer statutory and contractual leave entitlements:</p>
        <ul class="policy-list">
            <li><strong>Personnel Identifiers:</strong> Employee Code, legal given name, corporate email address, organizational department, designation, and hire date.</li>
            <li><strong>Authentication Records:</strong> Username and salted one-way cryptographic password hashes (BCrypt algorithm with cost factor 10).</li>
            <li><strong>Leave Applications:</strong> Absence category (e.g. Casual, Sick, Annual, Bereavement), requested date ranges, business justification, and designated emergency contact numbers.</li>
            <li><strong>Administrative Audit Trails:</strong> Timestamped records of managerial approvals, rejections, supervisory remarks, and leave balance adjustments.</li>
        </ul>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">3. Data Usage &amp; Access Controls</h2>
        <p class="policy-text">
            Access to employee records is strictly restricted according to Role-Based Access Control (RBAC):
        </p>
        <ul class="policy-list">
            <li><strong>Employees</strong> have access only to their own profile, assigned leave balances, and historical application submissions.</li>
            <li><strong>HR Administrators</strong> have access to organizational employee listings, leave approval queues, and departmental conflict monitoring tools.</li>
            <li>No employee data is shared with external commercial entities, advertisers, or third-party marketing services.</li>
        </ul>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">4. Security Measures &amp; Data Protection</h2>
        <p class="policy-text">
            ELMS implements multi-layer defense in depth:
        </p>
        <ul class="policy-list">
            <li>Session confidentiality using HttpOnly and SameSite cookie policies with 30-minute idle session termination.</li>
            <li>Parameterized SQL queries (PreparedStatements) to eliminate SQL injection risks across all DAO components.</li>
            <li>Context-aware HTML output escaping to prevent Cross-Site Scripting (XSS).</li>
            <li>Immutable audit logging tracking every state modification on leave requests and employee profiles.</li>
        </ul>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">5. Data Retention &amp; Rights of Rectification</h2>
        <p class="policy-text">
            Leave ledgers and attendance data are maintained during active employment and retained for a statutory audit window of seven (7) years post-termination, as mandated by corporate labor compliance standards.
        </p>
        <p class="policy-text">
            Personnel may review their records at any time via the Employee Profile page or request clerical corrections through their designated HR Business Partner.
        </p>
    </div>

    <div style="margin-top: 32px; padding-top: 20px; border-top: 1px solid var(--color-border); display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Inquiries: hr-compliance@internal.corp</span>
        <a href="<%= cp %>/terms-and-conditions" class="btn btn-secondary btn-sm">Read Terms &amp; Conditions</a>
    </div>
</div>

<footer class="app-footer" style="background:#ffffff; border-top:1px solid #e2e8f0;">
    <div>&copy; 2026 Enterprise Leave Management System. Internal HR Portal.</div>
    <div class="footer-compliance-links">
        <a href="<%= cp %>/privacy-policy">Privacy Policy</a>
        <span>&bull;</span>
        <a href="<%= cp %>/terms-and-conditions">Terms &amp; Conditions</a>
    </div>
</footer>

</body>
</html>

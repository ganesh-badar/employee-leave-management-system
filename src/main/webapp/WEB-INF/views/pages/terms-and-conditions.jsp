<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Terms &amp; Conditions | Enterprise Leave Management System</title>
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
    <h1 class="policy-title">Terms of Service &amp; Leave Administration Rules</h1>
    <div class="policy-meta">
        Document Identifier: ELMS-TOS-2026-V1 &bull; Effective Date: January 1, 2026 &bull; Classification: Enterprise Policy Guidelines
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">1. Authorized Access &amp; Credential Security</h2>
        <p class="policy-text">
            Access to this system is restricted solely to authenticated corporate personnel. Users are strictly responsible for maintaining the confidentiality of their portal credentials.
        </p>
        <p class="policy-text">
            Sharing user accounts, attempting unauthorized escalation of administrative privileges, or fabricating absence entries constitutes a direct violation of corporate employment policies.
        </p>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">2. Submission Protocol &amp; Notice Periods</h2>
        <p class="policy-text">
            Employees must submit absence requests with reasonable advance notice, corresponding to the requested leave category:
        </p>
        <ul class="policy-list">
            <li><strong>Privilege / Annual Vacation (PL):</strong> Must be submitted at least five (5) business days prior to scheduled departure.</li>
            <li><strong>Casual Leave (CL):</strong> Should be applied at least 24 hours in advance for planned personal commitments.</li>
            <li><strong>Sick / Medical Leave (SL):</strong> Must be entered on or before the first day of return to duty. Absences exceeding three (3) consecutive business days require formal medical attestation submitted to HR Operations.</li>
        </ul>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">3. Quota Integrity &amp; Working Day Calculations</h2>
        <p class="policy-text">
            Leave consumption is evaluated strictly on working business days (Monday through Friday, excluding statutory enterprise holidays). Weekends (Saturday and Sunday) do not count against accrued leave balances.
        </p>
        <p class="policy-text">
            Requests submitted beyond available paid balances will default to Loss of Pay (LOP) classification, subject to direct departmental approval.
        </p>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">4. Managerial Review &amp; Operational Conflicts</h2>
        <p class="policy-text">
            Submission of an application does not guarantee authorization. HR and departmental directors hold discretion to balance workforce coverage, mission-critical deadlines, and overlapping colleague leaves.
        </p>
        <p class="policy-text">
            When an application is rejected, an administrative justification is recorded into the audit trail and made visible to the applicant.
        </p>
    </div>

    <div class="policy-section">
        <h2 class="policy-section-title">5. Cancellation Protocol</h2>
        <p class="policy-text">
            Employees may cancel pending leave applications at any time prior to administrative evaluation, immediately returning reserved quota to their active balance. Approved leaves requiring revocation must be coordinated directly through HR Operations.
        </p>
    </div>

    <div style="margin-top: 32px; padding-top: 20px; border-top: 1px solid var(--color-border); display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 0.8rem; color: var(--color-text-muted);">Compliance Contact: hr-operations@internal.corp</span>
        <a href="<%= cp %>/privacy-policy" class="btn btn-secondary btn-sm">Read Privacy Policy</a>
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

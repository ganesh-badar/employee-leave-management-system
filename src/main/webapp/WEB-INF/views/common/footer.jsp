<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String cp = request.getContextPath();
%>
</main>
</div><!-- /.app-container -->

<footer class="app-footer">
    <div>
        &copy; 2026 Enterprise Leave Management System (ELMS). All corporate rights reserved. Internal Human Resources &amp; Personnel Operations.
    </div>
    <div class="footer-compliance-links">
        <a href="<%= cp %>/privacy-policy">Privacy Policy</a>
        <span>&bull;</span>
        <a href="<%= cp %>/terms-and-conditions">Terms &amp; Conditions</a>
        <span>&bull;</span>
        <a href="mailto:hr-operations@internal.corp">HR Support Desk</a>
    </div>
</footer>

<script src="<%= cp %>/static/js/main.js"></script>
</body>
</html>

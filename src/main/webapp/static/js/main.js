/**
 * Employee Leave Management System (ELMS)
 * Enterprise Client-Side Logic: Form Validation, Modal Dialogs, and Working Days Calculation
 * NO Cursor Animations. NO Bloat.
 */

(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        initLeaveCalculator();
        initModals();
        initStatusConfirmations();
        initAutoDismissAlerts();
    });

    /**
     * Dynamically calculates business days between start date and end date
     * excluding weekends (Saturday = 6, Sunday = 0).
     */
    function initLeaveCalculator() {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const daysDisplay = document.getElementById('calculatedDaysDisplay');
        const daysField = document.getElementById('totalDaysField');

        if (!startDateInput || !endDateInput || !daysDisplay) return;

        function updateDays() {
            const startVal = startDateInput.value;
            const endVal = endDateInput.value;

            if (!startVal || !endVal) {
                daysDisplay.textContent = '0 working days';
                if (daysField) daysField.value = '0';
                return;
            }

            const start = new Date(startVal + 'T00:00:00');
            const end = new Date(endVal + 'T00:00:00');

            if (end < start) {
                daysDisplay.textContent = 'End date cannot precede start date';
                daysDisplay.classList.add('text-danger');
                if (daysField) daysField.value = '0';
                return;
            }

            daysDisplay.classList.remove('text-danger');

            let count = 0;
            const cur = new Date(start);
            while (cur <= end) {
                const dayOfWeek = cur.getDay(); // 0 = Sunday, 6 = Saturday
                if (dayOfWeek !== 0 && dayOfWeek !== 6) {
                    count++;
                }
                cur.setDate(cur.getDate() + 1);
            }

            daysDisplay.textContent = count + ' working day' + (count === 1 ? '' : 's') + ' (weekends excluded)';
            if (daysField) daysField.value = count;
        }

        startDateInput.addEventListener('change', updateDays);
        endDateInput.addEventListener('change', updateDays);

        // Run once on load if values exist
        updateDays();
    }

    /**
     * Enterprise Modal Dialog Handler for Admin Approval/Rejection remarks
     */
    function initModals() {
        const modalBackdrops = document.querySelectorAll('.modal-backdrop');
        const modalOpenBtns = document.querySelectorAll('[data-modal-target]');
        const modalCloseBtns = document.querySelectorAll('[data-modal-close]');

        modalOpenBtns.forEach(function (btn) {
            btn.addEventListener('click', function () {
                const targetId = btn.getAttribute('data-modal-target');
                const modal = document.getElementById(targetId);
                if (modal) {
                    // Populate request ID into modal if supplied
                    const reqId = btn.getAttribute('data-request-id');
                    if (reqId) {
                        const inputReqId = modal.querySelector('input[name="requestId"]');
                        if (inputReqId) inputReqId.value = reqId;
                        const labelReqId = modal.querySelector('.modal-request-id-display');
                        if (labelReqId) labelReqId.textContent = reqId;
                    }
                    modal.classList.add('is-open');
                }
            });
        });

        modalCloseBtns.forEach(function (btn) {
            btn.addEventListener('click', function () {
                modalBackdrops.forEach(function (m) {
                    m.classList.remove('is-open');
                });
            });
        });

        // Close on escape
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                modalBackdrops.forEach(function (m) {
                    m.classList.remove('is-open');
                });
            }
        });
    }

    /**
     * Confirmation dialog for destructive or state-transition actions
     */
    function initStatusConfirmations() {
        const confirmForms = document.querySelectorAll('form[data-confirm]');
        confirmForms.forEach(function (form) {
            form.addEventListener('submit', function (e) {
                const msg = form.getAttribute('data-confirm');
                if (!window.confirm(msg || 'Are you sure you wish to proceed?')) {
                    e.preventDefault();
                }
            });
        });
    }

    /**
     * Auto-dismiss temporary alerts after 6 seconds
     */
    function initAutoDismissAlerts() {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function (alert) {
            setTimeout(function () {
                alert.style.transition = 'opacity 0.3s ease';
                alert.style.opacity = '0';
                setTimeout(function () {
                    if (alert.parentNode) alert.parentNode.removeChild(alert);
                }, 300);
            }, 6000);
        });
    }

})();

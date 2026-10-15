package com.elms;

import com.elms.exception.ValidationException;
import com.elms.util.DateUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    @DisplayName("Should omit Saturday and Sunday from business day count")
    void testBusinessDaysCalculation() {
        // Monday 2026-10-19 to Friday 2026-10-23 => 5 working days
        LocalDate monday = LocalDate.of(2026, 10, 19);
        LocalDate friday = LocalDate.of(2026, 10, 23);
        double days1 = DateUtil.calculateBusinessDays(monday, friday);
        assertEquals(5.0, days1, 0.001);

        // Friday 2026-10-23 to Monday 2026-10-26 (spans weekend) => 2 working days (Fri, Mon)
        LocalDate nextMonday = LocalDate.of(2026, 10, 26);
        double days2 = DateUtil.calculateBusinessDays(friday, nextMonday);
        assertEquals(2.0, days2, 0.001);

        // Saturday to Sunday => 0 working days
        LocalDate saturday = LocalDate.of(2026, 10, 24);
        LocalDate sunday = LocalDate.of(2026, 10, 25);
        double days3 = DateUtil.calculateBusinessDays(saturday, sunday);
        assertEquals(0.0, days3, 0.001);
    }

    @Test
    @DisplayName("Should throw ValidationException when end date precedes start date")
    void testEndDateBeforeStart() {
        LocalDate start = LocalDate.of(2026, 10, 20);
        LocalDate end = LocalDate.of(2026, 10, 15);
        assertThrows(ValidationException.class, () -> DateUtil.calculateBusinessDays(start, end));
    }
}

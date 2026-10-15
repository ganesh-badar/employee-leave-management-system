package com.elms.util;

import com.elms.exception.ValidationException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Enterprise date arithmetic and validation utilities.
 * Accurately calculates working days, excluding corporate weekends (Saturday/Sunday).
 */
public final class DateUtil {

    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter DISPLAY_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {
        // utility class
    }

    public static LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " is required.");
        }
        try {
            return LocalDate.parse(dateStr.trim(), ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName, fieldName + " must match format YYYY-MM-DD.");
        }
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE) : "-";
    }

    public static String formatIsoDate(LocalDate date) {
        return date != null ? date.format(ISO_DATE) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME) : "-";
    }

    /**
     * Calculates the number of working business days between start and end (inclusive),
     * omitting Saturday and Sunday.
     */
    public static double calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0.0;
        }
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("endDate", "End date cannot precede the start date.");
        }

        double count = 0.0;
        LocalDate cur = startDate;
        while (!cur.isAfter(endDate)) {
            DayOfWeek day = cur.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                count += 1.0;
            }
            cur = cur.plusDays(1);
        }
        return count;
    }

    public static void validateDateRange(LocalDate start, LocalDate end, boolean allowPast) {
        if (start == null || end == null) {
            throw new ValidationException("Dates cannot be null.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("endDate", "End date must be on or after start date.");
        }
        if (!allowPast && start.isBefore(LocalDate.now())) {
            throw new ValidationException("startDate", "Leave start date cannot be in the past.");
        }
    }
}

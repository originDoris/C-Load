package com.cl.framework.plugin.jooq.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * DateValidator
 *
 * @author xhz
 */
public class DateValidator {
    private static final List<DateTimeFormatter> dateFormatters = new ArrayList<>();

    static {
        // Add different date formats as needed
        dateFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dateFormatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        dateFormatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        dateFormatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        dateFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateFormatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        dateFormatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dateFormatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dateFormatters.add(DateTimeFormatter.ISO_LOCAL_DATE);
        dateFormatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static boolean isValidDate(String dateString) {
        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                LocalDate.parse(dateString, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Continue checking next format
            }

            try {
                LocalDateTime.parse(dateString, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Continue checking next format
            }
        }
        return false;
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            } catch (DateTimeParseException e) {
                // Continue checking next format
            }
        }
        // If no formatter could parse the datetime, return null or throw an exception
        return null;
    }
}

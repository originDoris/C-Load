package com.cl.framework.core.utils;

import cn.hutool.crypto.SmUtil;
import com.cl.framework.core.funcation.CubeFn;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author xhz
 * @Description:
 */
public class DateUtil {
    public static LocalDateTime parseDefault(String date){
       return parse(date, "yyyy-MM-dd HH:mm:ss");
    }



    public static LocalDate parseLocalDate(String date, String formatterSql) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterSql);
        // 将字符串转换为LocalDateTime对象
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime parse(String date, String formatterSql) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterSql);
        // 将字符串转换为LocalDateTime对象
        return LocalDateTime.parse(date, formatter);
    }

    public static String parse(LocalTime date, String formatterSql) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterSql);
        return date.format(formatter);

    }

    public static String parseToString(LocalDateTime localDateTime, String formatterSql) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterSql);
        return localDateTime.format(formatter);
    }

    public static String parseToString(LocalDateTime localDateTime) {
        return parseToString(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime getNow(){
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        return LocalDateTime.ofInstant(new Date().toInstant(),zoneId);
    }

    public static Long getNowSecond(){
        ZoneId zoneId = ZoneId.systemDefault();
        return getNow().atZone(zoneId).toEpochSecond();
    }


    public static Long getNowMilli(){
        return getMilli(getNow());
    }

    public static Long getMilli(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static Long getMilli(Date date){
        return date.getTime();
    }

    private static final List<DateTimeFormatter> DATETIMES = new ArrayList<DateTimeFormatter>() {
        {
            this.add(DateTimeFormatter.ISO_DATE_TIME.withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.ISO_INSTANT.withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.BASIC_ISO_DATE.withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.getDefault()));
        }
    };
    private static final List<DateTimeFormatter> DATES = new ArrayList<DateTimeFormatter>() {
        {
            this.add(DateTimeFormatter.ISO_DATE.withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.BASIC_ISO_DATE.withLocale(Locale.getDefault()));
            this.add(DateTimeFormatter.ISO_ORDINAL_DATE.withLocale(Locale.getDefault()));
        }
    };
    private static final List<DateTimeFormatter> TIMES = new ArrayList<DateTimeFormatter>() {
        {
            this.add(DateTimeFormatter.ISO_TIME.withLocale(Locale.getDefault()));
        }
    };


   private static final ConcurrentMap<Integer, String> PATTERNS_MAP = new ConcurrentHashMap<Integer, String>() {
        {
            this.put(19, "yyyy-MM-dd HH:mm:ss");
            this.put(24, "yyyy-MM-dd HH:mm:ss.SSS'Z'");
            this.put(25, "yyyy-MM-dd HH:mm:ss.SSS+'z'");
            this.put(23, "yyyy-MM-dd HH:mm:ss.SSS");
            this.put(28, "EEE MMM dd HH:mm:ss 'CST' yyyy");
            this.put(12, "HH:mm:ss.SSS");
            this.put(10, "yyyy-MM-dd");
            this.put(8, "HH:mm:ss");
        }
    };


   public static LocalDateTime toDateTime(final String literal) {
        return DATETIMES.stream()
                .map(formatter -> parseEach(literal, formatter, LocalDateTime::parse))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }



    private static <T> T parseEach(final String literal, final DateTimeFormatter formatter,
                                   final BiFunction<String, DateTimeFormatter, T> executor) {
        if (StringUtils.isBlank(literal)) {
            return null;
        } else {
            try {
                return executor.apply(literal, formatter);
            } catch (final Throwable ex) {
                return null;
            }
        }
    }

    public static LocalDateTime toDateTime(final Date date) {
        return toDateTime(date.toInstant());
    }


    static LocalDateTime toDateTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

   public static LocalDate toDate(final String literal) {
        return DATES.stream()
                .map(formatter -> parseEach(literal, formatter, LocalDate::parse))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }


    static LocalDate toDate(final Date date) {
        final LocalDateTime datetime = toDateTime(date);
        return datetime.toLocalDate();
    }

   public static LocalDate toDate(final Instant instant) {
        final LocalDateTime datetime = toDateTime(instant);
        return datetime.toLocalDate();
    }

    public static LocalTime toTime(final String literal) {
        return TIMES.stream()
                .map(formatter -> parseEach(literal, formatter, LocalTime::parse))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    static LocalTime toTime(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final Date normalized;
        if (1899 == cal.get(Calendar.YEAR)) {
            cal.add(Calendar.YEAR, 2);
            normalized = cal.getTime();
        } else {
            normalized = date;
        }
        final LocalDateTime datetime = toDateTime(normalized);
        return datetime.toLocalTime();
    }

    static LocalTime toTime(final Instant instant) {
        final LocalDateTime datetime = toDateTime(instant);
        return datetime.toLocalTime();
    }

    static boolean isValid(final String literal) {
        final Date parsed = parse(literal);
        return null != parsed;
    }

    private static DateTimeFormatter analyzeFormatter(final String pattern, final String literal) {
        final DateTimeFormatter formatter;
        if (19 == pattern.length()) {
            // 2018-07-29T16:26:49格式的特殊处理
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        } else if (23 == pattern.length()) {
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        } else if (literal.contains("\\+") || literal.contains("\\-")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS'z'", Locale.US);
        } else {
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        }
        return formatter;
    }

   public static Date parse(final String literal) {
        return CubeFn.getDefault(null, () -> {
            String target = literal;
            if (target.contains("T")) {
                target = target.replace('T', ' ');
            }
            final int length = target.length();
            final String pattern = PATTERNS_MAP.get(length);
            if (null != pattern) {
                final DateTimeFormatter formatter = analyzeFormatter(pattern, literal);
                final Date converted;
                if (10 == pattern.length()) {
                    final LocalDate date = parseEach(target, formatter, LocalDate::parse); // LocalDate.parse(target, formatter);
                    if (Objects.isNull(date)) {
                        converted = null;
                    } else {
                        final ZoneId zoneId = getAdjust(literal);
                        converted = parse(date, zoneId);
                    }
                } else if (15 > pattern.length()) {
                    final LocalTime time = parseEach(target, formatter, LocalTime::parse);
                    if (Objects.isNull(time)) {
                        converted = null;
                    } else {
                        final ZoneId zoneId = getAdjust(literal);
                        converted = parse(time, zoneId);
                    }
                } else {
                    final LocalDateTime datetime = parseEach(target, formatter, LocalDateTime::parse);
                    // final LocalDateTime datetime = LocalDateTime.parse(target, formatter);
                    if (Objects.isNull(datetime)) {
                        converted = null;
                    } else {
                        final ZoneId zoneId = getAdjust(literal);
                        converted = parse(datetime, zoneId);
                    }
                }
                return converted;
            } else {
                return parseFull(literal);
            }
        }, literal);
    }

    public static ZoneId getAdjust(final String literal) {
        if (literal.endsWith("Z")) {
            return ZoneId.from(ZoneOffset.UTC);
        } else {
            return ZoneId.systemDefault();
        }
    }

    /**
     * 「Not Recommend」directly for deep parsing
     *
     * @param literal Date/DateTime/Time literal value here.
     * @return null or valid `java.util.Date` object
     */
    public static Date parseFull(final String literal) {
        return CubeFn.getDefault(null, () -> {
            // Datetime parsing
            final LocalDateTime datetime = toDateTime(literal);
            final ZoneId zoneId = getAdjust(literal);
            if (Objects.isNull(datetime)) {
                // Date parsing
                final LocalDate date = toDate(literal);
                if (Objects.isNull(date)) {
                    // Time parsing
                    final LocalTime time = toTime(literal);
                    return null == time ? null : parse(time);
                } else {
                    /*
                     * Not null datetime
                     */
                    return Date.from(date.atStartOfDay().atZone(zoneId).toInstant());
                }
            } else {
                /*
                 * Not null datetime
                 */
                return Date.from(datetime.atZone(zoneId).toInstant());
            }
        }, literal);
    }

    static void itDay(final String from, final String to,
                      final Consumer<Date> consumer) {
        final LocalDateTime begin = toDateTime(parseFull(from));
        final LocalDateTime end = toDateTime(parseFull(to));
        itDay(begin, end, consumer);
    }

    static void itDay(final LocalDateTime from, final LocalDateTime end,
                      final Consumer<Date> consumer) {
        LocalDate beginDay = from.toLocalDate();
        final LocalDate endDay = end.toLocalDate();
        do {
            consumer.accept(parse(beginDay));
            beginDay = beginDay.plusDays(1);
        } while (endDay.isAfter(beginDay));
    }

    static void itWeek(final String from, final String to,
                       final Consumer<Date> consumer) {
        LocalDate begin = toDate(parseFull(from));
        final LocalDate end = toDate(parseFull(to));
        do {
            consumer.accept(parse(begin));
            begin = begin.plusWeeks(1);
        } while (end.isAfter(begin));
    }

    static boolean equalDate(final Date left, final Date right) {
        // Compare year
        int leftVal = toItem(left, Calendar.YEAR);
        int rightVal = toItem(right, Calendar.YEAR);
        if (leftVal == rightVal) {
            // Compare month
            leftVal = toItem(left, Calendar.MONTH);
            rightVal = toItem(right, Calendar.MONTH);
            if (leftVal == rightVal) {
                // Compare day
                leftVal = toItem(left, Calendar.DAY_OF_MONTH);
                rightVal = toItem(right, Calendar.DAY_OF_MONTH);
                return leftVal == rightVal;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    static int toMonth(final String literal) {
        final Date date = parse(literal);
        return toItem(date, Calendar.MONTH);
    }

    static int toMonth(final Date date) {
        return toItem(date, Calendar.MONTH);
    }

    static int toYear(final Date date) {
        return toItem(date, Calendar.YEAR);
    }

    static int toYear(final String literal) {
        final Date date = parse(literal);
        return toItem(date, Calendar.YEAR);
    }

    private static int toItem(final Date date, final int flag) {
        final Calendar issue = Calendar.getInstance();
        issue.setTime(date);
        return issue.get(flag);
    }

    static Date parse(final LocalTime time) {
        return parse(time, ZoneId.systemDefault());
    }

    private static Date parse(final LocalTime time, final ZoneId zoneId) {
        final LocalDate date = LocalDate.now();
        final LocalDateTime datetime = LocalDateTime.of(date, time);
        return Date.from(datetime.atZone(zoneId).toInstant());
    }

    static Date parse(final LocalDateTime datetime) {
        return parse(datetime, ZoneId.systemDefault());
    }

    private static Date parse(final LocalDateTime datetime, final ZoneId zoneId) {
        return Date.from(datetime.atZone(zoneId).toInstant());
    }

    static Date parse(final LocalDate datetime) {
        return parse(datetime, ZoneId.systemDefault());
    }


    private static Date parse(final LocalDate datetime, final ZoneId zoneId) {
        return Date.from(datetime.atStartOfDay(zoneId).toInstant());
    }
}

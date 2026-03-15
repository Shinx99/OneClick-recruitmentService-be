package com.onceClick.recruitmentService.shared.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations
 */
@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter HUMAN_READABLE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));

    /**
     * Get current timestamp
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Add duration to instant
     */
    public static Instant plusMinutes(Instant instant, long minutes) {
        return instant.plus(minutes, ChronoUnit.MINUTES);
    }

    public static Instant plusHours(Instant instant, long hours) {
        return instant.plus(hours, ChronoUnit.HOURS);
    }

    public static Instant plusDays(Instant instant, long days) {
        return instant.plus(days, ChronoUnit.DAYS);
    }

    public static Instant plusMillis(Instant instant, long millis) {
        return instant.plusMillis(millis);
    }

    /**
     * Check if instant is expired
     */
    public static boolean isExpired(Instant expiryTime) {
        return Instant.now().isAfter(expiryTime);
    }

    public static boolean isNotExpired(Instant expiryTime) {
        return !isExpired(expiryTime);
    }

    /**
     * Check if instant is in the future
     */
    public static boolean isFuture(Instant instant) {
        return instant.isAfter(Instant.now());
    }

    public static boolean isPast(Instant instant) {
        return instant.isBefore(Instant.now());
    }

    /**
     * Calculate duration between two instants
     */
    public static long minutesBetween(Instant start, Instant end) {
        return Duration.between(start, end).toMinutes();
    }

    public static long hoursBetween(Instant start, Instant end) {
        return Duration.between(start, end).toHours();
    }

    public static long daysBetween(Instant start, Instant end) {
        return Duration.between(start, end).toDays();
    }

    /**
     * Format instant to string
     */
    public static String formatISO(Instant instant) {
        return ISO_FORMATTER.format(instant);
    }

    public static String formatHumanReadable(Instant instant) {
        return HUMAN_READABLE_FORMATTER.format(instant);
    }

    /**
     * Parse string to instant
     */
    public static Instant parseISO(String dateTimeString) {
        return Instant.parse(dateTimeString);
    }

    /**
     * Create expiry time for tokens
     */
    public static Instant createEmailVerificationExpiry() {
        return Instant.now().plus(24, ChronoUnit.HOURS); // 24 hours
    }

    public static Instant createPasswordResetExpiry() {
        return Instant.now().plus(1, ChronoUnit.HOURS); // 1 hour
    }

    public static Instant createRefreshTokenExpiry() {
        return Instant.now().plus(7, ChronoUnit.DAYS); // 7 days
    }

    public static Instant createAccessTokenExpiry(long expirationMillis) {
        return Instant.now().plusMillis(expirationMillis);
    }

    /**
     * Get time ago (human readable)
     */
    public static String timeAgo(Instant instant) {
        Duration duration = Duration.between(instant, Instant.now());

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " seconds ago";
        }

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minutes ago";
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " hours ago";
        }

        long days = duration.toDays();
        if (days < 30) {
            return days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months + " months ago";
        }

        long years = days / 365;
        return years + " years ago";
    }

    /**
     * Calculate remaining time
     */
    public static String remainingTime(Instant expiryTime) {
        if (isExpired(expiryTime)) {
            return "Expired";
        }

        Duration duration = Duration.between(Instant.now(), expiryTime);

        long days = duration.toDays();
        if (days > 0) {
            return days + " days remaining";
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + " hours remaining";
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + " minutes remaining";
        }

        return duration.getSeconds() + " seconds remaining";
    }

    /**
     * Truncate instant to specific unit (useful for comparisons)
     */
    public static Instant truncateToSeconds(Instant instant) {
        return instant.truncatedTo(ChronoUnit.SECONDS);
    }

    public static Instant truncateToMinutes(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MINUTES);
    }

    public static Instant truncateToHours(Instant instant) {
        return instant.truncatedTo(ChronoUnit.HOURS);
    }

    public static Instant truncateToDays(Instant instant) {
        return instant.truncatedTo(ChronoUnit.DAYS);
    }
}

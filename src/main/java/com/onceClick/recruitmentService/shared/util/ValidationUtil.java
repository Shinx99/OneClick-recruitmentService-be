package com.onceClick.recruitmentService.shared.util;

import com.onceClick.recruitmentService.shared.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
@UtilityClass
public class ValidationUtil {

    // Email validation pattern (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Phone number pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"
    );

    // UUID pattern
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new ValidationException("email", "Invalid email format");
        }
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static void validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new ValidationException("phone", "Invalid phone number format");
        }
    }

    /**
     * Validate UUID format
     */
    public static boolean isValidUUID(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }

    public static void validateUUID(String uuid, String fieldName) {
        if (!isValidUUID(uuid)) {
            throw new ValidationException(fieldName, "Invalid UUID format");
        }
    }

    /**
     * Validate not null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, fieldName + " is required");
        }
    }

    /**
     * Validate not blank (for strings)
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be blank");
        }
    }

    /**
     * Validate string length
     */
    public static void validateLength(String value, int min, int max, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, fieldName + " is required");
        }

        if (value.length() < min) {
            throw new ValidationException(fieldName,
                    fieldName + " must be at least " + min + " characters");
        }

        if (value.length() > max) {
            throw new ValidationException(fieldName,
                    fieldName + " must not exceed " + max + " characters");
        }
    }

    /**
     * Validate numeric range
     */
    public static void validateRange(long value, long min, long max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName,
                    fieldName + " must be between " + min + " and " + max);
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositive(long value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName, fieldName + " must be positive");
        }
    }

    /**
     * Validate enum value
     */
    public static <E extends Enum<E>> void validateEnum(String value, Class<E> enumClass, String fieldName) {
        try {
            Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(fieldName,
                    "Invalid " + fieldName + ". Allowed values: " +
                            String.join(", ", getEnumNames(enumClass)));
        }
    }

    /**
     * Helper method to get enum names
     */
    private static <E extends Enum<E>> String[] getEnumNames(Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        String[] names = new String[constants.length];
        for (int i = 0; i < constants.length; i++) {
            names[i] = constants[i].name();
        }
        return names;
    }

    /**
     * Sanitize input (remove potential XSS)
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;")
                .trim();
    }

    /**
     * Parse UUID safely
     */
    public static UUID parseUUID(String uuidString, String fieldName) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(fieldName, "Invalid UUID format");
        }
    }

    /**
     * Parse Long safely
     */
    public static Long parseLong(String value, String fieldName) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, fieldName + " must be a valid number");
        }
    }
}

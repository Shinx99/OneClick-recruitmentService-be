package com.onceClick.recruitmentService.shared.util;

import com.onceClick.recruitmentService.shared.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for password operations and validation
 */
@UtilityClass
public class PasswordUtil {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    // Password strength regex patterns
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    // Common weak passwords
    private static final List<String> COMMON_PASSWORDS = List.of(
            "password", "12345678", "123456789", "qwerty", "abc123",
            "password123", "admin", "letmein", "welcome", "monkey"
    );

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Validate password strength
     * @throws ValidationException if password is weak
     */
    public static void validatePasswordStrength(String password) {
        List<String> errors = new ArrayList<>();

        // Check null or empty
        if (password == null || password.isEmpty()) {
            throw new ValidationException("password", "Password is required");
        }

        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }

        // Check complexity
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character");
        }

        // Check for common weak passwords
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            errors.add("Password is too common and easily guessable");
        }

        // Check for sequential characters
        if (hasSequentialCharacters(password)) {
            errors.add("Password contains sequential characters (e.g., '123', 'abc')");
        }

        // Check for repeated characters
        if (hasRepeatedCharacters(password)) {
            errors.add("Password contains too many repeated characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("password", String.join(". ", errors));
        }
    }

    /**
     * Check if password is strong (returns boolean instead of throwing)
     */
    public static boolean isStrongPassword(String password) {
        try {
            validatePasswordStrength(password);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    /**
     * Calculate password strength score (0-100)
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Length score (max 30 points)
        score += Math.min(password.length() * 2, 30);

        // Complexity score
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 10;
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 10;
        if (DIGIT_PATTERN.matcher(password).find()) score += 10;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 15;

        // Variety score (unique characters)
        long uniqueChars = password.chars().distinct().count();
        score += Math.min(uniqueChars * 2, 25);

        // Deduct for common passwords
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            score -= 30;
        }

        // Deduct for sequential/repeated characters
        if (hasSequentialCharacters(password)) score -= 10;
        if (hasRepeatedCharacters(password)) score -= 10;

        return Math.max(0, Math.min(score, 100));
    }

    /**
     * Get password strength label
     */
    public static String getPasswordStrengthLabel(String password) {
        int score = calculatePasswordStrength(password);

        if (score < 30) return "Very Weak";
        if (score < 50) return "Weak";
        if (score < 70) return "Moderate";
        if (score < 90) return "Strong";
        return "Very Strong";
    }

    /**
     * Generate a random strong password
     */
    public static String generateRandomPassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder password = new StringBuilder();

        // Ensure at least one of each required type
        password.append(lowercase.charAt(RANDOM.nextInt(lowercase.length())));
        password.append(uppercase.charAt(RANDOM.nextInt(uppercase.length())));
        password.append(digits.charAt(RANDOM.nextInt(digits.length())));
        password.append(special.charAt(RANDOM.nextInt(special.length())));

        // Fill remaining with random characters
        String allChars = lowercase + uppercase + digits + special;
        for (int i = password.length(); i < length; i++) {
            password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
        }

        // Shuffle the password
        List<Character> chars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, RANDOM);

        StringBuilder shuffled = new StringBuilder();
        for (char c : chars) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }

    /**
     * Check if password contains sequential characters
     */
    private static boolean hasSequentialCharacters(String password) {
        String lower = password.toLowerCase();

        for (int i = 0; i < lower.length() - 2; i++) {
            char c1 = lower.charAt(i);
            char c2 = lower.charAt(i + 1);
            char c3 = lower.charAt(i + 2);

            // Check for ascending sequence (e.g., "abc", "123")
            if (c2 == c1 + 1 && c3 == c2 + 1) {
                return true;
            }

            // Check for descending sequence (e.g., "cba", "321")
            if (c2 == c1 - 1 && c3 == c2 - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if password has too many repeated characters
     */
    private static boolean hasRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                    password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mask password for logging (show only first 2 and last 2 chars)
     */
    public static String maskPassword(String password) {
        if (password == null || password.length() < 4) {
            return "****";
        }

        return password.substring(0, 2) +
                "*".repeat(password.length() - 4) +
                password.substring(password.length() - 2);
    }

    /**
     * Hash Sha256
     */
    public static String sha256(String input){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for(byte b: hash){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }

    /**
     * Hash Argon2/BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));  // Argon2/BCrypt
    }

    /**
     * Hash BLAKE3
     */

    /**
     * Hash HMAC
     */


}

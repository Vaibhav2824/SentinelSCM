package com.scm.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * PasswordUtil - BCrypt password hashing utility.
 *
 * GRASP: Pure Fabrication - utility class with no domain meaning
 * SOLID: SRP - only handles password operations
 */
public class PasswordUtil {

    private static final int COST = 12;

    /** Hash a plain-text password with BCrypt. */
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(COST, plainPassword.toCharArray());
    }

    /** Verify a plain-text password against a BCrypt hash. */
    public static boolean verifyPassword(String plainPassword, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hash);
        return result.verified;
    }
}

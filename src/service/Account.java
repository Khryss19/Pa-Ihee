package service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * A login account (SR Code + password) that belongs to one kind of user.
 * The password itself is never stored: only a random salt and a PBKDF2 hash of the password.
 */
public class Account {
    private static final int ITERATIONS = 65536;

    private final String srCode;
    private final String name;
    private final String role;     // one of the MainSystem.ROLE_* constants
    private final int floor;       // the floor this person is usually on ("nearest comfort room")
    private final String salt;     // Base64
    private final String hash;     // Base64

    /** Used when an account is read from the CSV file (salt and hash are already computed). */
    public Account(String srCode, String name, String role, int floor, String salt, String hash) {
        this.srCode = srCode;
        this.name = name;
        this.role = role;
        this.floor = floor;
        this.salt = salt;
        this.hash = hash;
    }

    /** Used when someone signs up: makes a new random salt and hashes the password with it. */
    public static Account create(String srCode, String password, String name, String role, int floor) {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        return new Account(srCode, name, role, floor, salt, hashPassword(password, salt));
    }

    public boolean matches(String srCode, String password) {
        if (!this.srCode.equals(srCode)) return false;
        byte[] expected = Base64.getDecoder().decode(hash);
        byte[] actual = Base64.getDecoder().decode(hashPassword(password, salt));
        return MessageDigest.isEqual(expected, actual);
    }

    /** The login screen has one "Student/Employee" choice that covers both students and faculty. */
    public boolean matchesChoice(String chosenRole) {
        if (chosenRole.equals(MainSystem.ROLE_STUDENT_FACULTY)) {
            return role.equals(MainSystem.ROLE_STUDENT) || role.equals(MainSystem.ROLE_FACULTY);
        }
        return role.equals(chosenRole);
    }

    private static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, 256);
            byte[] bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is not available.", e);
        }
    }

    public String getSrCode() { return srCode; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getFloor() { return floor; }
    public String getSalt() { return salt; }
    public String getHash() { return hash; }
}
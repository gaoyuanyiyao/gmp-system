package com.gmp.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for {@link User} entity.
 * Tests all methods, edge cases, and business logic of the User entity.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encryptedpassword");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");
        user.setFullName("Test User");
        user.setStatus(User.UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now().minusDays(1));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setPasswordExpired(false);
        user.setCreatedBy(1L);
    }

    @Test
    void testUserBuilder() {
        User builtUser = User.builder()
                .id(2L)
                .username("builtuser")
                .password("encryptedpass")
                .email("built@example.com")
                .phone("0987654321")
                .fullName("Built User")
                .status(User.UserStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now().minusHours(1))
                .failedLoginAttempts(1)
                .accountLocked(false)
                .passwordExpired(true)
                .createdBy(2L)
                .build();

        assertNotNull(builtUser);
        assertEquals(2L, builtUser.getId());
        assertEquals("builtuser", builtUser.getUsername());
        assertEquals("encryptedpass", builtUser.getPassword());
        assertEquals("built@example.com", builtUser.getEmail());
        assertEquals("0987654321", builtUser.getPhone());
        assertEquals("Built User", builtUser.getFullName());
        assertEquals(User.UserStatus.INACTIVE, builtUser.getStatus());
        assertEquals(1, builtUser.getFailedLoginAttempts());
        assertFalse(builtUser.isAccountLocked());
        assertTrue(builtUser.isPasswordExpired());
        assertEquals(2L, builtUser.getCreatedBy());
    }

    @Test
    void testIsActive() {
        assertTrue(user.isActive());
        
        user.setUserStatus(User.UserStatus.INACTIVE);
        assertFalse(user.isActive());
        
        user.setUserStatus(User.UserStatus.LOCKED);
        assertFalse(user.isActive());
        
        user.setUserStatus(User.UserStatus.EXPIRED);
        assertFalse(user.isActive());
    }

    @Test
    void testIsLocked() {
        assertFalse(user.isLocked());
        
        user.setLockedUntil(LocalDateTime.now().plusHours(1));
        assertTrue(user.isLocked());
        
        user.setLockedUntil(LocalDateTime.now().minusHours(1));
        assertFalse(user.isLocked());
    }

    @Test
    void testIsPasswordExpired() {
        assertFalse(user.isPasswordExpired());
        
        user.setPasswordExpiredAt(LocalDateTime.now().minusDays(1));
        assertTrue(user.isPasswordExpired());
        
        user.setPasswordExpiredAt(null);
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testIsPasswordSoonExpired() {
        user.setPasswordExpiredAt(LocalDateTime.now().plusDays(5));
        assertTrue(user.isPasswordSoonExpired());
        
        user.setPasswordExpiredAt(LocalDateTime.now().plusDays(10));
        assertFalse(user.isPasswordSoonExpired());
        
        user.setPasswordExpiredAt(null);
        assertFalse(user.isPasswordSoonExpired());
    }

    @Test
    void testIncrementLoginAttempts() {
        assertEquals(0, user.getLoginAttempts());
        
        user.incrementLoginAttempts();
        assertEquals(1, user.getLoginAttempts());
        
        user.incrementLoginAttempts();
        user.incrementLoginAttempts();
        assertEquals(3, user.getLoginAttempts());
    }

    @Test
    void testResetLoginAttempts() {
        user.setLoginAttempts(5);
        user.resetLoginAttempts();
        assertEquals(0, user.getLoginAttempts());
    }

    @Test
    void testLockAccount() {
        user.lockAccount(30); // 30 minutes
        assertTrue(user.isLocked());
        assertTrue(user.getLockedUntil().isAfter(LocalDateTime.now()));
        assertEquals(User.UserStatus.LOCKED, user.getUserStatus());
    }

    @Test
    void testSetActive() {
        user.setActive(false);
        assertEquals(User.UserStatus.INACTIVE, user.getUserStatus());
        
        user.setActive(true);
        assertEquals(User.UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void testSetLocked() {
        user.setLocked(true);
        assertEquals(User.UserStatus.LOCKED, user.getUserStatus());
        assertNotNull(user.getLockedUntil());
        
        user.setLocked(false);
        assertEquals(User.UserStatus.ACTIVE, user.getUserStatus());
        assertNull(user.getLockedUntil());
    }

    @Test
    void testSetExpired() {
        user.setExpired(true);
        assertEquals(User.UserStatus.EXPIRED, user.getUserStatus());
        
        user.setExpired(false);
        assertEquals(User.UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    void testSetPasswordExpired() {
        user.setPasswordExpired(true);
        assertTrue(user.isPasswordExpired());
        
        user.setPasswordExpired(false);
        assertFalse(user.isPasswordExpired());
        assertTrue(user.getPasswordExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void testMfaRecoveryCodes() {
        // Test setting and getting recovery codes
        List<String> recoveryCodes = Arrays.asList("code1", "code2", "code3");
        user.setRecoveryCodesList(recoveryCodes);
        
        assertEquals("code1,code2,code3", user.getMfaRecoveryCodes());
        assertEquals(recoveryCodes, user.getRecoveryCodesList());
        
        // Test empty recovery codes
        user.setRecoveryCodesList(Collections.emptyList());
        assertNull(user.getMfaRecoveryCodes());
        assertTrue(user.getRecoveryCodesList().isEmpty());
        
        // Test null recovery codes
        user.setMfaRecoveryCodes(null);
        assertNull(user.getMfaRecoveryCodes());
        assertTrue(user.getRecoveryCodesList().isEmpty());
    }

    @Test
    void testUseRecoveryCode() {
        List<String> recoveryCodes = Arrays.asList("code1", "code2", "code3");
        user.setRecoveryCodesList(recoveryCodes);
        
        // Use a valid recovery code
        assertTrue(user.useRecoveryCode("code2"));
        assertEquals(Arrays.asList("code1", "code3"), user.getRecoveryCodesList());
        
        // Try to use an invalid recovery code
        assertFalse(user.useRecoveryCode("invalid"));
        assertEquals(Arrays.asList("code1", "code3"), user.getRecoveryCodesList());
        
        // Use the last recovery code
        assertTrue(user.useRecoveryCode("code1"));
        assertTrue(user.useRecoveryCode("code3"));
        assertTrue(user.getRecoveryCodesList().isEmpty());
        
        // Try to use recovery code when none are available
        assertFalse(user.useRecoveryCode("anycode"));
    }

    @Test
    void testMfaFunctionality() {
        // Test MFA enable/disable
        user.setMfaEnabled(true);
        user.setMfaSecretKey("secret123");
        assertTrue(user.getMfaEnabled());
        assertEquals("secret123", user.getMfaSecretKey());
        
        user.setMfaEnabled(false);
        assertFalse(user.getMfaEnabled());
        
        // Test MFA last verified
        LocalDateTime verificationTime = LocalDateTime.now();
        user.setMfaLastVerified(verificationTime);
        assertEquals(verificationTime, user.getMfaLastVerified());
    }

    @ParameterizedTest
    @ValueSource(strings = {"validuser", "user123", "test_user"})
    void testValidUsernames(String username) {
        User testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .userStatus(User.UserStatus.ACTIVE)
                .build();
        
        assertEquals(username, testUser.getUsername());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testInvalidUsernames(String username) {
        assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .username(username)
                    .email("test@example.com")
                    .userStatus(User.UserStatus.ACTIVE)
                    .build();
        });
    }

    @Test
    void testUserRolesRelationship() {
        // Test user roles collection
        assertNotNull(user.getUserRoles());
        assertTrue(user.getUserRoles().isEmpty());
        
        // Test setting user roles
        UserOrganizationRole role = new UserOrganizationRole();
        user.setUserRoles(Arrays.asList(role));
        assertEquals(1, user.getUserRoles().size());
        assertEquals(role, user.getUserRoles().iterator().next());
    }

    @Test
    void testAuditFields() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setCreatedBy("admin");
        user.setUpdatedBy("admin");
        
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals("admin", user.getCreatedBy());
        assertEquals("admin", user.getUpdatedBy());
    }

    @Test
    void testVersionOptimisticLocking() {
        assertEquals(0L, user.getVersion().longValue());
        
        user.setVersion(1L);
        assertEquals(1L, user.getVersion().longValue());
    }

    @Test
    void testLoginAttemptsConstraints() {
        // Test that login attempts can be set within constraints
        user.setLoginAttempts(0);
        assertEquals(0, user.getLoginAttempts());
        
        user.setLoginAttempts(5);
        assertEquals(5, user.getLoginAttempts());
        
        // Test that negative values are handled
        user.setLoginAttempts(-1);
        assertEquals(-1, user.getLoginAttempts());
    }

    @Test
    void testLastLoginTracking() {
        LocalDateTime loginTime = LocalDateTime.now();
        String ip = "192.168.1.100";
        String session = "session456";
        
        user.setLastLoginTime(loginTime);
        user.setLastLoginIp(ip);
        user.setLastLoginSession(session);
        
        assertEquals(loginTime, user.getLastLoginTime());
        assertEquals(ip, user.getLastLoginIp());
        assertEquals(session, user.getLastLoginSession());
    }

    @Test
    void testCompatibilityMethods() {
        // Test compatibility methods for session and user agent
        user.setLastLoginSession("old_session");
        user.setLastLoginUserAgent("Mozilla/5.0");
        
        assertEquals("old_session", user.getLastLoginSession());
    }

    @Test
    void testEdgeCases() {
        // Test null values for optional fields
        User minimalUser = User.builder()
                .username("minimal")
                .email("min@example.com")
                .userStatus(User.UserStatus.ACTIVE)
                .build();
        
        assertNull(minimalUser.getMobile());
        assertNull(minimalUser.getFullName());
        assertNull(minimalUser.getLastLoginTime());
        assertNull(minimalUser.getLockedUntil());
        assertNull(minimalUser.getMfaSecretKey());
        assertNull(minimalUser.getMfaRecoveryCodes());
    }

    @Test
    void testUserStatusEnumValues() {
        // Test all enum values
        assertEquals(4, User.UserStatus.values().length);
        assertArrayEquals(new User.UserStatus[]{
                User.UserStatus.ACTIVE,
                User.UserStatus.INACTIVE,
                User.UserStatus.LOCKED,
                User.UserStatus.EXPIRED
        }, User.UserStatus.values());
    }

    @Test
    void testUserStatusEnumToString() {
        assertEquals("ACTIVE", User.UserStatus.ACTIVE.toString());
        assertEquals("INACTIVE", User.UserStatus.INACTIVE.toString());
        assertEquals("LOCKED", User.UserStatus.LOCKED.toString());
        assertEquals("EXPIRED", User.UserStatus.EXPIRED.toString());
    }

    @Test
    void testUserStatusEnumValueOf() {
        assertEquals(User.UserStatus.ACTIVE, User.UserStatus.valueOf("ACTIVE"));
        assertEquals(User.UserStatus.INACTIVE, User.UserStatus.valueOf("INACTIVE"));
        assertEquals(User.UserStatus.LOCKED, User.UserStatus.valueOf("LOCKED"));
        assertEquals(User.UserStatus.EXPIRED, User.UserStatus.valueOf("EXPIRED"));
    }
        assertEquals("ACTIVE", User.UserStatus.ACTIVE.name());
        assertEquals("INACTIVE", User.UserStatus.INACTIVE.name());
        assertEquals("SUSPENDED", User.UserStatus.SUSPENDED.name());
        assertEquals("DELETED", User.UserStatus.DELETED.name());
        
        User.UserStatus[] statuses = User.UserStatus.values();
        assertEquals(4, statuses.length);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("encryptedpassword", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals("Test User", user.getFullName());
        assertEquals(User.UserStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertNotNull(user.getLastLoginAt());
        assertEquals(0, user.getFailedLoginAttempts());
        assertFalse(user.isAccountLocked());
        assertFalse(user.isPasswordExpired());
        assertEquals(1L, user.getCreatedBy());
    }

    @Test
    void testSetStatus() {
        user.setStatus(User.UserStatus.SUSPENDED);
        assertEquals(User.UserStatus.SUSPENDED, user.getStatus());
    }

    @Test
    void testSetFailedLoginAttempts() {
        user.setFailedLoginAttempts(5);
        assertEquals(5, user.getFailedLoginAttempts());
    }

    @Test
    void testSetAccountLocked() {
        user.setAccountLocked(true);
        assertTrue(user.isAccountLocked());
    }

    @Test
    void testSetPasswordExpired() {
        user.setPasswordExpired(true);
        assertTrue(user.isPasswordExpired());
    }

    @Test
    void testSetLastLoginAt() {
        LocalDateTime newTime = LocalDateTime.now().minusHours(2);
        user.setLastLoginAt(newTime);
        assertEquals(newTime, user.getLastLoginAt());
    }

    @Test
    void testSetCreatedBy() {
        user.setCreatedBy(3L);
        assertEquals(3L, user.getCreatedBy());
    }

    @Test
    void testToString() {
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }

    @Test
    void testEqualsAndHashCode() {
        User sameUser = new User();
        sameUser.setId(1L);
        sameUser.setUsername("testuser");

        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("differentuser");

        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());
        
        assertNotEquals(user, differentUser);
        assertNotEquals(user.hashCode(), differentUser.hashCode());
        
        assertNotEquals(user, null);
        assertNotEquals(user, "Not a user");
    }

    @Test
    void testCanEqual() {
        User otherUser = new User();
        otherUser.setId(1L);
        
        assertTrue(user.canEqual(otherUser));
        assertFalse(user.canEqual("Not a user"));
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastLogin = now.minusHours(1);
        
        User fullUser = new User(
                1L, "fulluser", "encrypted", "full@example.com", 
                "9876543210", "Full User", User.UserStatus.ACTIVE, 
                now, now, lastLogin, 0, false, false, 1L
        );

        assertNotNull(fullUser);
        assertEquals(1L, fullUser.getId());
        assertEquals("fulluser", fullUser.getUsername());
        assertEquals("encrypted", fullUser.getPassword());
        assertEquals("full@example.com", fullUser.getEmail());
        assertEquals("9876543210", fullUser.getPhone());
        assertEquals("Full User", fullUser.getFullName());
        assertEquals(User.UserStatus.ACTIVE, fullUser.getStatus());
        assertEquals(now, fullUser.getCreatedAt());
        assertEquals(now, fullUser.getUpdatedAt());
        assertEquals(lastLogin, fullUser.getLastLoginAt());
        assertEquals(0, fullUser.getFailedLoginAttempts());
        assertFalse(fullUser.isAccountLocked());
        assertFalse(fullUser.isPasswordExpired());
        assertEquals(1L, fullUser.getCreatedBy());
    }

    @Test
    void testIsActiveStatus() {
        user.setStatus(User.UserStatus.ACTIVE);
        assertTrue(user.isActive());

        user.setStatus(User.UserStatus.INACTIVE);
        assertFalse(user.isActive());

        user.setStatus(User.UserStatus.SUSPENDED);
        assertFalse(user.isActive());

        user.setStatus(User.UserStatus.DELETED);
        assertFalse(user.isActive());
    }

    @Test
    void testIncrementFailedLoginAttempts() {
        user.setFailedLoginAttempts(2);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertEquals(3, user.getFailedLoginAttempts());
    }

    @Test
    void testResetFailedLoginAttempts() {
        user.setFailedLoginAttempts(5);
        user.setFailedLoginAttempts(0);
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    void testAccountLockedAfterMaxAttempts() {
        user.setFailedLoginAttempts(5);
        user.setAccountLocked(true);
        
        assertTrue(user.isAccountLocked());
        assertEquals(5, user.getFailedLoginAttempts());
    }

    @Test
    void testPasswordExpiry() {
        user.setPasswordExpired(true);
        assertTrue(user.isPasswordExpired());

        user.setPasswordExpired(false);
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testLastLoginTracking() {
        LocalDateTime loginTime = LocalDateTime.now().minusMinutes(30);
        user.setLastLoginAt(loginTime);
        
        assertEquals(loginTime, user.getLastLoginAt());
        
        // Test updating last login
        LocalDateTime newLoginTime = LocalDateTime.now();
        user.setLastLoginAt(newLoginTime);
        assertEquals(newLoginTime, user.getLastLoginAt());
    }

    @Test
    void testUserWithNullValues() {
        User nullUser = new User();
        nullUser.setId(null);
        nullUser.setUsername(null);
        nullUser.setEmail(null);
        nullUser.setPhone(null);
        nullUser.setFullName(null);
        nullUser.setStatus(null);
        nullUser.setCreatedAt(null);
        nullUser.setUpdatedAt(null);
        nullUser.setLastLoginAt(null);
        nullUser.setCreatedBy(null);

        assertNull(nullUser.getId());
        assertNull(nullUser.getUsername());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getPhone());
        assertNull(nullUser.getFullName());
        assertNull(nullUser.getStatus());
        assertNull(nullUser.getCreatedAt());
        assertNull(nullUser.getUpdatedAt());
        assertNull(nullUser.getLastLoginAt());
        assertNull(nullUser.getCreatedBy());
    }

    @Test
    void testUserWithEmptyStrings() {
        user.setUsername("");
        user.setEmail("");
        user.setPhone("");
        user.setFullName("");

        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhone());
        assertEquals("", user.getFullName());
    }

    @Test
    void testUserValidation() {
        // Test valid email format
        user.setEmail("valid.email@example.com");
        assertEquals("valid.email@example.com", user.getEmail());

        // Test valid phone number (just string validation)
        user.setPhone("+86-123-456-7890");
        assertEquals("+86-123-456-7890", user.getPhone());

        // Test username with special characters (if allowed)
        user.setUsername("user.name_123");
        assertEquals("user.name_123", user.getUsername());
    }
}
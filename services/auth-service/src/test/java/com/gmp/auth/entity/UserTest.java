package com.gmp.auth.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    private User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        // 初始化Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // 创建一个基本的User对象
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setMobile("13800138000");
        user.setUserStatus(User.UserStatus.ACTIVE);
    }

    @Test
    void testUserBuilder() {
        // 测试User.builder()模式
        User builtUser = User.builder()
                .id(2L)
                .username("builtuser")
                .fullName("Built User")
                .email("built@example.com")
                .mobile("13900139000")
                .passwordHash("hashedpassword")
                .userStatus(User.UserStatus.ACTIVE)
                .build();

        assertNotNull(builtUser);
        assertEquals(2L, builtUser.getId());
        assertEquals("builtuser", builtUser.getUsername());
        assertEquals("Built User", builtUser.getFullName());
        assertEquals("built@example.com", builtUser.getEmail());
        assertEquals("13900139000", builtUser.getMobile());
        assertEquals(User.UserStatus.ACTIVE, builtUser.getUserStatus());
    }

    @Test
    void testUserGettersAndSetters() {
        // 测试基本的getter和setter方法
        user.setUsername("newname");
        user.setFullName("New Name");
        user.setEmail("new@example.com");
        user.setMobile("13700137000");

        assertEquals("newname", user.getUsername());
        assertEquals("New Name", user.getFullName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("13700137000", user.getMobile());
    }

    @Test
    void testPasswordMethods() {
        // 测试密码相关方法
        user.setPassword("newpassword");
        assertNotNull(user.getPassword());

        // 测试密码过期设置
        user.setPasswordExpired(true);
        assertTrue(user.isPasswordExpired());

        user.setPasswordExpired(false);
        assertFalse(user.isPasswordExpired());
    }

    @Test
    void testUserStatusMethods() {
        // 测试用户状态相关方法
        assertTrue(user.isActive());
        assertFalse(user.isLocked());

        // 测试设置不同状态
        user.setActive(false);
        assertEquals(User.UserStatus.INACTIVE, user.getUserStatus());
        assertFalse(user.isActive());

        user.setActive(true);
        user.setLocked(true);
        assertEquals(User.UserStatus.LOCKED, user.getUserStatus());
        assertTrue(user.isLocked());

        user.setLocked(false);
        user.setExpired(true);
        assertEquals(User.UserStatus.EXPIRED, user.getUserStatus());
        assertFalse(user.isActive());
    }

    @Test
    void testLoginAttempts() {
        // 测试登录尝试次数相关方法
        assertEquals(0, user.getLoginAttempts());

        user.incrementLoginAttempts();
        user.incrementLoginAttempts();
        assertEquals(2, user.getLoginAttempts());

        user.resetLoginAttempts();
        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getLockedUntil());
    }

    @Test
    void testAccountLocking() {
        // 测试账户锁定功能
        user.lockAccount(30); // 锁定30分钟
        assertEquals(User.UserStatus.LOCKED, user.getUserStatus());
        assertNotNull(user.getLockedUntil());
        assertTrue(user.getLockedUntil().isAfter(LocalDateTime.now()));

        // 重置后应该解锁
        user.resetLoginAttempts();
        assertEquals(0, user.getLoginAttempts());
        assertNull(user.getLockedUntil());
    }

    @Test
    void testMfaMethods() {
        // 测试MFA相关方法
        assertFalse(user.isMfaEnabled());
        user.setMfaEnabled(true);
        assertTrue(user.isMfaEnabled());

        // 测试恢复码功能
        List<String> recoveryCodes = List.of("code1", "code2", "code3");
        user.setRecoveryCodesList(recoveryCodes);
        
        assertEquals(recoveryCodes, user.getRecoveryCodesList());
        assertEquals("code1,code2,code3", user.getMfaRecoveryCodes());
    }

    @Test
    void testLastLoginSession() {
        // 测试最后登录会话设置
        user.setLastLoginSession("session123");
        assertEquals("session123", user.getLastLoginSession());

        // 测试兼容方法
        user.setLastLoginSession(456L);
        assertEquals("456", user.getLastLoginSession());
    }

    @Test
    void testUserValidation() {
        // 测试用户验证约束
        User invalidUser = new User();
        invalidUser.setUsername("ab"); // 太短
        invalidUser.setPassword(""); // 空密码
        invalidUser.setFullName(""); // 空姓名
        invalidUser.setEmail("invalid-email"); // 无效邮箱
        invalidUser.setMobile("12345"); // 无效手机号

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.size() > 0);
        
        // 检查具体的验证错误
        boolean hasUsernameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        boolean hasPasswordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash"));
        boolean hasFullNameViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullName"));
        boolean hasEmailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        boolean hasMobileViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("mobile"));

        assertTrue(hasUsernameViolation);
        assertTrue(hasPasswordViolation);
        assertTrue(hasFullNameViolation);
        assertTrue(hasEmailViolation);
        assertTrue(hasMobileViolation);
    }

    @Test
    void testUserRoles() {
        // 测试用户角色关联
        List<UserRole> roles = List.of(
                new UserRole(1L, 1L),
                new UserRole(1L, 2L)
        );
        user.setUserRoles(roles);
        assertEquals(2, user.getUserRoles().size());
    }

    @Test
    void testRecoveryCodes() {
        // 测试恢复码的使用
        List<String> recoveryCodes = List.of("code1", "code2", "code3");
        user.setRecoveryCodesList(recoveryCodes);
        
        // 使用一个恢复码
        boolean used = user.useRecoveryCode("code2");
        assertTrue(used);
        
        // 检查恢复码列表是否更新
        List<String> remainingCodes = user.getRecoveryCodesList();
        assertEquals(2, remainingCodes.size());
        assertFalse(remainingCodes.contains("code2"));
        assertTrue(remainingCodes.contains("code1"));
        assertTrue(remainingCodes.contains("code3"));
        
        // 使用不存在的恢复码
        boolean notUsed = user.useRecoveryCode("nonexistent");
        assertFalse(notUsed);
    }

    @Test
    void testPasswordSoonExpired() {
        // 测试密码即将过期功能
        assertFalse(user.isPasswordSoonExpired());
    }

    @Test
    void testLastLoginUserAgent() {
        // 测试User-Agent兼容方法
        user.setLastLoginUserAgent("Mozilla/5.0");
        // 这个方法目前是空实现，所以只需要确保它能正常调用
    }
}
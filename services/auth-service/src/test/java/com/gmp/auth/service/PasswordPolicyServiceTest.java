package com.gmp.auth.service;

import com.gmp.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserPasswordHistoryService userPasswordHistoryService;

    @InjectMocks
    private PasswordPolicyService passwordPolicyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void testValidatePasswordComplexity_ValidPassword_ReturnsNull() {
        // Arrange
        String validPassword = "Valid123!";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(validPassword);

        // Assert
        assertNull(result);
    }

    @Test
    void testValidatePasswordComplexity_TooShort_ReturnsErrorMessage() {
        // Arrange
        String shortPassword = "Short1!";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(shortPassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码长度必须至少"));
    }

    @Test
    void testValidatePasswordComplexity_NoDigit_ReturnsErrorMessage() {
        // Arrange
        String noDigitPassword = "NoDigitPassword!";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(noDigitPassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码必须包含至少一个数字"));
    }

    @Test
    void testValidatePasswordComplexity_NoLowercase_ReturnsErrorMessage() {
        // Arrange
        String noLowercasePassword = "NODIGITPASSWORD!1";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(noLowercasePassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码必须包含至少一个小写字母"));
    }

    @Test
    void testValidatePasswordComplexity_NoUppercase_ReturnsErrorMessage() {
        // Arrange
        String noUppercasePassword = "nodigitpassword!1";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(noUppercasePassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码必须包含至少一个大写字母"));
    }

    @Test
    void testValidatePasswordComplexity_NoSpecialCharacter_ReturnsErrorMessage() {
        // Arrange
        String noSpecialCharPassword = "NoSpecialChar123";

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(noSpecialCharPassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码必须包含至少一个特殊字符"));
    }

    @Test
    void testValidatePasswordComplexity_NullPassword_ReturnsErrorMessage() {
        // Arrange
        String nullPassword = null;

        // Act
        String result = passwordPolicyService.validatePasswordComplexity(nullPassword);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("密码长度必须至少"));
    }

    @Test
    void testValidatePassword_ValidPassword_ReturnsTrue() {
        // Arrange
        String validPassword = "Valid123!";

        // Act
        boolean result = passwordPolicyService.validatePassword(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidatePassword_InvalidPassword_ReturnsFalse() {
        // Arrange
        String invalidPassword = "invalid";

        // Act
        boolean result = passwordPolicyService.validatePassword(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPasswordInHistory_PasswordInHistory_ReturnsTrue() {
        // Arrange
        String rawPassword = "TestPassword123!";
        String hashedPassword = "hashedpassword123";
        
        when(userPasswordHistoryService.getPasswordHistory(eq(1L), anyInt())).thenReturn(List.of(hashedPassword));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        boolean result = passwordPolicyService.isPasswordInHistory(1L, rawPassword);

        // Assert
        assertTrue(result);
        verify(userPasswordHistoryService).getPasswordHistory(eq(1L), anyInt());
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }

    @Test
    void testIsPasswordInHistory_PasswordNotInHistory_ReturnsFalse() {
        // Arrange
        String rawPassword = "TestPassword123!";
        String hashedPassword = "hashedpassword123";
        
        when(userPasswordHistoryService.getPasswordHistory(eq(1L), anyInt())).thenReturn(List.of(hashedPassword));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Act
        boolean result = passwordPolicyService.isPasswordInHistory(1L, rawPassword);

        // Assert
        assertFalse(result);
        verify(userPasswordHistoryService).getPasswordHistory(eq(1L), anyInt());
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }

    @Test
    void testCalculatePasswordExpiryTime_ReturnsFutureDate() {
        // Act
        LocalDateTime expiryTime = passwordPolicyService.calculatePasswordExpiryTime();

        // Assert
        assertNotNull(expiryTime);
        assertTrue(expiryTime.isAfter(LocalDateTime.now()));
    }

    @Test
    void testIsPasswordExpiringSoon_PasswordExpiringIn5Days_ReturnsTrue() {
        // Arrange
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(5);

        // Act
        boolean result = passwordPolicyService.isPasswordExpiringSoon(expiryTime);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsPasswordExpiringSoon_PasswordExpiringIn8Days_ReturnsFalse() {
        // Arrange
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(8);

        // Act
        boolean result = passwordPolicyService.isPasswordExpiringSoon(expiryTime);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPasswordExpiringSoon_PasswordAlreadyExpired_ReturnsFalse() {
        // Arrange
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(1);

        // Act
        boolean result = passwordPolicyService.isPasswordExpiringSoon(expiryTime);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPasswordExpiringSoon_NullExpiryTime_ReturnsFalse() {
        // Arrange
        LocalDateTime expiryTime = null;

        // Act
        boolean result = passwordPolicyService.isPasswordExpiringSoon(expiryTime);

        // Assert
        assertFalse(result);
    }

    @Test
    void testRecordPasswordHistory_CallsServiceMethod() {
        // Arrange
        String hashedPassword = "hashedpassword123";

        // Act
        passwordPolicyService.recordPasswordHistory(1L, hashedPassword);

        // Assert
        verify(userPasswordHistoryService).recordPasswordHistory(1L, hashedPassword);
    }

    @Test
    void testGetPasswordComplexityRequirements_ReturnsDescription() {
        // Act
        String description = passwordPolicyService.getPasswordComplexityRequirements();

        // Assert
        assertNotNull(description);
        assertTrue(description.contains("密码必须满足以下要求"));
        assertTrue(description.contains("长度至少"));
        assertTrue(description.contains("包含至少一个数字"));
        assertTrue(description.contains("包含至少一个小写字母"));
        assertTrue(description.contains("包含至少一个大写字母"));
        assertTrue(description.contains("包含至少一个特殊字符"));
    }

    @Test
    void testGetPasswordRequirementsDescription_ReturnsDescription() {
        // Act
        String description = passwordPolicyService.getPasswordRequirementsDescription();

        // Assert
        assertNotNull(description);
        assertTrue(description.contains("密码必须至少包含"));
        assertTrue(description.contains("个字符"));
        assertTrue(description.contains("大写字母"));
        assertTrue(description.contains("小写字母"));
        assertTrue(description.contains("数字"));
        assertTrue(description.contains("特殊字符"));
    }
}
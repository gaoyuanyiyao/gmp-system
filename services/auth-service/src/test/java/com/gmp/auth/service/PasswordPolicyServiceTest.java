package com.gmp.auth.service;

import com.gmp.auth.config.PasswordPolicyConfig;
import com.gmp.auth.entity.User;
import com.gmp.auth.exception.InvalidPasswordException;
import com.gmp.auth.service.impl.PasswordPolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyServiceTest {

    @Mock
    private PasswordPolicyConfig passwordPolicyConfig;

    @InjectMocks
    private PasswordPolicyServiceImpl passwordPolicyService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        // 设置默认的密码策略配置
        when(passwordPolicyConfig.getMinLength()).thenReturn(8);
        when(passwordPolicyConfig.getMaxLength()).thenReturn(128);
        when(passwordPolicyConfig.getRequireUppercase()).thenReturn(true);
        when(passwordPolicyConfig.getRequireLowercase()).thenReturn(true);
        when(passwordPolicyConfig.getRequireNumbers()).thenReturn(true);
        when(passwordPolicyConfig.getRequireSpecialCharacters()).thenReturn(true);
        when(passwordPolicyConfig.getSpecialCharacters()).thenReturn("!@#$%^&*()_+-=[]{}|;:,.<>?");
        when(passwordPolicyConfig.getMaxPasswordAgeDays()).thenReturn(90);
        when(passwordPolicyConfig.getPasswordHistorySize()).thenReturn(5);
    }

    @Test
    void testValidatePassword_ValidPassword_ShouldNotThrowException() {
        // Arrange
        String validPassword = "ValidPassword123!";

        // Act & Assert
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(validPassword));
    }

    @Test
    void testValidatePassword_TooShort_ShouldThrowException() {
        // Arrange
        String shortPassword = "Short1!";

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(shortPassword));
        assertTrue(exception.getMessage().contains("密码长度必须至少"));
    }

    @Test
    void testValidatePassword_TooLong_ShouldThrowException() {
        // Arrange
        String longPassword = "A".repeat(129) + "1!";
        when(passwordPolicyConfig.getMaxLength()).thenReturn(128);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(longPassword));
        assertTrue(exception.getMessage().contains("密码长度不能超过"));
    }

    @Test
    void testValidatePassword_NoUppercase_ShouldThrowException() {
        // Arrange
        String noUppercasePassword = "lowercase123!";

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(noUppercasePassword));
        assertTrue(exception.getMessage().contains("必须包含至少一个大写字母"));
    }

    @Test
    void testValidatePassword_NoLowercase_ShouldThrowException() {
        // Arrange
        String noLowercasePassword = "UPPERCASE123!";

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(noLowercasePassword));
        assertTrue(exception.getMessage().contains("必须包含至少一个小写字母"));
    }

    @Test
    void testValidatePassword_NoNumbers_ShouldThrowException() {
        // Arrange
        String noNumbersPassword = "NoNumbersPassword!";

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(noNumbersPassword));
        assertTrue(exception.getMessage().contains("必须包含至少一个数字"));
    }

    @Test
    void testValidatePassword_NoSpecialCharacters_ShouldThrowException() {
        // Arrange
        String noSpecialCharsPassword = "NoSpecialChars123";

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> passwordPolicyService.validatePassword(noSpecialCharsPassword));
        assertTrue(exception.getMessage().contains("必须包含至少一个特殊字符"));
    }

    @Test
    void testValidatePassword_WithDisabledUppercaseRequirement_ShouldNotRequireUppercase() {
        // Arrange
        String passwordWithoutUppercase = "lowercase123!";
        when(passwordPolicyConfig.getRequireUppercase()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(passwordWithoutUppercase));
    }

    @Test
    void testValidatePassword_WithDisabledLowercaseRequirement_ShouldNotRequireLowercase() {
        // Arrange
        String passwordWithoutLowercase = "UPPERCASE123!";
        when(passwordPolicyConfig.getRequireLowercase()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(passwordWithoutLowercase));
    }

    @Test
    void testValidatePassword_WithDisabledNumbersRequirement_ShouldNotRequireNumbers() {
        // Arrange
        String passwordWithoutNumbers = "NoNumbersPassword!";
        when(passwordPolicyConfig.getRequireNumbers()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(passwordWithoutNumbers));
    }

    @Test
    void testValidatePassword_WithDisabledSpecialCharsRequirement_ShouldNotRequireSpecialChars() {
        // Arrange
        String passwordWithoutSpecialChars = "NoSpecialChars123";
        when(passwordPolicyConfig.getRequireSpecialCharacters()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(passwordWithoutSpecialChars));
    }

    @Test
    void testIsPasswordExpired_WhenPasswordNeverChanged_ShouldReturnFalse() {
        // Arrange
        user.setPasswordLastChanged(null);

        // Act
        boolean result = passwordPolicyService.isPasswordExpired(user);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPasswordExpired_WhenPasswordRecentlyChanged_ShouldReturnFalse() {
        // Arrange
        user.setPasswordLastChanged(java.time.LocalDateTime.now().minusDays(30));

        // Act
        boolean result = passwordPolicyService.isPasswordExpired(user);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsPasswordExpired_WhenPasswordExpired_ShouldReturnTrue() {
        // Arrange
        user.setPasswordLastChanged(java.time.LocalDateTime.now().minusDays(91));

        // Act
        boolean result = passwordPolicyService.isPasswordExpired(user);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetPasswordExpiryDays_ShouldReturnRemainingDays() {
        // Arrange
        user.setPasswordLastChanged(java.time.LocalDateTime.now().minusDays(60));

        // Act
        long remainingDays = passwordPolicyService.getPasswordExpiryDays(user);

        // Assert
        assertEquals(30, remainingDays);
    }

    @Test
    void testGetPasswordExpiryDays_WhenPasswordNeverChanged_ShouldReturnMaxDays() {
        // Arrange
        user.setPasswordLastChanged(null);

        // Act
        long remainingDays = passwordPolicyService.getPasswordExpiryDays(user);

        // Assert
        assertEquals(90, remainingDays);
    }

    @Test
    void testGetPasswordExpiryDays_WhenPasswordAlreadyExpired_ShouldReturnNegative() {
        // Arrange
        user.setPasswordLastChanged(java.time.LocalDateTime.now().minusDays(100));

        // Act
        long remainingDays = passwordPolicyService.getPasswordExpiryDays(user);

        // Assert
        assertTrue(remainingDays < 0);
    }

    @Test
    void testGenerateRandomPassword_ShouldGenerateValidPassword() {
        // Act
        String randomPassword = passwordPolicyService.generateRandomPassword();

        // Assert
        assertNotNull(randomPassword);
        assertTrue(randomPassword.length() >= 12);
        assertTrue(randomPassword.length() <= 16);
        
        // 验证生成的密码符合策略要求
        assertDoesNotThrow(() -> passwordPolicyService.validatePassword(randomPassword));
    }

    @Test
    void testCheckPasswordStrength_StrongPassword_ShouldReturnHighScore() {
        // Arrange
        String strongPassword = "VeryStrongPassword123!";

        // Act
        int strength = passwordPolicyService.checkPasswordStrength(strongPassword);

        // Assert
        assertTrue(strength >= 80);
    }

    @Test
    void testCheckPasswordStrength_WeakPassword_ShouldReturnLowScore() {
        // Arrange
        String weakPassword = "weak";

        // Act
        int strength = passwordPolicyService.checkPasswordStrength(weakPassword);

        // Assert
        assertTrue(strength <= 20);
    }
}
package com.gmp.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmp.auth.entity.OperationLog;
import com.gmp.auth.repository.OperationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private OperationLogRepository operationLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private OperationLog testLog;

    @BeforeEach
    void setUp() {
        testLog = OperationLog.builder()
                .id(1L)
                .userId(1L)
                .username("testuser")
                .operation(OperationLog.OperationType.LOGIN.name())
                .module(OperationLog.Module.AUTH.name())
                .action("用户登录成功")
                .result(OperationLog.Result.SUCCESS)
                .ipAddress("192.168.1.1")
                .userAgent("Test Browser")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void testLogLogout_StringUsername() {
        // 执行测试
        auditLogService.logLogout("testuser");

        // 验证日志记录被调用
        verify(operationLogRepository, never()).save(any()); // 当前实现只记录日志，不保存到数据库
    }

    @Test
    void testLogLoginSuccess_StringUsername() {
        // 执行测试
        auditLogService.logLoginSuccess("testuser", "192.168.1.1", "Test Browser");

        // 验证日志记录被调用
        verify(operationLogRepository, never()).save(any()); // 当前实现只记录日志，不保存到数据库
    }

    @Test
    void testLogLoginFailure_StringUsername() {
        // 执行测试
        auditLogService.logLoginFailure("testuser", "192.168.1.1", "Test Browser", "Invalid credentials");

        // 验证日志记录被调用
        verify(operationLogRepository, never()).save(any()); // 当前实现只记录日志，不保存到数据库
    }

    @Test
    void testLogPasswordChange_StringUsername() {
        // 执行测试
        auditLogService.logPasswordChange("testuser");

        // 验证日志记录被调用
        verify(operationLogRepository, never()).save(any()); // 当前实现只记录日志，不保存到数据库
    }

    @Test
    void testLogPasswordReset_StringUsername() {
        // 执行测试
        auditLogService.logPasswordReset("testuser");

        // 验证日志记录被调用
        verify(operationLogRepository, never()).save(any()); // 当前实现只记录日志，不保存到数据库
    }

    @Test
    void testLogPasswordReset_LongUserId() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);

        // 执行测试
        auditLogService.logPasswordReset(1L, "testuser", "192.168.1.1");

        // 验证结果
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogPasswordChange_LongUserId() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);

        // 执行测试
        auditLogService.logPasswordChange(1L, "testuser", "192.168.1.1");

        // 验证结果
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogLoginSuccess_LongUserId() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);

        // 执行测试
        auditLogService.logLoginSuccess(1L, "testuser", "192.168.1.1", "Test Browser");

        // 验证结果
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogOperation_Success() {
        // 准备数据
        when(operationLogRepository.save(testLog)).thenReturn(testLog);

        // 执行测试
        OperationLog result = auditLogService.logOperation(testLog);

        // 验证结果
        assertNotNull(result);
        assertEquals(testLog, result);
        verify(operationLogRepository).save(testLog);
    }

    @Test
    void testLogOperation_Exception() {
        // 准备数据
        RuntimeException expectedException = new RuntimeException("Database error");
        when(operationLogRepository.save(testLog)).thenThrow(expectedException);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                auditLogService.logOperation(testLog));

        assertEquals("Failed to log operation", exception.getMessage());
        assertEquals(expectedException, exception.getCause());
    }

    @Test
    void testLogLogin_Success() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("loginType", "password");
        additionalInfo.put("mfaEnabled", true);

        // 执行测试
        OperationLog result = auditLogService.logLogin(1L, "testuser", "192.168.1.1", "Test Browser", true, additionalInfo);

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogLogin_Failure() {
        // 准备数据
        OperationLog failureLog = OperationLog.builder()
                .userId(1L)
                .username("testuser")
                .operation(OperationLog.OperationType.LOGIN.name())
                .module(OperationLog.Module.AUTH.name())
                .action("用户登录失败")
                .result(OperationLog.Result.FAILED)
                .ipAddress("192.168.1.1")
                .userAgent("Test Browser")
                .build();

        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(failureLog);

        // 执行测试
        OperationLog result = auditLogService.logLogin(1L, "testuser", "192.168.1.1", "Test Browser", false, null);

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogLogin_WithAdditionalInfo() throws Exception {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"loginType\":\"password\"}");

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("loginType", "password");

        // 执行测试
        OperationLog result = auditLogService.logLogin(1L, "testuser", "192.168.1.1", "Test Browser", true, additionalInfo);

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
        verify(objectMapper).writeValueAsString(additionalInfo);
    }

    @Test
    void testLogLogout_LongUserId() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);

        // 执行测试
        OperationLog result = auditLogService.logLogout(1L, "testuser", "192.168.1.1");

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
    }

    @Test
    void testLogUserManagement_Success() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"targetUserId\":2,\"targetUsername\":\"targetuser\"}");

        // 执行测试
        OperationLog result = auditLogService.logUserManagement(
                1L, "operator", 2L, "targetuser", 
                OperationLog.OperationType.USER_CREATE, true, "User created successfully");

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
        verify(objectMapper).writeValueAsString(any(Map.class));
    }

    @Test
    void testLogRolePermissionChange_Success() {
        // 准备数据
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testLog);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"roleId\":1,\"roleCode\":\"ADMIN\",\"permissionChanges\":\"Added READ permission\"}");

        // 执行测试
        OperationLog result = auditLogService.logRolePermissionChange(
                1L, "admin", 1L, "ADMIN", "Added READ permission", true);

        // 验证结果
        assertNotNull(result);
        verify(operationLogRepository).save(any(OperationLog.class));
        verify(objectMapper).writeValueAsString(any(Map.class));
    }

    @Test
    void testNullAndEdgeCases() {
        // 测试空值处理
        auditLogService.logLogout(null);
        auditLogService.logLoginSuccess(null, null, null);
        auditLogService.logLoginFailure(null, null, null, null);
        auditLogService.logPasswordChange(null);
        auditLogService.logPasswordReset(null);

        // 验证这些调用不会抛出异常
        assertTrue(true); // 如果到达这里，说明没有抛出异常
    }

    @Test
    void testEmptyStringValues() {
        // 测试空字符串值
        auditLogService.logLogout("");
        auditLogService.logLoginSuccess("", "", "");
        auditLogService.logLoginFailure("", "", "", "");
        auditLogService.logPasswordChange("");
        auditLogService.logPasswordReset("");

        // 验证这些调用不会抛出异常
        assertTrue(true); // 如果到达这里，说明没有抛出异常
    }

    @Test
    void testLongStringValues() {
        // 测试长字符串值
        String longString = "a".repeat(1000);
        
        auditLogService.logLogout(longString);
        auditLogService.logLoginSuccess(longString, longString, longString);
        auditLogService.logLoginFailure(longString, longString, longString, longString);
        auditLogService.logPasswordChange(longString);
        auditLogService.logPasswordReset(longString);

        // 验证这些调用不会抛出异常
        assertTrue(true); // 如果到达这里，说明没有抛出异常
    }

    @Test
    void testSpecialCharacters() {
        // 测试特殊字符
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\\";
        
        auditLogService.logLogout(specialChars);
        auditLogService.logLoginSuccess(specialChars, specialChars, specialChars);
        auditLogService.logLoginFailure(specialChars, specialChars, specialChars, specialChars);
        auditLogService.logPasswordChange(specialChars);
        auditLogService.logPasswordReset(specialChars);

        // 验证这些调用不会抛出异常
        assertTrue(true); // 如果到达这里，说明没有抛出异常
    }
}
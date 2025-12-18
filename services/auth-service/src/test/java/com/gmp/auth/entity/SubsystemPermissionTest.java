package com.gmp.auth.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * SubsystemPermission实体类的综合测试
 * 测试所有属性、方法和业务逻辑
 */
class SubsystemPermissionTest {

    private SubsystemPermission subsystemPermission;
    private Subsystem mockSubsystem;
    private Permission mockPermission;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        // 创建模拟对象
        mockSubsystem = mock(Subsystem.class);
        mockPermission = mock(Permission.class);
        
        // 设置模拟对象的行为
        when(mockSubsystem.getId()).thenReturn(1L);
        when(mockPermission.getId()).thenReturn(101L);
        
        subsystemPermission = SubsystemPermission.builder()
                .subsystem(mockSubsystem)
                .permission(mockPermission)
                .accessLevel(2)
                .isDefault(false)
                .createdBy("system")
                .createdTime(now)
                .updatedBy("admin")
                .updatedTime(now)
                .build();
    }

    // 基础构建器和构造函数测试
    @Test
    @DisplayName("测试构建器模式创建对象")
    void testBuilder() {
        assertEquals(mockSubsystem, subsystemPermission.getSubsystem());
        assertEquals(mockPermission, subsystemPermission.getPermission());
        assertEquals(2, subsystemPermission.getAccessLevel());
        assertFalse(subsystemPermission.getIsDefault());
        assertEquals("system", subsystemPermission.getCreatedBy());
        assertEquals(now, subsystemPermission.getCreatedTime());
        assertEquals("admin", subsystemPermission.getUpdatedBy());
        assertEquals(now, subsystemPermission.getUpdatedTime());
    }

    @Test
    @DisplayName("测试无参构造函数")
    void testNoArgsConstructor() {
        SubsystemPermission emptyPermission = new SubsystemPermission();
        assertNotNull(emptyPermission);
    }

    @Test
    @DisplayName("测试全参构造函数")
    void testAllArgsConstructor() {
        SubsystemPermission fullPermission = new SubsystemPermission(
                1L, mockSubsystem, mockPermission, 3, true, 
                "system", now, "admin", now
        );
        
        assertEquals(1L, fullPermission.getId());
        assertEquals(mockSubsystem, fullPermission.getSubsystem());
        assertEquals(mockPermission, fullPermission.getPermission());
        assertEquals(3, fullPermission.getAccessLevel());
        assertTrue(fullPermission.getIsDefault());
        assertEquals("system", fullPermission.getCreatedBy());
        assertEquals(now, fullPermission.getCreatedTime());
        assertEquals("admin", fullPermission.getUpdatedBy());
        assertEquals(now, fullPermission.getUpdatedTime());
    }

    // 基本属性测试
    @Test
    @DisplayName("测试基本属性设置")
    void testBasicProperties() {
        // 创建新的模拟对象用于测试
        Subsystem newMockSubsystem = mock(Subsystem.class);
        Permission newMockPermission = mock(Permission.class);
        
        when(newMockSubsystem.getId()).thenReturn(2L);
        when(newMockPermission.getId()).thenReturn(102L);
        
        subsystemPermission.setId(1L);
        subsystemPermission.setSubsystem(newMockSubsystem);
        subsystemPermission.setPermission(newMockPermission);
        subsystemPermission.setAccessLevel(3);
        subsystemPermission.setIsDefault(true);
        
        assertEquals(1L, subsystemPermission.getId());
        assertEquals(newMockSubsystem, subsystemPermission.getSubsystem());
        assertEquals(newMockPermission, subsystemPermission.getPermission());
        assertEquals(3, subsystemPermission.getAccessLevel());
        assertTrue(subsystemPermission.getIsDefault());
    }

    // 权限检查方法测试
    @CsvSource({
        "0, false, false, false, false",
        "1, true, true, false, false",
        "2, true, true, true, false",
        "3, true, true, true, true",
        "null, false, false, false, false"
    })
    @ParameterizedTest
    @DisplayName("测试权限检查方法")
    void testPermissionChecks(Integer accessLevel, boolean expectedAccess, 
                              boolean expectedRead, boolean expectedWrite, 
                              boolean expectedAdmin) {
        subsystemPermission.setAccessLevel("null".equals(accessLevel) ? null : accessLevel);
        
        assertEquals(expectedAccess, subsystemPermission.hasAccessPermission());
        assertEquals(expectedRead, subsystemPermission.hasReadPermission());
        assertEquals(expectedWrite, subsystemPermission.hasWritePermission());
        assertEquals(expectedAdmin, subsystemPermission.hasAdminPermission());
    }

    // equals和hashCode测试
    @Test
    @DisplayName("测试equals方法 - 相同对象")
    void testEquals_SameObject() {
        assertTrue(subsystemPermission.equals(subsystemPermission));
    }

    @Test
    @DisplayName("测试equals方法 - 空对象")
    void testEquals_NullObject() {
        assertFalse(subsystemPermission.equals(null));
    }

    @Test
    @DisplayName("测试equals方法 - 不同类")
    void testEquals_DifferentClass() {
        assertFalse(subsystemPermission.equals(new Object()));
    }

    @Test
    @DisplayName("测试equals方法 - 相同子系统和权限")
    void testEquals_SameSubsystemAndPermission() {
        SubsystemPermission permission2 = SubsystemPermission.builder()
                .subsystem(mockSubsystem)
                .permission(mockPermission)
                .accessLevel(1)
                .build();
        
        assertTrue(subsystemPermission.equals(permission2));
        assertEquals(subsystemPermission.hashCode(), permission2.hashCode());
    }

    @Test
    @DisplayName("测试equals方法 - 不同子系统")
    void testEquals_DifferentSubsystem() {
        Subsystem mockSubsystem2 = mock(Subsystem.class);
        when(mockSubsystem2.getId()).thenReturn(2L);
        
        SubsystemPermission permission2 = SubsystemPermission.builder()
                .subsystem(mockSubsystem2)
                .permission(mockPermission)
                .accessLevel(2)
                .build();
        
        assertFalse(subsystemPermission.equals(permission2));
        assertNotEquals(subsystemPermission.hashCode(), permission2.hashCode());
    }

    @Test
    @DisplayName("测试equals方法 - 不同权限")
    void testEquals_DifferentPermission() {
        Permission mockPermission2 = mock(Permission.class);
        when(mockPermission2.getId()).thenReturn(102L);
        
        SubsystemPermission permission2 = SubsystemPermission.builder()
                .subsystem(mockSubsystem)
                .permission(mockPermission2)
                .accessLevel(2)
                .build();
        
        assertFalse(subsystemPermission.equals(permission2));
        assertNotEquals(subsystemPermission.hashCode(), permission2.hashCode());
    }

    // 审计字段测试
    @Test
    @DisplayName("测试审计字段")
    void testAuditFields() {
        subsystemPermission.setCreatedBy("user123");
        subsystemPermission.setCreatedTime(now.plusMinutes(5));
        subsystemPermission.setUpdatedBy("admin456");
        subsystemPermission.setUpdatedTime(now.plusMinutes(10));
        
        assertEquals("user123", subsystemPermission.getCreatedBy());
        assertEquals(now.plusMinutes(5), subsystemPermission.getCreatedTime());
        assertEquals("admin456", subsystemPermission.getUpdatedBy());
        assertEquals(now.plusMinutes(10), subsystemPermission.getUpdatedTime());
    }

    // 边界情况测试
    @Test
    @DisplayName("测试边界情况 - 空值")
    void testEdgeCases_NullValues() {
        SubsystemPermission edgeCase = new SubsystemPermission();
        assertNull(edgeCase.getId());
        assertNull(edgeCase.getSubsystem());
        assertNull(edgeCase.getPermission());
        assertNull(edgeCase.getAccessLevel());
        assertFalse(edgeCase.getIsDefault());
        
        // 验证默认值
        assertEquals(Boolean.FALSE, edgeCase.getIsDefault());
        
        // 验证权限检查
        assertFalse(edgeCase.hasAccessPermission());
        assertFalse(edgeCase.hasReadPermission());
        assertFalse(edgeCase.hasWritePermission());
        assertFalse(edgeCase.hasAdminPermission());
    }

    @Test
    @DisplayName("测试边界情况 - 访问级别边界值")
    void testEdgeCases_AccessLevelBoundaries() {
        // 测试最小值
        subsystemPermission.setAccessLevel(0);
        assertEquals(0, subsystemPermission.getAccessLevel());
        assertFalse(subsystemPermission.hasAccessPermission());
        
        // 测试最大值
        subsystemPermission.setAccessLevel(3);
        assertEquals(3, subsystemPermission.getAccessLevel());
        assertTrue(subsystemPermission.hasAdminPermission());
        
        // 测试超过最大值
        subsystemPermission.setAccessLevel(4);
        assertEquals(4, subsystemPermission.getAccessLevel());
        assertTrue(subsystemPermission.hasAdminPermission());
        
        // 测试负数
        subsystemPermission.setAccessLevel(-1);
        assertEquals(-1, subsystemPermission.getAccessLevel());
        assertFalse(subsystemPermission.hasAccessPermission());
    }

    @Test
    @DisplayName("测试默认权限设置")
    void testDefaultPermission() {
        subsystemPermission.setIsDefault(true);
        assertTrue(subsystemPermission.getIsDefault());
        
        subsystemPermission.setIsDefault(false);
        assertFalse(subsystemPermission.getIsDefault());
        
        subsystemPermission.setIsDefault(null);
        assertNull(subsystemPermission.getIsDefault());
    }
}
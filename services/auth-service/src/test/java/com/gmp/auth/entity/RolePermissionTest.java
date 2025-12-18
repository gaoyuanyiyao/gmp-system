package com.gmp.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * RolePermission实体类的综合测试
 * 测试所有属性、方法和业务逻辑
 */
class RolePermissionTest {

    private RolePermission rolePermission;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        rolePermission = RolePermission.builder()
                .roleId(1L)
                .permissionId(101L)
                .isActive(true)
                .createdBy(1001L)
                .updatedBy(1001L)
                .build();
    }

    // 基础构建器和构造函数测试
    @Test
    @DisplayName("测试构建器模式创建对象")
    void testBuilder() {
        assertEquals(1L, rolePermission.getRoleId());
        assertEquals(101L, rolePermission.getPermissionId());
        assertTrue(rolePermission.getIsActive());
        assertEquals(1001L, rolePermission.getCreatedBy());
        assertEquals(1001L, rolePermission.getUpdatedBy());
    }

    @Test
    @DisplayName("测试无参构造函数")
    void testNoArgsConstructor() {
        RolePermission emptyRolePermission = new RolePermission();
        assertNotNull(emptyRolePermission);
    }

    @Test
    @DisplayName("测试全参构造函数")
    void testAllArgsConstructor() {
        RolePermission fullRolePermission = new RolePermission(
                1L, 1L, 101L, true, now, now, 1001L, 1001L, 1
        );
        
        assertEquals(1L, fullRolePermission.getId());
        assertEquals(1L, fullRolePermission.getRoleId());
        assertEquals(101L, fullRolePermission.getPermissionId());
        assertTrue(fullRolePermission.getIsActive());
        assertEquals(now, fullRolePermission.getCreatedAt());
        assertEquals(now, fullRolePermission.getUpdatedAt());
        assertEquals(1001L, fullRolePermission.getCreatedBy());
        assertEquals(1001L, fullRolePermission.getUpdatedBy());
        assertEquals(1, fullRolePermission.getVersion());
    }

    // 基本属性测试
    @Test
    @DisplayName("测试基本属性设置")
    void testBasicProperties() {
        rolePermission.setId(2L);
        rolePermission.setRoleId(2L);
        rolePermission.setPermissionId(102L);
        rolePermission.setIsActive(false);
        
        assertEquals(2L, rolePermission.getId());
        assertEquals(2L, rolePermission.getRoleId());
        assertEquals(102L, rolePermission.getPermissionId());
        assertFalse(rolePermission.getIsActive());
    }

    // isValid方法测试
    @ParameterizedTest
    @CsvSource({
        "true, true",
        "false, false"
    })
    @DisplayName("测试isValid方法")
    void testIsValid(boolean isActive, boolean expectedValid) {
        rolePermission.setIsActive(isActive);
        assertEquals(expectedValid, rolePermission.isValid());
    }

    // 审计字段测试
    @Test
    @DisplayName("测试审计字段")
    void testAuditFields() {
        rolePermission.setCreatedAt(now);
        rolePermission.setUpdatedAt(now);
        rolePermission.setCreatedBy(1002L);
        rolePermission.setUpdatedBy(1002L);
        
        assertEquals(now, rolePermission.getCreatedAt());
        assertEquals(now, rolePermission.getUpdatedAt());
        assertEquals(1002L, rolePermission.getCreatedBy());
        assertEquals(1002L, rolePermission.getUpdatedBy());
    }

    // 版本乐观锁测试
    @Test
    @DisplayName("测试版本乐观锁")
    void testVersionLocking() {
        assertEquals(1, rolePermission.getVersion());
        rolePermission.setVersion(2);
        assertEquals(2, rolePermission.getVersion());
    }

    // 边界情况测试
    @Test
    @DisplayName("测试边界情况")
    void testEdgeCases() {
        // 测试空值
        RolePermission edgeCase = new RolePermission();
        assertNull(edgeCase.getId());
        assertNull(edgeCase.getRoleId());
        assertNull(edgeCase.getPermissionId());
        assertEquals(1, edgeCase.getVersion());
        
        // 测试零值
        edgeCase.setRoleId(0L);
        edgeCase.setPermissionId(0L);
        assertEquals(0L, edgeCase.getRoleId());
        assertEquals(0L, edgeCase.getPermissionId());
    }
}
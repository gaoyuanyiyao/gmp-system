package com.gmp.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Subsystem实体类的综合测试
 * 测试所有属性、方法和业务逻辑
 */
class SubsystemTest {

    private Subsystem subsystem;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        subsystem = Subsystem.builder()
                .subsystemCode("AUTH")
                .subsystemName("认证子系统")
                .description("用户认证和授权管理子系统")
                .iconPath("/icons/auth.png")
                .sortOrder(1)
                .enabled(true)
                .gmpCritical(true)
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
        assertEquals("AUTH", subsystem.getSubsystemCode());
        assertEquals("认证子系统", subsystem.getSubsystemName());
        assertEquals("用户认证和授权管理子系统", subsystem.getDescription());
        assertEquals("/icons/auth.png", subsystem.getIconPath());
        assertEquals(1, subsystem.getSortOrder());
        assertTrue(subsystem.getEnabled());
        assertTrue(subsystem.getGmpCritical());
        assertEquals("system", subsystem.getCreatedBy());
        assertEquals(now, subsystem.getCreatedTime());
        assertEquals("admin", subsystem.getUpdatedBy());
        assertEquals(now, subsystem.getUpdatedTime());
    }

    @Test
    @DisplayName("测试无参构造函数")
    void testNoArgsConstructor() {
        Subsystem emptySubsystem = new Subsystem();
        assertNotNull(emptySubsystem);
    }

    @Test
    @DisplayName("测试全参构造函数")
    void testAllArgsConstructor() {
        Subsystem fullSubsystem = new Subsystem(
                1L, "EDMS", "电子文档管理系统", "电子文档存储和管理", 
                "/icons/edms.png", 2, true, true, 
                "system", now, "admin", now
        );
        
        assertEquals(1L, fullSubsystem.getId());
        assertEquals("EDMS", fullSubsystem.getSubsystemCode());
        assertEquals("电子文档管理系统", fullSubsystem.getSubsystemName());
        assertEquals("电子文档存储和管理", fullSubsystem.getDescription());
        assertEquals("/icons/edms.png", fullSubsystem.getIconPath());
        assertEquals(2, fullSubsystem.getSortOrder());
        assertTrue(fullSubsystem.getEnabled());
        assertTrue(fullSubsystem.getGmpCritical());
        assertEquals("system", fullSubsystem.getCreatedBy());
        assertEquals(now, fullSubsystem.getCreatedTime());
        assertEquals("admin", fullSubsystem.getUpdatedBy());
        assertEquals(now, fullSubsystem.getUpdatedTime());
    }

    // 基本属性测试
    @Test
    @DisplayName("测试基本属性设置")
    void testBasicProperties() {
        subsystem.setId(1L);
        subsystem.setSubsystemCode("EDMS");
        subsystem.setSubsystemName("电子文档管理系统");
        subsystem.setDescription("电子文档存储和管理");
        subsystem.setIconPath("/icons/edms.png");
        subsystem.setSortOrder(2);
        subsystem.setEnabled(false);
        subsystem.setGmpCritical(false);
        
        assertEquals(1L, subsystem.getId());
        assertEquals("EDMS", subsystem.getSubsystemCode());
        assertEquals("电子文档管理系统", subsystem.getSubsystemName());
        assertEquals("电子文档存储和管理", subsystem.getDescription());
        assertEquals("/icons/edms.png", subsystem.getIconPath());
        assertEquals(2, subsystem.getSortOrder());
        assertFalse(subsystem.getEnabled());
        assertFalse(subsystem.getGmpCritical());
    }

    // isValid方法测试
    @ParameterizedTest
    @CsvSource({
        "AUTH, 认证子系统, true, true",
        "AUTH, 认证子系统, false, false",
        ", 认证子系统, true, false",
        "AUTH, , true, false"
    })
    @DisplayName("测试isValid方法")
    void testIsValid(String subsystemCode, String subsystemName, boolean enabled, boolean expectedValid) {
        subsystem.setSubsystemCode(subsystemCode);
        subsystem.setSubsystemName(subsystemName);
        subsystem.setEnabled(enabled);
        assertEquals(expectedValid, subsystem.isValid());
    }

    @Test
    @DisplayName("测试isValid方法 - null值")
    void testIsValidNullValues() {
        subsystem.setSubsystemCode(null);
        subsystem.setSubsystemName("认证子系统");
        subsystem.setEnabled(true);
        assertFalse(subsystem.isValid());
        
        subsystem.setSubsystemCode("AUTH");
        subsystem.setSubsystemName(null);
        subsystem.setEnabled(true);
        assertFalse(subsystem.isValid());
    }

    // equals和hashCode测试
    @Test
    @DisplayName("测试equals方法 - 相同对象")
    void testEquals_SameObject() {
        assertTrue(subsystem.equals(subsystem));
    }

    @Test
    @DisplayName("测试equals方法 - 空对象")
    void testEquals_NullObject() {
        assertFalse(subsystem.equals(null));
    }

    @Test
    @DisplayName("测试equals方法 - 不同类")
    void testEquals_DifferentClass() {
        assertFalse(subsystem.equals(new Object()));
    }

    @Test
    @DisplayName("测试equals方法 - 相同ID和编码")
    void testEquals_SameIdAndCode() {
        Subsystem subsystem2 = Subsystem.builder()
                .id(1L)
                .subsystemCode("AUTH")
                .subsystemName("认证子系统")
                .build();
        
        subsystem.setId(1L);
        assertTrue(subsystem.equals(subsystem2));
        assertEquals(subsystem.hashCode(), subsystem2.hashCode());
    }

    @Test
    @DisplayName("测试equals方法 - 不同ID")
    void testEquals_DifferentId() {
        Subsystem subsystem2 = Subsystem.builder()
                .id(2L)
                .subsystemCode("AUTH")
                .build();
        
        subsystem.setId(1L);
        assertFalse(subsystem.equals(subsystem2));
        assertNotEquals(subsystem.hashCode(), subsystem2.hashCode());
    }

    @Test
    @DisplayName("测试equals方法 - 不同编码")
    void testEquals_DifferentCode() {
        Subsystem subsystem2 = Subsystem.builder()
                .subsystemCode("EDMS")
                .build();
        
        assertFalse(subsystem.equals(subsystem2));
        assertNotEquals(subsystem.hashCode(), subsystem2.hashCode());
    }

    // 审计字段测试
    @Test
    @DisplayName("测试审计字段")
    void testAuditFields() {
        subsystem.setCreatedBy("user123");
        subsystem.setCreatedTime(now.plusMinutes(5));
        subsystem.setUpdatedBy("admin456");
        subsystem.setUpdatedTime(now.plusMinutes(10));
        
        assertEquals("user123", subsystem.getCreatedBy());
        assertEquals(now.plusMinutes(5), subsystem.getCreatedTime());
        assertEquals("admin456", subsystem.getUpdatedBy());
        assertEquals(now.plusMinutes(10), subsystem.getUpdatedTime());
    }

    // 边界情况测试
    @Test
    @DisplayName("测试边界情况 - 空值")
    void testEdgeCases_NullValues() {
        Subsystem edgeCase = new Subsystem();
        assertNull(edgeCase.getId());
        assertNull(edgeCase.getSubsystemCode());
        assertNull(edgeCase.getSubsystemName());
        assertNull(edgeCase.getDescription());
        assertNull(edgeCase.getIconPath());
        assertNull(edgeCase.getSortOrder());
        assertTrue(edgeCase.getEnabled());
        assertFalse(edgeCase.getGmpCritical());
        
        // 验证默认值
        assertEquals(Boolean.TRUE, edgeCase.getEnabled());
        assertEquals(Boolean.FALSE, edgeCase.getGmpCritical());
        
        // 验证无效状态
        assertFalse(edgeCase.isValid());
    }

    @Test
    @DisplayName("测试边界情况 - 空字符串")
    void testEdgeCases_EmptyStrings() {
        subsystem.setSubsystemCode("");
        subsystem.setSubsystemName("");
        subsystem.setDescription("");
        subsystem.setIconPath("");
        subsystem.setCreatedBy("");
        subsystem.setUpdatedBy("");
        
        assertEquals("", subsystem.getSubsystemCode());
        assertEquals("", subsystem.getSubsystemName());
        assertEquals("", subsystem.getDescription());
        assertEquals("", subsystem.getIconPath());
        assertEquals("", subsystem.getCreatedBy());
        assertEquals("", subsystem.getUpdatedBy());
        
        // 验证无效状态
        assertFalse(subsystem.isValid());
    }

    @Test
    @DisplayName("测试边界情况 - 排序号")
    void testEdgeCases_SortOrder() {
        subsystem.setSortOrder(0);
        assertEquals(0, subsystem.getSortOrder());
        
        subsystem.setSortOrder(100);
        assertEquals(100, subsystem.getSortOrder());
        
        subsystem.setSortOrder(null);
        assertNull(subsystem.getSortOrder());
    }

    // GMP关键子系统测试
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("测试GMP关键子系统设置")
    void testGmpCriticalSetting(boolean gmpCritical) {
        subsystem.setGmpCritical(gmpCritical);
        assertEquals(gmpCritical, subsystem.getGmpCritical());
    }
}
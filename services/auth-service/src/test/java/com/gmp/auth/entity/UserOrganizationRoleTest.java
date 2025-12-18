package com.gmp.auth.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import com.gmp.auth.entity.UserOrganizationRole.AssignmentStatus;

/**
 * UserOrganizationRole实体类的综合测试
 * 测试所有属性、方法和业务逻辑
 */
@ExtendWith(MockitoExtension.class)
class UserOrganizationRoleTest {

    private UserOrganizationRole userOrgRole;
    private LocalDateTime now;
    private LocalDateTime tomorrow;
    private LocalDateTime oneMonthLater;
    private LocalDateTime oneYearLater;
    private LocalDateTime pastDate;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        tomorrow = now.plusDays(1);
        oneMonthLater = now.plusMonths(1);
        oneYearLater = now.plusYears(1);
        pastDate = now.minusDays(1);
        
        userOrgRole = UserOrganizationRole.builder()
                .id(1L)
                .userId(1001L)
                .organizationId(2001L)
                .roleId(3001L)
                .status(AssignmentStatus.ACTIVE)
                .assignedBy(1L) // 使用Long类型而不是String
                .assignedAt(now)
                .effectiveFrom(now)
                .effectiveUntil(oneYearLater)
                .build();
    }

    // 基础构建器和构造函数测试
    @Test
    @DisplayName("测试构建器模式创建对象")
    void testBuilder() {
        // 验证构建的对象属性
        assertEquals(1L, userOrgRole.getId());
        assertEquals(1001L, userOrgRole.getUserId());
        assertEquals(2001L, userOrgRole.getOrganizationId());
        assertEquals(3001L, userOrgRole.getRoleId());
        assertEquals(AssignmentStatus.ACTIVE, userOrgRole.getStatus());
        assertEquals("admin", userOrgRole.getAssignedBy());
        assertEquals(now, userOrgRole.getAssignedAt());
        assertEquals(now, userOrgRole.getEffectiveFrom());
        assertEquals(oneYearLater, userOrgRole.getEffectiveUntil());
    }

    @Test
    @DisplayName("测试无参构造函数")
    void testNoArgsConstructor() {
        UserOrganizationRole emptyOrgRole = new UserOrganizationRole();
        assertNotNull(emptyOrgRole);
    }

    // AssignmentStatus枚举测试
    @EnumSource(AssignmentStatus.class)
    @ParameterizedTest
    @DisplayName("测试AssignmentStatus枚举值")
    void testAssignmentStatusEnum(AssignmentStatus status) {
        userOrgRole.setStatus(status);
        assertEquals(status, userOrgRole.getStatus());
    }

    @Test
    @DisplayName("测试AssignmentStatus的isActiveStatus方法")
    void testIsActiveStatus() {
        assertTrue(AssignmentStatus.ACTIVE.isActiveStatus());
        assertTrue(AssignmentStatus.APPROVED.isActiveStatus());
        assertFalse(AssignmentStatus.PENDING.isActiveStatus());
        assertFalse(AssignmentStatus.EXPIRED.isActiveStatus());
        assertFalse(AssignmentStatus.REVOKED.isActiveStatus());
        assertFalse(AssignmentStatus.SUSPENDED.isActiveStatus());
        assertFalse(AssignmentStatus.REJECTED.isActiveStatus());
    }

    @Test
    @DisplayName("测试AssignmentStatus的toString和valueOf方法")
    void testAssignmentStatusStringMethods() {
        assertEquals("ACTIVE", AssignmentStatus.ACTIVE.toString());
        assertEquals(AssignmentStatus.ACTIVE, AssignmentStatus.valueOf("ACTIVE"));
    }

    // 有效性检查测试
    @Test
    @DisplayName("测试isEffective方法 - 当前有效")
    void testIsEffective_ActiveNow() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(now);
        userOrgRole.setEffectiveUntil(oneYearLater);
        assertTrue(userOrgRole.isEffective());
    }

    @Test
    @DisplayName("测试isEffective方法 - 未来生效")
    void testIsEffective_EffectiveInFuture() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(tomorrow);
        userOrgRole.setEffectiveUntil(oneYearLater);
        assertFalse(userOrgRole.isEffective());
    }

    @Test
    @DisplayName("测试isEffective方法 - 已过期")
    void testIsEffective_Expired() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(pastDate);
        userOrgRole.setEffectiveUntil(pastDate);
        assertFalse(userOrgRole.isEffective());
    }

    @Test
    @DisplayName("测试isEffective方法 - 非活跃状态")
    void testIsEffective_InactiveStatus() {
        userOrgRole.setStatus(AssignmentStatus.SUSPENDED);
        userOrgRole.setEffectiveFrom(now);
        userOrgRole.setEffectiveUntil(oneYearLater);
        assertFalse(userOrgRole.isEffective());
    }

    @Test
    @DisplayName("测试isExpiringSoon方法 - 即将过期")
    void testIsExpiringSoon_ExpiringSoon() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(now);
        userOrgRole.setEffectiveUntil(now.plusDays(7)); // 7天后过期
        assertTrue(userOrgRole.isExpiringSoon(30)); // 30天内过期
    }

    @Test
    @DisplayName("测试isExpiringSoon方法 - 不会很快过期")
    void testIsExpiringSoon_NotExpiringSoon() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(now);
        userOrgRole.setEffectiveUntil(oneMonthLater); // 1个月后过期
        assertFalse(userOrgRole.isExpiringSoon(15)); // 15天内不过期
    }

    @Test
    @DisplayName("测试isExpired方法 - 已过期")
    void testIsExpired_Expired() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(pastDate);
        userOrgRole.setEffectiveUntil(pastDate);
        assertTrue(userOrgRole.isExpired());
    }

    @Test
    @DisplayName("测试isExpired方法 - 未过期")
    void testIsExpired_NotExpired() {
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        userOrgRole.setEffectiveFrom(now);
        userOrgRole.setEffectiveUntil(oneYearLater);
        assertFalse(userOrgRole.isExpired());
    }

    @Test
    @DisplayName("测试getRemainingDays方法")
    void testGetRemainingDays() {
        userOrgRole.setEffectiveUntil(now.plusDays(30));
        assertTrue(userOrgRole.getRemainingDays() >= 29);
        assertTrue(userOrgRole.getRemainingDays() <= 30);
    }

    @Test
    @DisplayName("测试getRemainingDays方法 - 已过期")
    void testGetRemainingDays_Expired() {
        userOrgRole.setEffectiveUntil(pastDate);
        assertEquals(0, userOrgRole.getRemainingDays());
    }

    // GMP合规性测试
    @Test
    @DisplayName("测试GMP要求设置")
    void testGmpRequirements() {
        userOrgRole.setGmpRequired(true);
        userOrgRole.setQualificationRequired("Required");
        userOrgRole.setTrainingRequired("Required");
        
        assertTrue(userOrgRole.getGmpRequired());
        assertEquals("Required", userOrgRole.getQualificationRequired());
        assertEquals("Required", userOrgRole.getTrainingRequired());
    }

    @Test
    @DisplayName("测试非GMP要求设置")
    void testNonGmpRequirements() {
        userOrgRole.setGmpRequired(false);
        userOrgRole.setQualificationRequired("Not Required");
        userOrgRole.setTrainingRequired("Not Required");
        
        assertFalse(userOrgRole.getGmpRequired());
        assertEquals("Not Required", userOrgRole.getQualificationRequired());
        assertEquals("Not Required", userOrgRole.getTrainingRequired());
    }

    // 审批工作流测试
    @Test
    @DisplayName("测试审批要求")
    void testApprovalRequired() {
        userOrgRole.setApprovalRequired(true);
        userOrgRole.setApprovedBy(2L); // 2L是经理的ID
        userOrgRole.setApprovedAt(now);
        userOrgRole.setApprovalNote("Approved by manager");
        
        assertTrue(userOrgRole.getApprovalRequired());
        assertTrue(userOrgRole.getApprovalRequired());
        assertEquals(2L, userOrgRole.getApprovedBy());
        assertEquals(now, userOrgRole.getApprovedAt());
        assertEquals("Approved by manager", userOrgRole.getApprovalNote());
    }

    @Test
    @DisplayName("测试审批流程 - 待审批状态")
    void testPendingApprovalStatus() {
        userOrgRole.setStatus(AssignmentStatus.PENDING_APPROVAL);
        userOrgRole.setApprovalRequired(true);
        userOrgRole.setApprovedBy(null);
        userOrgRole.setApprovedAt(null);
        
        assertTrue(userOrgRole.requiresApproval());
        assertNull(userOrgRole.getApprovedBy());
        assertNull(userOrgRole.getApprovedAt());
    }

    // 移除追踪测试
    @Test
    @DisplayName("测试角色移除追踪")
    void testRoleRemovalTracking() {
        userOrgRole.setStatus(AssignmentStatus.REVOKED);
        userOrgRole.setRemovedBy("admin");
        userOrgRole.setRemovedAt(now);
        userOrgRole.setRemovalReason("Role no longer needed");
        
        assertEquals(AssignmentStatus.REVOKED, userOrgRole.getStatus());
        assertEquals("admin", userOrgRole.getRemovedBy());
        assertEquals(now, userOrgRole.getRemovedAt());
        assertEquals("Role no longer needed", userOrgRole.getRemovalReason());
    }

    // 审计字段测试
    @Test
    @DisplayName("测试审计字段")
    void testAuditFields() {
        userOrgRole.setCreatedBy("system");
        userOrgRole.setCreatedAt(now);
        userOrgRole.setUpdatedBy("admin");
        userOrgRole.setUpdatedAt(now);
        
        assertEquals("system", userOrgRole.getCreatedBy());
        assertEquals(now, userOrgRole.getCreatedAt());
        assertEquals("admin", userOrgRole.getUpdatedBy());
        assertEquals(now, userOrgRole.getUpdatedAt());
    }

    // 版本乐观锁测试
    @Test
    @DisplayName("测试版本乐观锁")
    void testVersionLocking() {
        assertEquals(0, userOrgRole.getVersion());
        userOrgRole.setVersion(1);
        assertEquals(1, userOrgRole.getVersion());
    }

    // 通知和备注测试
    @Test
    @DisplayName("测试过期通知标志")
    void testExpiresNotified() {
        userOrgRole.setExpiresNotified(false);
        assertFalse(userOrgRole.isExpiresNotified());
        
        userOrgRole.setExpiresNotified(true);
        assertTrue(userOrgRole.isExpiresNotified());
    }

    @Test
    @DisplayName("测试分配原因和备注")
    void testAssignmentReasonAndNotes() {
        String reason = "New department assignment";
        String notes = "Temporary role assignment during transition";
        
        userOrgRole.setAssignmentReason(reason);
        userOrgRole.setNotes(notes);
        
        assertEquals(reason, userOrgRole.getAssignmentReason());
        assertEquals(notes, userOrgRole.getNotes());
    }

    // 边界情况测试
    @Test
    @DisplayName("测试边界情况 - 空字符串")
    void testEdgeCases_EmptyStrings() {
        userOrgRole.setAssignedBy("");
        userOrgRole.setAssignmentReason("");
        userOrgRole.setNotes("");
        
        assertEquals("", userOrgRole.getAssignedBy());
        assertEquals("", userOrgRole.getAssignmentReason());
        assertEquals("", userOrgRole.getNotes());
    }

    @Test
    @DisplayName("测试边界情况 - 空日期")
    void testEdgeCases_NullDates() {
        userOrgRole.setEffectiveUntil(null);
        assertFalse(userOrgRole.isExpired());
        assertEquals(0, userOrgRole.getRemainingDays());
    }

    // 状态转换测试
    @Test
    @DisplayName("测试状态转换")
    void testStatusTransitions() {
        // 从待审批到活跃
        userOrgRole.setStatus(AssignmentStatus.PENDING_APPROVAL);
        assertEquals(AssignmentStatus.PENDING_APPROVAL, userOrgRole.getStatus());
        assertFalse(userOrgRole.isEffective());
        
        userOrgRole.setStatus(AssignmentStatus.ACTIVE);
        assertEquals(AssignmentStatus.ACTIVE, userOrgRole.getStatus());
        assertTrue(userOrgRole.isEffective());
        
        // 从活跃到过期
        userOrgRole.setEffectiveUntil(pastDate);
        assertTrue(userOrgRole.isExpired());
        
        // 从活跃到撤销
        userOrgRole.setStatus(AssignmentStatus.REVOKED);
        assertEquals(AssignmentStatus.REVOKED, userOrgRole.getStatus());
        assertFalse(userOrgRole.isEffective());
    }

    // 参数化测试 - 不同状态组合
    @CsvSource({
        "ACTIVE, true",
        "APPROVED, true",
        "PENDING, false",
        "EXPIRED, false",
        "REVOKED, false",
        "SUSPENDED, false",
        "REJECTED, false"
    })
    @ParameterizedTest
    @DisplayName("参数化测试 - 状态与有效性关系")
    void testStatusAndEffectiveness(String status, boolean expectedEffective) {
        userOrgRole.setStatus(AssignmentStatus.valueOf(status));
        assertEquals(expectedEffective, userOrgRole.isEffective());
    }
}
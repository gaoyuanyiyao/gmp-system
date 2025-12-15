package com.gmp.auth.service.impl;

import com.gmp.auth.entity.*;
import com.gmp.auth.repository.*;
import com.gmp.auth.service.UserOrganizationRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 测试 UserOrganizationRoleServiceImpl 类的所有方法
 */
@ExtendWith(MockitoExtension.class)
class UserOrganizationRoleServiceImplTest {

    @Mock
    private UserOrganizationRoleRepository userOrgRoleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private UserOrganizationRoleServiceImpl userOrgRoleService;

    private User testUser;
    private Organization testOrganization;
    private Role testRole;
    private UserOrganizationRole testUserOrgRole;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testOrganization = new Organization();
        testOrganization.setId(1L);
        testOrganization.setName("Test Organization");

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleCode("TEST_ROLE");

        testUserOrgRole = new UserOrganizationRole();
        testUserOrgRole.setId(1L);
        testUserOrgRole.setUserId(1L);
        testUserOrgRole.setOrganizationId(1L);
        testUserOrgRole.setRoleId(1L);
        testUserOrgRole.setStatus(UserOrganizationRole.AssignmentStatus.ACTIVE);
    }

    @Test
    void testAssignRoleToUserInOrganization_Success() {
        // 准备数据
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userOrgRoleRepository.existsByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(false);
        when(userOrgRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(testUserOrgRole);

        // 执行测试
        UserOrganizationRole result = userOrgRoleService.assignRoleToUserInOrganization(
                1L, 1L, 1L, 2L, "Test assignment", LocalDateTime.now().plusDays(30));

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userOrgRoleRepository).save(any(UserOrganizationRole.class));
    }

    @Test
    void testAssignRoleToUserInOrganization_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userOrgRoleService.assignRoleToUserInOrganization(1L, 1L, 1L, 2L, "Test", null));

        assertEquals("用户不存在: 1", exception.getMessage());
    }

    @Test
    void testAssignRoleToUserInOrganization_OrganizationNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userOrgRoleService.assignRoleToUserInOrganization(1L, 1L, 1L, 2L, "Test", null));

        assertEquals("组织不存在: 1", exception.getMessage());
    }

    @Test
    void testAssignRoleToUserInOrganization_RoleNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userOrgRoleService.assignRoleToUserInOrganization(1L, 1L, 1L, 2L, "Test", null));

        assertEquals("角色不存在: 1", exception.getMessage());
    }

    @Test
    void testAssignRoleToUserInOrganization_RoleAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userOrgRoleRepository.existsByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(true);

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userOrgRoleService.assignRoleToUserInOrganization(1L, 1L, 1L, 2L, "Test", null));

        assertEquals("用户在该组织中已拥有此角色", exception.getMessage());
    }

    @Test
    void testAssignRolesToUserInOrganizations_Success() {
        // 准备数据
        Map<Long, List<Long>> orgRoleAssignments = new HashMap<>();
        orgRoleAssignments.put(1L, Arrays.asList(1L, 2L));
        orgRoleAssignments.put(2L, Arrays.asList(3L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(anyLong())).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(testRole));
        when(userOrgRoleRepository.existsByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(false);
        when(userOrgRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(testUserOrgRole);

        // 执行测试
        List<UserOrganizationRole> results = userOrgRoleService.assignRolesToUserInOrganizations(
                1L, orgRoleAssignments, 2L, "Bulk assignment");

        // 验证结果
        assertNotNull(results);
        assertEquals(3, results.size());
        verify(userOrgRoleRepository, times(3)).save(any(UserOrganizationRole.class));
    }

    @Test
    void testRemoveRoleFromUserInOrganization_Success() {
        // 准备数据
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(Optional.of(testUserOrgRole));
        when(userOrgRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(testUserOrgRole);

        // 执行测试
        userOrgRoleService.removeRoleFromUserInOrganization(1L, 1L, 1L);

        // 验证结果
        assertEquals(UserOrganizationRole.AssignmentStatus.REVOKED, testUserOrgRole.getStatus());
        verify(userOrgRoleRepository).save(testUserOrgRole);
    }

    @Test
    void testRemoveRoleFromUserInOrganization_RoleNotFound() {
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(Optional.empty());

        // 验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userOrgRoleService.removeRoleFromUserInOrganization(1L, 1L, 1L));

        assertEquals("用户在该组织中没有此角色", exception.getMessage());
    }

    @Test
    void testGetUserOrganizationRoles() {
        // 准备数据
        List<UserOrganizationRole> expectedRoles = Arrays.asList(testUserOrgRole);
        when(userOrgRoleRepository.findByUserIdAndStatusIn(1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(expectedRoles);

        // 执行测试
        List<UserOrganizationRole> results = userOrgRoleService.getUserOrganizationRoles(1L);

        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testUserOrgRole, results.get(0));
    }

    @Test
    void testGetUserOrganizationRolesInOrganization() {
        // 准备数据
        List<UserOrganizationRole> expectedRoles = Arrays.asList(testUserOrgRole);
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndStatusIn(1L, 1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(expectedRoles);

        // 执行测试
        List<UserOrganizationRole> results = userOrgRoleService.getUserOrganizationRolesInOrganization(1L, 1L);

        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testUserOrgRole, results.get(0));
    }

    @Test
    void testGetUserOrganizations() {
        // 执行测试
        Set<Long> results = userOrgRoleService.getUserOrganizations(1L);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetUserRoleCodesInOrganization() {
        // 准备数据
        List<UserOrganizationRole> userOrgRoles = Arrays.asList(testUserOrgRole);
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndStatusIn(1L, 1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(userOrgRoles);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // 执行测试
        Set<String> results = userOrgRoleService.getUserRoleCodesInOrganization(1L, 1L);

        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.contains("TEST_ROLE"));
    }

    @Test
    void testGetUserRoleCodesInOrganization_NoRoles() {
        // 准备数据
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndStatusIn(1L, 1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(Collections.emptyList());

        // 执行测试
        Set<String> results = userOrgRoleService.getUserRoleCodesInOrganization(1L, 1L);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetUserRoleCodesInOrganization_RoleNotFound() {
        // 准备数据
        List<UserOrganizationRole> userOrgRoles = Arrays.asList(testUserOrgRole);
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndStatusIn(1L, 1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(userOrgRoles);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试
        Set<String> results = userOrgRoleService.getUserRoleCodesInOrganization(1L, 1L);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetUserPermissionCodesInOrganization() {
        // 准备数据
        when(userOrgRoleRepository.findByUserIdAndOrganizationIdAndStatusIn(1L, 1L, UserOrganizationRoleServiceImpl.ACTIVE_STATUSES))
                .thenReturn(Arrays.asList(testUserOrgRole));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        // 执行测试
        Set<String> results = userOrgRoleService.getUserPermissionCodesInOrganization(1L, 1L);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty()); // 当前实现返回空集合
    }

    @Test
    void testGetUserPermissionCodesAcrossOrganizations() {
        // 执行测试
        Set<String> results = userOrgRoleService.getUserPermissionCodesAcrossOrganizations(1L);

        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty()); // 当前实现返回空集合
    }

    @Test
    void testActiveStatuses() {
        // 验证ACTIVE_STATUSES集合的正确性
        Set<UserOrganizationRole.AssignmentStatus> activeStatuses = UserOrganizationRoleServiceImpl.ACTIVE_STATUSES;
        
        assertNotNull(activeStatuses);
        assertEquals(2, activeStatuses.size());
        assertTrue(activeStatuses.contains(UserOrganizationRole.AssignmentStatus.ACTIVE));
        assertTrue(activeStatuses.contains(UserOrganizationRole.AssignmentStatus.APPROVED));
        assertFalse(activeStatuses.contains(UserOrganizationRole.AssignmentStatus.REVOKED));
        assertFalse(activeStatuses.contains(UserOrganizationRole.AssignmentStatus.PENDING));
    }

    @Test
    void testAssignRoleWithNullParameters() {
        // 测试空值处理
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userOrgRoleRepository.existsByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(false);
        when(userOrgRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(testUserOrgRole);

        // 测试空字符串赋值原因
        UserOrganizationRole result = userOrgRoleService.assignRoleToUserInOrganization(
                1L, 1L, 1L, 2L, "", null);

        assertNotNull(result);

        // 测试null赋值原因
        result = userOrgRoleService.assignRoleToUserInOrganization(
                1L, 1L, 1L, 2L, null, null);

        assertNotNull(result);
    }

    @Test
    void testEdgeCases() {
        // 测试边界值
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userOrgRoleRepository.existsByUserIdAndOrganizationIdAndRoleIdAndStatusIn(anyLong(), anyLong(), anyLong(), anySet()))
                .thenReturn(false);
        when(userOrgRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(testUserOrgRole);

        // 测试极长的赋值原因
        String longReason = "a".repeat(1000);
        UserOrganizationRole result = userOrgRoleService.assignRoleToUserInOrganization(
                1L, 1L, 1L, 2L, longReason, LocalDateTime.now().plusYears(10));

        assertNotNull(result);

        // 测试过去的有效期
        result = userOrgRoleService.assignRoleToUserInOrganization(
                1L, 1L, 1L, 2L, "Test", LocalDateTime.now().minusDays(1));

        assertNotNull(result);
    }
}
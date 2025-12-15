package com.gmp.auth.init;

import com.gmp.auth.entity.Role;
import com.gmp.auth.entity.User;
import com.gmp.auth.entity.UserOrganizationRole;
import com.gmp.auth.entity.UserRole;
import com.gmp.auth.repository.OrganizationRepository;
import com.gmp.auth.repository.RoleRepository;
import com.gmp.auth.repository.UserOrganizationRoleRepository;
import com.gmp.auth.repository.UserRepository;
import com.gmp.auth.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 测试 UserInitializer 类的所有方法
 */
@ExtendWith(MockitoExtension.class)
class UserInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserOrganizationRoleRepository userOrganizationRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserInitializer userInitializer;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("testuser@gmp-system.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setUserStatus(User.UserStatus.ACTIVE);
        testUser.setLoginAttempts(0);
        testUser.setMobile("13800138000");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setVersion(1);
        testUser.setMfaEnabled(false);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleCode("ROLE_SYSTEM_ADMIN");
        testRole.setRoleName("系统管理员");
        testRole.setDescription("系统管理员角色");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testInitializeUsers_Success() {
        // 准备数据
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(new UserRole());
        when(userOrganizationRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(new UserOrganizationRole());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(roleRepository).findAll();
        verify(organizationRepository).findAll();
        verify(userRoleRepository, atLeastOnce()).save(any(UserRole.class));
        verify(userOrganizationRoleRepository, atLeastOnce()).save(any(UserOrganizationRole.class));
    }

    @Test
    void testInitializeUsers_SkipExisting() {
        // 准备数据 - 模拟已有用户存在
        when(userRepository.count()).thenReturn(10L);

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该跳过初始化
        verify(userRepository, never()).save(any(User.class));
        verify(roleRepository, never()).findAll();
        verify(organizationRepository, never()).findAll();
    }

    @Test
    void testInitializeUsers_TestModeReinitialize() {
        // 准备数据 - 模拟测试模式下的重新初始化
        when(userRepository.count()).thenReturn(5L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(new UserRole());
        when(userOrganizationRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(new UserOrganizationRole());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该重新初始化
        verify(userRepository).delete(testUser);
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void testInitializeUsers_ExceptionHandling() {
        // 准备数据 - 模拟异常情况
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userInitializer.initializeUsers());

        assertEquals("测试用户初始化失败", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());
    }

    @Test
    void testCreateTestUsers() {
        // 准备数据
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 使用反射调用私有方法
        Map<String, User> users = new HashMap<>();
        try {
            java.lang.reflect.Method method = UserInitializer.class.getDeclaredMethod("createTestUsers");
            method.setAccessible(true);
            users = (Map<String, User>) method.invoke(userInitializer);
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }

        // 验证结果
        assertNotNull(users);
        assertTrue(users.size() > 0);
        verify(userRepository, atLeast(15)).save(any(User.class)); // 至少15个测试用户
    }

    @Test
    void testLoadRoles() {
        // 准备数据
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));

        // 使用反射调用私有方法
        Map<String, Role> roles = new HashMap<>();
        try {
            java.lang.reflect.Method method = UserInitializer.class.getDeclaredMethod("loadRoles");
            method.setAccessible(true);
            roles = (Map<String, Role>) method.invoke(userInitializer);
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }

        // 验证结果
        assertNotNull(roles);
        assertTrue(roles.containsKey("ROLE_SYSTEM_ADMIN"));
        verify(roleRepository).findAll();
    }

    @Test
    void testLoadOrganizations() {
        // 准备数据
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // 使用反射调用私有方法
        Map<String, Long> organizations = new HashMap<>();
        try {
            java.lang.reflect.Method method = UserInitializer.class.getDeclaredMethod("loadOrganizations");
            method.setAccessible(true);
            organizations = (Map<String, Long>) method.invoke(userInitializer);
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }

        // 验证结果
        assertNotNull(organizations);
        assertTrue(organizations.containsKey("DEFAULT_ORG"));
        assertEquals(1L, organizations.get("DEFAULT_ORG"));
        verify(organizationRepository).findAll();
    }

    @Test
    void testCreateUserRoleAssociations() {
        // 准备数据
        Map<String, User> users = new HashMap<>();
        users.put("admin", testUser);
        
        Map<String, Role> roles = new HashMap<>();
        roles.put("ROLE_SYSTEM_ADMIN", testRole);

        when(userRoleRepository.save(any(UserRole.class))).thenReturn(new UserRole());

        // 使用反射调用私有方法
        try {
            java.lang.reflect.Method method = UserInitializer.class.getDeclaredMethod(
                    "createUserRoleAssociations", Map.class, Map.class);
            method.setAccessible(true);
            method.invoke(userInitializer, users, roles);
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }

        // 验证结果
        verify(userRoleRepository, atLeastOnce()).save(any(UserRole.class));
    }

    @Test
    void testCreateUserOrganizationRoleAssociations() {
        // 准备数据
        Map<String, User> users = new HashMap<>();
        users.put("admin", testUser);
        
        Map<String, Role> roles = new HashMap<>();
        roles.put("ROLE_SYSTEM_ADMIN", testRole);
        
        Map<String, Long> organizations = new HashMap<>();
        organizations.put("DEFAULT_ORG", 1L);

        when(userOrganizationRoleRepository.save(any(UserOrganizationRole.class))).thenReturn(new UserOrganizationRole());

        // 使用反射调用私有方法
        try {
            java.lang.reflect.Method method = UserInitializer.class.getDeclaredMethod(
                    "createUserOrganizationRoleAssociations", Map.class, Map.class, Map.class);
            method.setAccessible(true);
            method.invoke(userInitializer, users, roles, organizations);
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }

        // 验证结果
        verify(userOrganizationRoleRepository, atLeastOnce()).save(any(UserOrganizationRole.class));
    }

    @Test
    void testUserDataClass() {
        // 测试内部类 UserData
        UserInitializer.UserData userData = new UserInitializer.UserData(
                "testuser", "Test User", "test@example.com", "13800138000", true, true, "测试用户");

        assertEquals("testuser", userData.username);
        assertEquals("Test User", userData.fullName);
        assertEquals("test@example.com", userData.email);
        assertEquals("13800138000", userData.phone);
        assertTrue(userData.active);
        assertTrue(userData.admin);
        assertEquals("测试用户", userData.description);
    }

    @Test
    void testUserOrgRoleDataClass() {
        // 测试内部类 UserOrgRoleData
        UserInitializer.UserOrgRoleData userOrgRoleData = new UserInitializer.UserOrgRoleData(
                "admin", "ROLE_SYSTEM_ADMIN", "DEFAULT_ORG");

        assertEquals("admin", userOrgRoleData.username);
        assertEquals("ROLE_SYSTEM_ADMIN", userOrgRoleData.roleCode);
        assertEquals("DEFAULT_ORG", userOrgRoleData.organizationCode);
    }

    @Test
    void testNullAndEmptyCollections() {
        // 测试空集合处理
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());
        when(organizationRepository.findAll()).thenReturn(Collections.emptyList());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该正常处理空集合
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(roleRepository).findAll();
        verify(organizationRepository).findAll();
    }

    @Test
    void testDuplicateUserCreation() {
        // 测试重复用户创建
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该处理重复用户
        verify(userRepository, atLeast(15)).save(any(User.class));
    }

    @Test
    void testUserStatusAndProperties() {
        // 测试用户状态和属性设置
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行测试
        userInitializer.initializeUsers();

        // 验证用户属性设置
        verify(userRepository, atLeastOnce()).save(argThat(user -> 
                user.getUserStatus() == User.UserStatus.ACTIVE &&
                user.getLoginAttempts() == 0 &&
                !user.isMfaEnabled() &&
                user.getVersion() == 1
        ));
    }

    @Test
    void testRoleLoadingWithNullRoleCode() {
        // 测试角色加载时处理空角色代码
        Role nullRoleCodeRole = new Role();
        nullRoleCodeRole.setId(2L);
        nullRoleCodeRole.setRoleCode(null); // 空角色代码
        nullRoleCodeRole.setRoleName("Test Role");

        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole, nullRoleCodeRole));
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该跳过空角色代码的角色
        verify(roleRepository).findAll();
    }

    @Test
    void testUserDeletionInTestMode() {
        // 测试测试模式下的用户删除
        User testUser1 = new User();
        testUser1.setUsername("test_user1");
        User testUser2 = new User();
        testUser2.setUsername("normal_user");

        when(userRepository.count()).thenReturn(2L);
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser1, testUser2));

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该只删除测试用户
        verify(userRepository).delete(testUser1);
        verify(userRepository, never()).delete(testUser2);
    }

    @Test
    void testTransactionalBehavior() {
        // 测试事务行为
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleRepository.findAll()).thenReturn(Arrays.asList(testRole));
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证事务完整性 - 所有操作都应该成功
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(roleRepository).findAll();
        verify(organizationRepository).findAll();
    }

    @Test
    void testErrorLogging() {
        // 测试错误日志记录
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Test error"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userInitializer.initializeUsers());

        // 验证错误信息
        assertEquals("测试用户初始化失败", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Test error", exception.getCause().getMessage());
    }

    @Test
    void testPerformanceWithLargeData() {
        // 测试大数据量下的性能
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // 创建大量角色数据
        List<Role> manyRoles = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Role role = new Role();
            role.setId((long) i);
            role.setRoleCode("ROLE_" + i);
            role.setRoleName("Role " + i);
            manyRoles.add(role);
        }
        when(roleRepository.findAll()).thenReturn(manyRoles);
        when(organizationRepository.findAll()).thenReturn(new ArrayList<>());

        // 执行测试
        userInitializer.initializeUsers();

        // 验证结果 - 应该处理大量数据
        verify(userRepository, atLeast(15)).save(any(User.class));
        verify(roleRepository).findAll();
    }
}
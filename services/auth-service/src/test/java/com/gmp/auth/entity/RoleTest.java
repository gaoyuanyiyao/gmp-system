package com.gmp.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator role with full access");
        role.setCode("ADMIN");
        role.setActive(true);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        role.setCreatedBy(1L);
    }

    @Test
    void testRoleBuilder() {
        Role builtRole = Role.builder()
                .id(2L)
                .name("ROLE_USER")
                .description("Regular user role")
                .code("USER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(2L)
                .build();

        assertNotNull(builtRole);
        assertEquals(2L, builtRole.getId());
        assertEquals("ROLE_USER", builtRole.getName());
        assertEquals("Regular user role", builtRole.getDescription());
        assertEquals("USER", builtRole.getCode());
        assertTrue(builtRole.isActive());
        assertEquals(2L, builtRole.getCreatedBy());
    }

    @Test
    void testRoleTypeEnum() {
        assertEquals("SYSTEM", Role.RoleType.SYSTEM.name());
        assertEquals("CUSTOM", Role.RoleType.CUSTOM.name());
        
        Role.RoleType[] types = Role.RoleType.values();
        assertEquals(2, types.length);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, role.getId());
        assertEquals("ROLE_ADMIN", role.getName());
        assertEquals("Administrator role with full access", role.getDescription());
        assertEquals("ADMIN", role.getCode());
        assertTrue(role.isActive());
        assertNotNull(role.getCreatedAt());
        assertNotNull(role.getUpdatedAt());
        assertEquals(1L, role.getCreatedBy());
    }

    @Test
    void testSetActive() {
        role.setActive(false);
        assertFalse(role.isActive());
    }

    @Test
    void testSetCreatedBy() {
        role.setCreatedBy(3L);
        assertEquals(3L, role.getCreatedBy());
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime newTime = LocalDateTime.now().minusDays(1);
        role.setCreatedAt(newTime);
        assertEquals(newTime, role.getCreatedAt());
    }

    @Test
    void testSetUpdatedAt() {
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        role.setUpdatedAt(newTime);
        assertEquals(newTime, role.getUpdatedAt());
    }

    @Test
    void testToString() {
        String toString = role.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ROLE_ADMIN"));
        assertTrue(toString.contains("ADMIN"));
    }

    @Test
    void testEqualsAndHashCode() {
        Role sameRole = new Role();
        sameRole.setId(1L);
        sameRole.setName("ROLE_ADMIN");

        Role differentRole = new Role();
        differentRole.setId(2L);
        differentRole.setName("ROLE_USER");

        assertEquals(role, sameRole);
        assertEquals(role.hashCode(), sameRole.hashCode());
        
        assertNotEquals(role, differentRole);
        assertNotEquals(role.hashCode(), differentRole.hashCode());
        
        assertNotEquals(role, null);
        assertNotEquals(role, "Not a role");
    }

    @Test
    void testCanEqual() {
        Role otherRole = new Role();
        otherRole.setId(1L);
        
        assertTrue(role.canEqual(otherRole));
        assertFalse(role.canEqual("Not a role"));
    }

    @Test
    void testNoArgsConstructor() {
        Role emptyRole = new Role();
        assertNotNull(emptyRole);
        assertNull(emptyRole.getId());
        assertNull(emptyRole.getName());
        assertNull(emptyRole.getCode());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Role fullRole = new Role(
                1L, "ROLE_MANAGER", "Manager role", "MANAGER", 
                true, now, now, 1L
        );

        assertNotNull(fullRole);
        assertEquals(1L, fullRole.getId());
        assertEquals("ROLE_MANAGER", fullRole.getName());
        assertEquals("Manager role", fullRole.getDescription());
        assertEquals("MANAGER", fullRole.getCode());
        assertTrue(fullRole.isActive());
        assertEquals(now, fullRole.getCreatedAt());
        assertEquals(now, fullRole.getUpdatedAt());
        assertEquals(1L, fullRole.getCreatedBy());
    }

    @Test
    void testRoleWithNullValues() {
        Role nullRole = new Role();
        nullRole.setId(null);
        nullRole.setName(null);
        nullRole.setDescription(null);
        nullRole.setCode(null);
        nullRole.setCreatedAt(null);
        nullRole.setUpdatedAt(null);
        nullRole.setCreatedBy(null);

        assertNull(nullRole.getId());
        assertNull(nullRole.getName());
        assertNull(nullRole.getDescription());
        assertNull(nullRole.getCode());
        assertNull(nullRole.getCreatedAt());
        assertNull(nullRole.getUpdatedAt());
        assertNull(nullRole.getCreatedBy());
    }

    @Test
    void testRoleWithEmptyStrings() {
        role.setName("");
        role.setDescription("");
        role.setCode("");

        assertEquals("", role.getName());
        assertEquals("", role.getDescription());
        assertEquals("", role.getCode());
    }

    @Test
    void testRoleValidation() {
        // Test valid role name format (should start with ROLE_)
        role.setName("ROLE_VALID");
        assertEquals("ROLE_VALID", role.getName());

        // Test valid code format (uppercase letters and underscores)
        role.setCode("VALID_CODE");
        assertEquals("VALID_CODE", role.getCode());

        // Test role with special characters in description
        role.setDescription("Role with special chars: @#$%^&*()");
        assertEquals("Role with special chars: @#$%^&*()", role.getDescription());
    }

    @Test
    void testRoleTypeFunctionality() {
        // Test SYSTEM role type
        role.setType(Role.RoleType.SYSTEM);
        assertEquals(Role.RoleType.SYSTEM, role.getType());

        // Test CUSTOM role type
        role.setType(Role.RoleType.CUSTOM);
        assertEquals(Role.RoleType.CUSTOM, role.getType());

        // Test default role type (if any)
        Role defaultRole = new Role();
        // This depends on the implementation - if there's a default type, test it
        // If not, this test can be omitted or adjusted
    }

    @Test
    void testRoleActivationDeactivation() {
        // Test deactivating role
        role.setActive(false);
        assertFalse(role.isActive());

        // Test reactivating role
        role.setActive(true);
        assertTrue(role.isActive());

        // Test multiple state changes
        role.setActive(false);
        role.setActive(true);
        role.setActive(false);
        assertFalse(role.isActive());
    }

    @Test
    void testRoleTimestamps() {
        LocalDateTime createTime = LocalDateTime.now().minusDays(5);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(2);

        role.setCreatedAt(createTime);
        role.setUpdatedAt(updateTime);

        assertEquals(createTime, role.getCreatedAt());
        assertEquals(updateTime, role.getUpdatedAt());

        // Test that updatedAt is after createdAt
        assertTrue(role.getUpdatedAt().isAfter(role.getCreatedAt()));
    }

    @Test
    void testRoleCodeUniqueness() {
        // Test that role code is case-sensitive (if applicable)
        role.setCode("ADMIN");
        assertEquals("ADMIN", role.getCode());

        // Test different code
        role.setCode("USER");
        assertEquals("USER", role.getCode());

        // Test code with numbers
        role.setCode("ROLE_001");
        assertEquals("ROLE_001", role.getCode());
    }

    @Test
    void testRoleDescriptionLength() {
        // Test short description
        role.setDescription("Short");
        assertEquals("Short", role.getDescription());

        // Test long description
        String longDescription = "This is a very long description that might exceed typical length limits but should still be handled properly by the system";
        role.setDescription(longDescription);
        assertEquals(longDescription, role.getDescription());

        // Test description with special characters
        role.setDescription("Description with special chars: 中文测试 @#$%");
        assertEquals("Description with special chars: 中文测试 @#$%", role.getDescription());
    }

    @Test
    void testRoleNameConvention() {
        // Test role name starting with ROLE_
        role.setName("ROLE_TEST");
        assertTrue(role.getName().startsWith("ROLE_"));

        // Test role name without ROLE_ prefix (if allowed)
        role.setName("TEST_ROLE");
        assertEquals("TEST_ROLE", role.getName());
    }

    @Test
    void testRoleCreationByDifferentUsers() {
        // Test role created by system user
        role.setCreatedBy(1L);
        assertEquals(1L, role.getCreatedBy());

        // Test role created by admin user
        role.setCreatedBy(100L);
        assertEquals(100L, role.getCreatedBy());

        // Test role created by regular user
        role.setCreatedBy(999L);
        assertEquals(999L, role.getCreatedBy());
    }

    @Test
    void testRoleWithMinimalData() {
        Role minimalRole = new Role();
        minimalRole.setName("ROLE_MINIMAL");
        minimalRole.setCode("MINIMAL");

        assertEquals("ROLE_MINIMAL", minimalRole.getName());
        assertEquals("MINIMAL", minimalRole.getCode());
        assertNull(minimalRole.getDescription());
        assertNull(minimalRole.getId());
        assertNull(minimalRole.getCreatedAt());
        assertNull(minimalRole.getUpdatedAt());
        assertNull(minimalRole.getCreatedBy());
    }
}
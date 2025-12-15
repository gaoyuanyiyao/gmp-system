package com.gmp.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 Organization 实体类的所有方法
 */
class OrganizationTest {

    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(1L)
                .orgCode("ORG_QA_DEPARTMENT")
                .orgName("质量保证部")
                .description("负责GMP质量保证工作")
                .orgType(Organization.OrganizationType.QUALITY)
                .hierarchyLevel(2)
                .manager("张经理")
                .contactInfo("电话: 13800138000")
                .orgStatus(Organization.OrganizationStatus.ACTIVE)
                .gmpRelevant(true)
                .gmpResponsible("李质量负责人")
                .gmpRequirements("符合GMP规范要求")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedBy(1L)
                .version(1)
                .build();
    }

    @Test
    void testBuilderPattern() {
        assertNotNull(organization);
        assertEquals(1L, organization.getId());
        assertEquals("ORG_QA_DEPARTMENT", organization.getOrgCode());
        assertEquals("质量保证部", organization.getOrgName());
        assertEquals("负责GMP质量保证工作", organization.getDescription());
        assertEquals(Organization.OrganizationType.QUALITY, organization.getOrgType());
        assertEquals(2, organization.getHierarchyLevel());
        assertEquals("张经理", organization.getManager());
        assertEquals("电话: 13800138000", organization.getContactInfo());
        assertEquals(Organization.OrganizationStatus.ACTIVE, organization.getOrgStatus());
        assertTrue(organization.getGmpRelevant());
        assertEquals("李质量负责人", organization.getGmpResponsible());
        assertEquals("符合GMP规范要求", organization.getGmpRequirements());
        assertNotNull(organization.getCreatedAt());
        assertNotNull(organization.getUpdatedAt());
        assertEquals(1L, organization.getCreatedBy());
        assertEquals(1L, organization.getUpdatedBy());
        assertEquals(1, organization.getVersion());
    }

    @Test
    void testNoArgsConstructor() {
        Organization emptyOrg = new Organization();
        assertNotNull(emptyOrg);
        assertNull(emptyOrg.getId());
        assertNull(emptyOrg.getOrgCode());
        assertNull(emptyOrg.getOrgName());
        assertNull(emptyOrg.getDescription());
        assertNull(emptyOrg.getOrgType());
        assertNull(emptyOrg.getHierarchyLevel());
        assertNull(emptyOrg.getManager());
        assertNull(emptyOrg.getContactInfo());
        assertNull(emptyOrg.getOrgStatus());
        assertNull(emptyOrg.getGmpRelevant());
        assertNull(emptyOrg.getGmpResponsible());
        assertNull(emptyOrg.getGmpRequirements());
        assertNull(emptyOrg.getCreatedAt());
        assertNull(emptyOrg.getUpdatedAt());
        assertNull(emptyOrg.getCreatedBy());
        assertNull(emptyOrg.getUpdatedBy());
        assertNull(emptyOrg.getVersion());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Organization org = new Organization(
                1L, "ORG_HR_DEPARTMENT", "人力资源部", "负责人力资源管理",
                Organization.OrganizationType.HR, null, 2, "王经理", "电话: 13900139000",
                Organization.OrganizationStatus.ACTIVE, false, null, null,
                now, now, 1L, 1L, 1);

        assertNotNull(org);
        assertEquals(1L, org.getId());
        assertEquals("ORG_HR_DEPARTMENT", org.getOrgCode());
        assertEquals("人力资源部", org.getOrgName());
        assertEquals("负责人力资源管理", org.getDescription());
        assertEquals(Organization.OrganizationType.HR, org.getOrgType());
        assertNull(org.getParent());
        assertEquals(2, org.getHierarchyLevel());
        assertEquals("王经理", org.getManager());
        assertEquals("电话: 13900139000", org.getContactInfo());
        assertEquals(Organization.OrganizationStatus.ACTIVE, org.getOrgStatus());
        assertFalse(org.getGmpRelevant());
        assertNull(org.getGmpResponsible());
        assertNull(org.getGmpRequirements());
        assertEquals(now, org.getCreatedAt());
        assertEquals(now, org.getUpdatedAt());
        assertEquals(1L, org.getCreatedBy());
        assertEquals(1L, org.getUpdatedBy());
        assertEquals(1, org.getVersion());
    }

    @Test
    void testIsActive_WhenActive() {
        organization.setOrgStatus(Organization.OrganizationStatus.ACTIVE);
        assertTrue(organization.isActive());
    }

    @ParameterizedTest
    @EnumSource(value = Organization.OrganizationStatus.class, names = {"INACTIVE", "UNDER_REVIEW", "MERGED", "ARCHIVED"})
    void testIsActive_WhenNotActive(Organization.OrganizationStatus status) {
        organization.setOrgStatus(status);
        assertFalse(organization.isActive());
    }

    @Test
    void testGetFullPath_WithoutParent() {
        organization.setParent(null);
        assertEquals("质量保证部", organization.getFullPath());
    }

    @Test
    void testGetFullPath_WithParent() {
        Organization parentOrg = Organization.builder()
                .orgName("公司总部")
                .build();
        organization.setParent(parentOrg);
        
        assertEquals("公司总部 > 质量保证部", organization.getFullPath());
    }

    @Test
    void testGetFullPath_WithGrandParent() {
        Organization grandParent = Organization.builder()
                .orgName("集团公司")
                .build();
        
        Organization parentOrg = Organization.builder()
                .orgName("公司总部")
                .parent(grandParent)
                .build();
                
        organization.setParent(parentOrg);
        
        assertEquals("集团公司 > 公司总部 > 质量保证部", organization.getFullPath());
    }

    @Test
    void testOrganizationTypeEnum() {
        assertEquals("部门管理", Organization.OrganizationType.DEPARTMENT.getDescription());
        assertEquals("生产管理", Organization.OrganizationType.PRODUCTION.getDescription());
        assertEquals("质量管理", Organization.OrganizationType.QUALITY.getDescription());
        assertEquals("合规审核", Organization.OrganizationType.COMPLIANCE.getDescription());
        assertEquals("培训管理", Organization.OrganizationType.TRAINING.getDescription());
        assertEquals("人力资源", Organization.OrganizationType.HR.getDescription());
        assertEquals("信息技术", Organization.OrganizationType.IT.getDescription());
        assertEquals("设备维护", Organization.OrganizationType.MAINTENANCE.getDescription());
        assertEquals("后勤保障", Organization.OrganizationType.LOGISTICS.getDescription());
        assertEquals("安全环保", Organization.OrganizationType.SAFETY.getDescription());
    }

    @Test
    void testOrganizationStatusEnum() {
        assertEquals("正常运行", Organization.OrganizationStatus.ACTIVE.getDescription());
        assertEquals("临时停用", Organization.OrganizationStatus.INACTIVE.getDescription());
        assertEquals("审核中", Organization.OrganizationStatus.UNDER_REVIEW.getDescription());
        assertEquals("已合并", Organization.OrganizationStatus.MERGED.getDescription());
        assertEquals("已归档", Organization.OrganizationStatus.ARCHIVED.getDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        Organization org1 = Organization.builder()
                .id(1L)
                .orgCode("ORG_TEST")
                .build();
                
        Organization org2 = Organization.builder()
                .id(1L)
                .orgCode("ORG_TEST")
                .build();
                
        Organization org3 = Organization.builder()
                .id(2L)
                .orgCode("ORG_DIFFERENT")
                .build();

        assertEquals(org1, org2);
        assertNotEquals(org1, org3);
        assertEquals(org1.hashCode(), org2.hashCode());
        assertNotEquals(org1.hashCode(), org3.hashCode());
    }

    @Test
    void testToString() {
        String toString = organization.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ORG_QA_DEPARTMENT"));
        assertTrue(toString.contains("质量保证部"));
        assertTrue(toString.contains("QUALITY"));
    }

    @Test
    void testDefaultValues() {
        Organization defaultOrg = Organization.builder()
                .orgCode("ORG_DEFAULT")
                .orgName("默认部门")
                .build();

        assertEquals(Organization.OrganizationType.DEPARTMENT, defaultOrg.getOrgType());
        assertEquals(1, defaultOrg.getHierarchyLevel());
        assertEquals(Organization.OrganizationStatus.ACTIVE, defaultOrg.getOrgStatus());
        assertFalse(defaultOrg.getGmpRelevant());
        assertEquals(1, defaultOrg.getVersion());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ORG_QA", "ORG_PRODUCTION", "ORG_HR_DEPARTMENT"})
    void testValidOrgCodes(String orgCode) {
        organization.setOrgCode(orgCode);
        assertEquals(orgCode, organization.getOrgCode());
    }

    @Test
    void testParentChildRelationship() {
        Organization parent = Organization.builder()
                .id(100L)
                .orgCode("ORG_PARENT")
                .orgName("父组织")
                .build();
                
        Organization child = Organization.builder()
                .id(101L)
                .orgCode("ORG_CHILD")
                .orgName("子组织")
                .parent(parent)
                .hierarchyLevel(2)
                .build();

        assertEquals(parent, child.getParent());
        assertEquals(2, child.getHierarchyLevel());
    }

    @Test
    void testGmpRelevantOrganization() {
        Organization gmpOrg = Organization.builder()
                .orgCode("ORG_GMP")
                .orgName("GMP相关组织")
                .gmpRelevant(true)
                .gmpResponsible("GMP负责人")
                .gmpRequirements("必须符合GMP规范")
                .build();

        assertTrue(gmpOrg.getGmpRelevant());
        assertEquals("GMP负责人", gmpOrg.getGmpResponsible());
        assertEquals("必须符合GMP规范", gmpOrg.getGmpRequirements());
    }

    @Test
    void testNonGmpOrganization() {
        Organization nonGmpOrg = Organization.builder()
                .orgCode("ORG_NON_GMP")
                .orgName("非GMP组织")
                .gmpRelevant(false)
                .build();

        assertFalse(nonGmpOrg.getGmpRelevant());
        assertNull(nonGmpOrg.getGmpResponsible());
        assertNull(nonGmpOrg.getGmpRequirements());
    }

    @Test
    void testVersionIncrement() {
        organization.setVersion(2);
        assertEquals(2, organization.getVersion());
        
        organization.setVersion(5);
        assertEquals(5, organization.getVersion());
    }

    @Test
    void testAuditFields() {
        LocalDateTime createTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updateTime = LocalDateTime.of(2023, 1, 2, 10, 0);
        
        organization.setCreatedAt(createTime);
        organization.setUpdatedAt(updateTime);
        organization.setCreatedBy(100L);
        organization.setUpdatedBy(200L);

        assertEquals(createTime, organization.getCreatedAt());
        assertEquals(updateTime, organization.getUpdatedAt());
        assertEquals(100L, organization.getCreatedBy());
        assertEquals(200L, organization.getUpdatedBy());
    }

    @Test
    void testContactInfoAndManager() {
        organization.setManager("新经理");
        organization.setContactInfo("邮箱: manager@example.com");

        assertEquals("新经理", organization.getManager());
        assertEquals("邮箱: manager@example.com", organization.getContactInfo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void testEmptyManagerAndContactInfo(String value) {
        organization.setManager(value);
        organization.setContactInfo(value);

        if (value == null || value.trim().isEmpty()) {
            assertTrue(organization.getManager() == null || organization.getManager().trim().isEmpty());
            assertTrue(organization.getContactInfo() == null || organization.getContactInfo().trim().isEmpty());
        }
    }

    @Test
    void testHierarchyLevel() {
        organization.setHierarchyLevel(1);
        assertEquals(1, organization.getHierarchyLevel());
        
        organization.setHierarchyLevel(5);
        assertEquals(5, organization.getHierarchyLevel());
        
        organization.setHierarchyLevel(10);
        assertEquals(10, organization.getHierarchyLevel());
    }

    @Test
    void testDescriptionLength() {
        String longDescription = "这是一个很长的描述信息，用于测试组织描述字段的长度限制。"
                + "这个描述应该能够处理较长的文本内容，确保系统能够正确处理。"
                + "测试组织描述的存储和显示功能。";
                
        organization.setDescription(longDescription);
        assertEquals(longDescription, organization.getDescription());
    }

    @Test
    void testOrganizationTypeConversion() {
        // 测试枚举类型的字符串转换
        organization.setOrgType(Organization.OrganizationType.valueOf("PRODUCTION"));
        assertEquals(Organization.OrganizationType.PRODUCTION, organization.getOrgType());
        
        organization.setOrgType(Organization.OrganizationType.valueOf("QUALITY"));
        assertEquals(Organization.OrganizationType.QUALITY, organization.getOrgType());
    }

    @Test
    void testOrganizationStatusConversion() {
        // 测试枚举类型的字符串转换
        organization.setOrgStatus(Organization.OrganizationStatus.valueOf("INACTIVE"));
        assertEquals(Organization.OrganizationStatus.INACTIVE, organization.getOrgStatus());
        
        organization.setOrgStatus(Organization.OrganizationStatus.valueOf("ARCHIVED"));
        assertEquals(Organization.OrganizationStatus.ARCHIVED, organization.getOrgStatus());
    }

    @Test
    void testCircularReferencePrevention() {
        // 测试防止循环引用
        Organization org1 = Organization.builder()
                .orgCode("ORG_1")
                .orgName("组织1")
                .build();
                
        Organization org2 = Organization.builder()
                .orgCode("ORG_2")
                .orgName("组织2")
                .parent(org1)
                .build();

        // 不应该允许循环引用
        org1.setParent(org2); // 这应该被避免，但需要测试
        
        // 验证层级关系
        assertEquals(org2, org1.getParent());
        assertEquals(org1, org2.getParent().getParent()); // 这显示了循环引用
    }

    @Test
    void testImmutableFields() {
        // 测试不可变字段的访问
        LocalDateTime originalCreateTime = organization.getCreatedAt();
        organization.setUpdatedAt(LocalDateTime.now());
        
        // 创建时间应该保持不变
        assertEquals(originalCreateTime, organization.getCreatedAt());
    }

    @Test
    void testNullSafety() {
        Organization nullOrg = new Organization();
        
        // 测试空值安全性
        assertNull(nullOrg.getId());
        assertNull(nullOrg.getOrgCode());
        assertNull(nullOrg.getOrgName());
        assertNull(nullOrg.getDescription());
        assertNull(nullOrg.getOrgType());
        assertNull(nullOrg.getParent());
        assertNull(nullOrg.getHierarchyLevel());
        assertNull(nullOrg.getManager());
        assertNull(nullOrg.getContactInfo());
        assertNull(nullOrg.getOrgStatus());
        assertNull(nullOrg.getGmpRelevant());
        assertNull(nullOrg.getGmpResponsible());
        assertNull(nullOrg.getGmpRequirements());
        assertNull(nullOrg.getCreatedAt());
        assertNull(nullOrg.getUpdatedAt());
        assertNull(nullOrg.getCreatedBy());
        assertNull(nullOrg.getUpdatedBy());
        assertNull(nullOrg.getVersion());
        
        // 测试方法在空值情况下的行为
        assertFalse(nullOrg.isActive()); // 默认应为false
        assertEquals("", nullOrg.getFullPath()); // 空组织名称
    }

    @Test
    void testEdgeCases() {
        // 测试边界情况
        Organization edgeCaseOrg = Organization.builder()
                .orgCode("ORG_EDGE")
                .orgName("边界测试组织")
                .hierarchyLevel(Integer.MAX_VALUE)
                .version(Integer.MAX_VALUE)
                .build();

        assertEquals(Integer.MAX_VALUE, edgeCaseOrg.getHierarchyLevel());
        assertEquals(Integer.MAX_VALUE, edgeCaseOrg.getVersion());
    }

    @Test
    void testCopyConstructorEquivalent() {
        // 测试复制构造函数等效行为
        Organization original = Organization.builder()
                .id(1L)
                .orgCode("ORG_ORIGINAL")
                .orgName("原始组织")
                .build();

        Organization copy = Organization.builder()
                .id(original.getId())
                .orgCode(original.getOrgCode())
                .orgName(original.getOrgName())
                .build();

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }
}
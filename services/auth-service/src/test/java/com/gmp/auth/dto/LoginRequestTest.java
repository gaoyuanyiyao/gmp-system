package com.gmp.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequestDefaultConstructor() {
        LoginRequest loginRequest = new LoginRequest();

        assertNotNull(loginRequest);
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
        assertFalse(loginRequest.getRememberMe());
        assertNull(loginRequest.getCaptchaCode());
        assertNull(loginRequest.getIpAddress());
        assertNull(loginRequest.getUserAgent());
        assertNull(loginRequest.getOrganizationId());
        assertNull(loginRequest.getMfaCode());
    }

    @Test
    void testLoginRequestSettersAndGetters() {
        LoginRequest loginRequest = new LoginRequest();
        
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        loginRequest.setRememberMe(true);
        loginRequest.setCaptchaCode("ABCD1234");
        loginRequest.setIpAddress("192.168.1.1");
        loginRequest.setUserAgent("Mozilla/5.0");
        loginRequest.setOrganizationId(123L);
        loginRequest.setMfaCode("123456");

        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
        assertTrue(loginRequest.getRememberMe());
        assertEquals("ABCD1234", loginRequest.getCaptchaCode());
        assertEquals("192.168.1.1", loginRequest.getIpAddress());
        assertEquals("Mozilla/5.0", loginRequest.getUserAgent());
        assertEquals(123L, loginRequest.getOrganizationId());
        assertEquals("123456", loginRequest.getMfaCode());
    }

    @Test
    void testLoginRequestWithNullValues() {
        LoginRequest loginRequest = new LoginRequest();
        
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);
        loginRequest.setRememberMe(false);
        loginRequest.setCaptchaCode(null);
        loginRequest.setIpAddress(null);
        loginRequest.setUserAgent(null);
        loginRequest.setOrganizationId(null);
        loginRequest.setMfaCode(null);

        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
        assertFalse(loginRequest.getRememberMe());
        assertNull(loginRequest.getCaptchaCode());
        assertNull(loginRequest.getIpAddress());
        assertNull(loginRequest.getUserAgent());
        assertNull(loginRequest.getOrganizationId());
        assertNull(loginRequest.getMfaCode());
    }

    @Test
    void testLoginRequestWithEmptyStrings() {
        LoginRequest loginRequest = new LoginRequest();
        
        loginRequest.setUsername("");
        loginRequest.setPassword("");
        loginRequest.setCaptchaCode("");
        loginRequest.setIpAddress("");
        loginRequest.setUserAgent("");
        loginRequest.setMfaCode("");

        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
        assertEquals("", loginRequest.getCaptchaCode());
        assertEquals("", loginRequest.getIpAddress());
        assertEquals("", loginRequest.getUserAgent());
        assertEquals("", loginRequest.getMfaCode());
    }

    @Test
    void testLoginRequestRememberMeDefaultValue() {
        LoginRequest loginRequest1 = new LoginRequest();
        assertFalse(loginRequest1.getRememberMe());
        
        LoginRequest loginRequest2 = new LoginRequest();
        loginRequest2.setRememberMe(true);
        assertTrue(loginRequest2.getRememberMe());
        
        LoginRequest loginRequest3 = new LoginRequest();
        loginRequest3.setRememberMe(false);
        assertFalse(loginRequest3.getRememberMe());
    }
}
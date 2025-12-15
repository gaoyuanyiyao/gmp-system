package com.gmp.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequestBuilder() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        assertNotNull(loginRequest);
        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testLoginRequestAllArgsConstructor() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");

        assertNotNull(loginRequest);
        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testLoginRequestNoArgsConstructor() {
        LoginRequest loginRequest = new LoginRequest();

        assertNotNull(loginRequest);
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testLoginRequestSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("newuser");
        loginRequest.setPassword("newpassword");

        assertEquals("newuser", loginRequest.getUsername());
        assertEquals("newpassword", loginRequest.getPassword());
    }

    @Test
    void testLoginRequestToString() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        String toString = loginRequest.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("password123"));
    }

    @Test
    void testLoginRequestEqualsAndHashCode() {
        LoginRequest request1 = new LoginRequest("testuser", "password123");
        LoginRequest request2 = new LoginRequest("testuser", "password123");
        LoginRequest request3 = new LoginRequest("different", "password");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
        
        assertNotEquals(request1, null);
        assertNotEquals(request1, "Not a LoginRequest");
    }

    @Test
    void testLoginRequestWithNullValues() {
        LoginRequest loginRequest = new LoginRequest(null, null);

        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());

        loginRequest.setUsername("user");
        loginRequest.setPassword(null);

        assertEquals("user", loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testLoginRequestWithEmptyStrings() {
        LoginRequest loginRequest = new LoginRequest("", "");

        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testLoginRequestCanEqual() {
        LoginRequest request1 = new LoginRequest("user", "pass");
        LoginRequest request2 = new LoginRequest("user", "pass");

        assertTrue(request1.canEqual(request2));
        assertFalse(request1.canEqual("Not a LoginRequest"));
    }

    @Test
    void testLoginRequestValidation() {
        // Test with various username formats
        LoginRequest request1 = new LoginRequest("user.name", "pass");
        assertEquals("user.name", request1.getUsername());

        LoginRequest request2 = new LoginRequest("user_name", "pass");
        assertEquals("user_name", request2.getUsername());

        LoginRequest request3 = new LoginRequest("user123", "pass");
        assertEquals("user123", request3.getUsername());

        // Test password with special characters
        LoginRequest request4 = new LoginRequest("user", "pass@123#");
        assertEquals("pass@123#", request4.getPassword());
    }

    @Test
    void testLoginRequestBuilderWithNullValues() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(null)
                .password(null)
                .build();

        assertNotNull(loginRequest);
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testLoginRequestBuilderWithEmptyValues() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("")
                .password("")
                .build();

        assertNotNull(loginRequest);
        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testLoginRequestCopy() {
        LoginRequest original = new LoginRequest("user", "pass");
        LoginRequest copy = LoginRequest.builder()
                .username(original.getUsername())
                .password(original.getPassword())
                .build();

        assertEquals(original, copy);
        assertEquals(original.getUsername(), copy.getUsername());
        assertEquals(original.getPassword(), copy.getPassword());
    }
}
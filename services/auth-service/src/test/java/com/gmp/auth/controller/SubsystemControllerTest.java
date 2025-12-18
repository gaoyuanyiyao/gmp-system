package com.gmp.auth.controller;

import com.gmp.auth.dto.ApiResponse;
import com.gmp.auth.entity.Subsystem;
import com.gmp.auth.service.SubsystemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubsystemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubsystemService subsystemService;

    @InjectMocks
    private SubsystemController subsystemController;

    private ObjectMapper objectMapper;
    private Subsystem subsystem;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subsystemController).build();
        objectMapper = new ObjectMapper();
        
        subsystem = new Subsystem();
        subsystem.setId(1L);
        subsystem.setSubsystemCode("GDM");
        subsystem.setSubsystemName("GMP Document Management");
        subsystem.setDescription("GMP Document Management System");
        subsystem.setEnabled(true);
    }

    @Test
    void testGetAllSubsystems_Success() throws Exception {
        // Arrange
        List<Subsystem> subsystems = Arrays.asList(subsystem);
        when(subsystemService.getAllSubsystems()).thenReturn(subsystems);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("GMP Document Management"));

        verify(subsystemService, times(1)).getAllSubsystems();
    }

    @Test
    void testGetSubsystemById_Success() throws Exception {
        // Arrange
        when(subsystemService.getSubsystemById(1L)).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("GMP Document Management"));

        verify(subsystemService, times(1)).getSubsystemById(1L);
    }

    @Test
    void testGetSubsystemById_NotFound() throws Exception {
        // Arrange
        when(subsystemService.getSubsystemById(1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(subsystemService, times(1)).getSubsystemById(1L);
    }

    @Test
    void testCreateSubsystem_Success() throws Exception {
        // Arrange
        when(subsystemService.createSubsystem(any(Subsystem.class))).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(post("/api/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subsystem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(subsystemService, times(1)).createSubsystem(any(Subsystem.class));
    }

    @Test
    void testCreateSubsystem_InvalidData() throws Exception {
        // Arrange
        Subsystem invalidSubsystem = new Subsystem();
        invalidSubsystem.setName(""); // 无效的名称

        // Act & Assert
        mockMvc.perform(post("/api/subsystems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSubsystem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSubsystem_Success() throws Exception {
        // Arrange
        subsystem.setName("Updated GMP Document Management");
        when(subsystemService.updateSubsystem(eq(1L), any(Subsystem.class))).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(put("/api/subsystems/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subsystem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated GMP Document Management"));

        verify(subsystemService, times(1)).updateSubsystem(eq(1L), any(Subsystem.class));
    }

    @Test
    void testUpdateSubsystem_NotFound() throws Exception {
        // Arrange
        when(subsystemService.updateSubsystem(eq(1L), any(Subsystem.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/subsystems/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subsystem)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(subsystemService, times(1)).updateSubsystem(eq(1L), any(Subsystem.class));
    }

    @Test
    void testDeleteSubsystem_Success() throws Exception {
        // Arrange
        when(subsystemService.deleteSubsystem(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/subsystems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subsystem deleted successfully"));

        verify(subsystemService, times(1)).deleteSubsystem(1L);
    }

    @Test
    void testDeleteSubsystem_NotFound() throws Exception {
        // Arrange
        when(subsystemService.deleteSubsystem(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/subsystems/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(subsystemService, times(1)).deleteSubsystem(1L);
    }

    @Test
    void testGetSubsystemByCode_Success() throws Exception {
        // Arrange
        when(subsystemService.getSubsystemByCode("GDM")).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems/code/GDM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("GDM"));

        verify(subsystemService, times(1)).getSubsystemByCode("GDM");
    }

    @Test
    void testGetSubsystemByCode_NotFound() throws Exception {
        // Arrange
        when(subsystemService.getSubsystemByCode("INVALID")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems/code/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(subsystemService, times(1)).getSubsystemByCode("INVALID");
    }

    @Test
    void testGetEnabledSubsystems_Success() throws Exception {
        // Arrange
        List<Subsystem> enabledSubsystems = Arrays.asList(subsystem);
        when(subsystemService.getEnabledSubsystems()).thenReturn(enabledSubsystems);

        // Act & Assert
        mockMvc.perform(get("/api/subsystems/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].enabled").value(true));

        verify(subsystemService, times(1)).getEnabledSubsystems();
    }

    @Test
    void testToggleSubsystemStatus_Enable_Success() throws Exception {
        // Arrange
        when(subsystemService.toggleSubsystemStatus(eq(1L), eq(true))).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(put("/api/subsystems/1/status")
                .param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subsystem status updated successfully"));

        verify(subsystemService, times(1)).toggleSubsystemStatus(eq(1L), eq(true));
    }

    @Test
    void testToggleSubsystemStatus_Disable_Success() throws Exception {
        // Arrange
        subsystem.setEnabled(false);
        when(subsystemService.toggleSubsystemStatus(eq(1L), eq(false))).thenReturn(subsystem);

        // Act & Assert
        mockMvc.perform(put("/api/subsystems/1/status")
                .param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Subsystem status updated successfully"));

        verify(subsystemService, times(1)).toggleSubsystemStatus(eq(1L), eq(false));
    }

    @Test
    void testToggleSubsystemStatus_NotFound() throws Exception {
        // Arrange
        when(subsystemService.toggleSubsystemStatus(eq(1L), eq(true))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/subsystems/1/status")
                .param("enabled", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(subsystemService, times(1)).toggleSubsystemStatus(eq(1L), eq(true));
    }
}
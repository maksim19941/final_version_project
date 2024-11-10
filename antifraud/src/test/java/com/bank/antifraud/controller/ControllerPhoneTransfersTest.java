package com.bank.antifraud.controller;

import com.bank.antifraud.AntifraudApplication;
import com.bank.antifraud.dto.PhoneTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.service.interface_service.SuspiciousPhoneTransfersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControllerPhoneTransfers.class)
@ContextConfiguration(classes = AntifraudApplication.class)
public class ControllerPhoneTransfersTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SuspiciousPhoneTransfersService phoneService;
    private PhoneTransfersDTO phoneTransfer;
    private ObjectMapper objectMapper;
    private static final long ID = 999L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        phoneTransfer = PhoneTransfersDTO.builder()
                .id(1L)
                .phoneTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();
    }

    @Test
    void getPhoneTest() throws Exception {
        when(phoneService.getPhoneTransfer(anyLong())).thenReturn(phoneTransfer);

        mockMvc.perform(get("/phone_transfers/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(phoneService, times(1)).getPhoneTransfer(1L);
    }

    @Test
    void getAllPhoneTest() throws Exception {
        when(phoneService.getListPhoneTransfers()).thenReturn(Collections.singletonList(phoneTransfer));

        mockMvc.perform(get("/phone_transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
        verify(phoneService, times(1)).getListPhoneTransfers();
    }

    @Test
    void deletePhoneTest() throws Exception {
        doNothing().when(phoneService).delete(anyLong());

        mockMvc.perform(delete("/phone_transfers/{id}", 1))
                .andExpect(status().isOk());
        Mockito.verify(phoneService).delete(eq(1L));
        verify(phoneService, times(1)).delete(1L);
    }

    @Test
    void savePhoneTest() throws Exception {
        mockMvc.perform(post("/phone_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект сохранен"));
        Mockito.verify(phoneService, times(1)).savePhone(any(PhoneTransfersDTO.class));
    }

    @Test
    void updatePhoneTest() throws Exception {

        mockMvc.perform(put("/phone_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект обновлён"));
    }

    @Test
    void getPhoneTestException() throws Exception {

        when(phoneService.getPhoneTransfer(ID)).thenThrow(
                new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID));
        mockMvc.perform(get("/phone_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void deletePhoneExceptionIsNotFound() throws Exception {

        doThrow(new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID))
                .when(phoneService).delete(ID);

        mockMvc.perform(delete("/phone_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void savePhoneExceptionTestValidationException() throws Exception {

        phoneTransfer.setPhoneTransferId(null);
        phoneTransfer.setBlockedReason(null);
        phoneTransfer.setSuspiciousReason(null);


        mockMvc.perform(post("/phone_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneTransfer)))
                .andExpect(status().isBadRequest());
    }
}

package com.bank.antifraud.controller;

import com.bank.antifraud.AntifraudApplication;
import com.bank.antifraud.dto.AccountTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.service.interface_service.SuspiciousAccountTransfersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(ControllerAccountTransfers.class)
@ContextConfiguration(classes = AntifraudApplication.class)
public class ControllerAccountTransfersTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SuspiciousAccountTransfersService accountService;
    private AccountTransfersDTO accountTransfer;
    private ObjectMapper objectMapper;

    private static final long ID = 999L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        accountTransfer = AccountTransfersDTO.builder()
                .id(1L)
                .accountTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();
    }

    @Test
    void getAccountTest() throws Exception {

        when(accountService.getAccountTransfer(anyLong())).thenReturn(accountTransfer);

        mockMvc.perform(get("/account_transfers/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(accountService, times(1)).getAccountTransfer(1L);
    }

    @Test
    void getAllAccountTest() throws Exception {

        when(accountService.getListSAccountTransfers()).thenReturn(Collections.singletonList(accountTransfer));

        mockMvc.perform(get("/account_transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
        verify(accountService, times(1)).getListSAccountTransfers();
    }

    @Test
    void deleteAccountTest() throws Exception {

        doNothing().when(accountService).deleteAccount(anyLong());

        mockMvc.perform(delete("/account_transfers/{id}", 1))
                .andExpect(status().isOk());
        verify(accountService).deleteAccount(eq(1L));
        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    void saveAccountTest() throws Exception {

        mockMvc.perform(post("/account_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект сохранен"));
        verify(accountService, times(1)).saveAccount(any(AccountTransfersDTO.class));
    }

    @Test
    void updateAccountTest() throws Exception {

        mockMvc.perform(put("/account_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект обновлён"));
    }

    @Test
    void getAccountTestException() throws Exception {

        when(accountService.getAccountTransfer(ID)).thenThrow(
                new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID));
        mockMvc.perform(get("/account_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void deleteAccountExceptionIsNotFound() throws Exception {

        doThrow(new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID))
                .when(accountService).deleteAccount(ID);

        mockMvc.perform(delete("/account_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void saveAccountExceptionTestValidationException() throws Exception {

        accountTransfer.setAccountTransferId(null);
        accountTransfer.setBlockedReason(null);
        accountTransfer.setSuspiciousReason(null);

        mockMvc.perform(post("/account_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountTransfer)))
                .andExpect(status().isBadRequest());
    }
}

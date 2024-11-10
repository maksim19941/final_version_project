package com.bank.antifraud.controller;

import com.bank.antifraud.AntifraudApplication;
import com.bank.antifraud.dto.CardTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.service.interface_service.SuspiciousCardTransfersService;
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

@WebMvcTest(ControllerCardTransfers.class)
@ContextConfiguration(classes = AntifraudApplication.class)
public class ControllerCardTransfersTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SuspiciousCardTransfersService cardService;
    private CardTransfersDTO cardTransfer;
    private ObjectMapper objectMapper;

    private static final long ID = 999L;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        cardTransfer = CardTransfersDTO.builder()
                .id(1L)
                .cardTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();
    }

    @Test
    void getCardTest() throws Exception {
        Mockito.when(cardService.getCardTransfer(anyLong())).thenReturn(cardTransfer);

        mockMvc.perform(get("/card_transfers/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(cardService, times(1)).getCardTransfer(1L);
    }

    @Test
    void getAllCardTest() throws Exception {
        Mockito.when(cardService.getListSCardTransfers()).thenReturn(Collections.singletonList(cardTransfer));

        mockMvc.perform(get("/card_transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
        verify(cardService, times(1)).getListSCardTransfers();
    }

    @Test
    void deleteCardTest() throws Exception {
        Mockito.doNothing().when(cardService).delete(anyLong());

        mockMvc.perform(delete("/card_transfers/{id}", 1))
                .andExpect(status().isOk());
        Mockito.verify(cardService).delete(eq(1L));
        verify(cardService, times(1)).delete(1L);
    }

    @Test
    void saveCardTest() throws Exception {
        mockMvc.perform(post("/card_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект сохранен"));
        Mockito.verify(cardService, times(1)).saveCard(any(CardTransfersDTO.class));
    }

    @Test
    void updateCardTest() throws Exception {

        mockMvc.perform(put("/card_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardTransfer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объект обновлён"));
    }

    @Test
    void getCardTestException() throws Exception {

        when(cardService.getCardTransfer(ID)).thenThrow(
                new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID));
        mockMvc.perform(get("/card_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void deleteCardExceptionIsNotFound() throws Exception {

        doThrow(new EntityNotFoundException("An object with this ID was not found for deletion, ID: " + ID))
                .when(cardService).delete(ID);

        mockMvc.perform(delete("/card_transfers/{id}", ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("An object with this ID was not found for deletion, ID: " + ID));
    }

    @Test
    void saveCardExceptionTestValidationException() throws Exception {

        cardTransfer.setCardTransferId(null);
        cardTransfer.setBlockedReason(null);
        cardTransfer.setSuspiciousReason(null);


        mockMvc.perform(post("/card_transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardTransfer)))
                .andExpect(status().isBadRequest());
    }
}

package com.bank.antifraud.service;

import com.bank.antifraud.dto.CardTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.CardTransferMapper;
import com.bank.antifraud.model.SuspiciousCardTransfers;
import com.bank.antifraud.repository.SuspiciousCardTransfersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SuspiciousCardTransfersServiceImpTest {

    @Mock
    private SuspiciousCardTransfersRepository sctRepository;
    @InjectMocks
    private SuspiciousCardTransfersServiceImp service;
    private final CardTransferMapper cardTransferMapper = CardTransferMapper.INSTANCE;
    private SuspiciousCardTransfers cardTransferEntity;
    private CardTransfersDTO cardTransfersDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cardTransferEntity = new SuspiciousCardTransfers(
                1L,
                12345L,
                false,
                true,
                "activity",
                "Suspicious activity detected");

        cardTransfersDTO = cardTransferMapper.toDTO(cardTransferEntity);
    }

    @Test
    void getListSCardTransfers() {

        when(sctRepository.findAll()).thenReturn(List.of(cardTransferEntity));

        var result = service.getListSCardTransfers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardTransfersDTO, result.get(0));
        verify(sctRepository, times(1)).findAll();
    }

    @Test
    void save_CardTest() {

        when(sctRepository.save(any())).thenReturn(cardTransferEntity);
        HttpStatus result = service.saveCard(cardTransfersDTO);

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(any());
    }

    @Test
    void update_CardTest() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(cardTransferEntity));
        CardTransfersDTO cardTransfersDTO = new CardTransfersDTO(
                1L,
                231L,
                true,
                true,
                "not",
                "not");

        HttpStatus result = service.updateCard(cardTransfersDTO);   //Передача сущности в update и возврат Http статуса 200

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(cardTransferEntity);

        Optional<SuspiciousCardTransfers> updatedTransferOpt = sctRepository.findById(1L);    // Извлечение обновленной сущности из БД
        assertTrue(updatedTransferOpt.isPresent());

        SuspiciousCardTransfers updatedTransfer = updatedTransferOpt.get();
        SuspiciousCardTransfers expectedTransfer = new SuspiciousCardTransfers(    // Создание новой сущности для сравнения
                cardTransfersDTO.getId(),
                cardTransfersDTO.getCardTransferId(),
                cardTransfersDTO.isBlocked(),
                cardTransfersDTO.isSuspicious(),
                cardTransfersDTO.getBlockedReason(),
                cardTransfersDTO.getSuspiciousReason()
        );
        assertEquals(expectedTransfer, updatedTransfer); // Сравнение обновленной сущности с ожидаемыми значениями
    }

    @Test
    void update_Card_EntityNotFoundException_Test() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.updateCard(cardTransfersDTO);
        });
        assertEquals("CardTransfers not found ID: 1", exception.getMessage());
    }

    @Test
    void delete_Test() {

        when(sctRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);
        verify(sctRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_EntityNotFoundException_Test() {

        when(sctRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.delete(1L);
        });
        assertEquals("An object with this ID was not found, ID: 1", exception.getMessage());
    }

    @Test
    void get_Card_Transfer() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(cardTransferEntity));

        CardTransfersDTO result = service.getCardTransfer(1L);

        assertNotNull(result);
        assertEquals(cardTransfersDTO, result);
    }

    @Test
    void getCardTransfer_ShouldThrowEntityNotFoundException_WhenCardNotFound() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getCardTransfer(1L);
        });
        assertEquals("An object with this ID was not found, ID: 1", exception.getMessage());
    }
}

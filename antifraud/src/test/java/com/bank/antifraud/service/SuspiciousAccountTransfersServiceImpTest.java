package com.bank.antifraud.service;

import com.bank.antifraud.dto.AccountTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.AccountTransferMapper;
import com.bank.antifraud.model.SuspiciousAccountTransfers;
import com.bank.antifraud.repository.SuspiciousAccountTransfersRepository;
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

class SuspiciousAccountTransfersServiceImpTest {

    @Mock
    private SuspiciousAccountTransfersRepository sctRepository;
    @InjectMocks
    private SuspiciousAccountTransfersServiceImp service;
    private final AccountTransferMapper accountTransferMapper = AccountTransferMapper.INSTANCE;
    private SuspiciousAccountTransfers accountTransferEntity;
    private AccountTransfersDTO accountTransfersDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountTransferEntity = new SuspiciousAccountTransfers(
                1L,
                12345L,
                false,
                true,
                "activity",
                "Suspicious activity detected");

        accountTransfersDTO = accountTransferMapper.toDTO(accountTransferEntity);
    }

    @Test
    void getListSAccountTransfers() {

        when(sctRepository.findAll()).thenReturn(List.of(accountTransferEntity));

        var result = service.getListSAccountTransfers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accountTransfersDTO, result.get(0));
        verify(sctRepository, times(1)).findAll();
    }

    @Test
    void save_AccountTest() {

        when(sctRepository.save(any())).thenReturn(accountTransferEntity);
        HttpStatus result = service.saveAccount(accountTransfersDTO);

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(any());
    }

    @Test
    void update_AccountTest() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(accountTransferEntity));
        AccountTransfersDTO accountTransfersDTO = new AccountTransfersDTO(
                1L,
                231L,
                true,
                true,
                "not",
                "not");

        HttpStatus result = service.updateAccount(accountTransfersDTO);   //Передача сущности в update и возврат Http статуса 200

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(accountTransferEntity);

        Optional<SuspiciousAccountTransfers> updatedTransferOpt = sctRepository.findById(1L);    // Извлечение обновленной сущности из БД
        assertTrue(updatedTransferOpt.isPresent());

        SuspiciousAccountTransfers updatedTransfer = updatedTransferOpt.get();
        SuspiciousAccountTransfers expectedTransfer = new SuspiciousAccountTransfers(    // Создание новой сущности для сравнения
                accountTransfersDTO.getId(),
                accountTransfersDTO.getAccountTransferId(),
                accountTransfersDTO.isBlocked(),
                accountTransfersDTO.isSuspicious(),
                accountTransfersDTO.getBlockedReason(),
                accountTransfersDTO.getSuspiciousReason()
        );
        assertEquals(expectedTransfer, updatedTransfer); // Сравнение обновленной сущности с ожидаемыми значениями
    }

    @Test
    void update_Account_EntityNotFoundException_Test() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.updateAccount(accountTransfersDTO);
        });
        assertEquals("AccountTransfers not found ID: 1", exception.getMessage());
    }

    @Test
    void delete_Test() {

        when(sctRepository.existsById(1L)).thenReturn(true);

        service.deleteAccount(1L);
        verify(sctRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_EntityNotFoundException_Test() {

        when(sctRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.deleteAccount(1L);
        });
        assertEquals("An object with this ID was not found, ID: 1", exception.getMessage());
    }

    @Test
    void get_Account_Transfer() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(accountTransferEntity));

        AccountTransfersDTO result = service.getAccountTransfer(1L);

        assertNotNull(result);
        assertEquals(accountTransfersDTO, result);
    }

    @Test
    void getAccountTransfer_ShouldThrowEntityNotFoundException_WhenAccountNotFound() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getAccountTransfer(1L);
        });
        assertEquals("An object with this ID was not found, ID: 1", exception.getMessage());
    }
}

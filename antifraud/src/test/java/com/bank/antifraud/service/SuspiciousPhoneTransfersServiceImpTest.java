package com.bank.antifraud.service;

import com.bank.antifraud.dto.PhoneTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.PhoneTransferMapper;
import com.bank.antifraud.model.SuspiciousPhoneTransfers;
import com.bank.antifraud.repository.SuspiciousPhoneTransfersRepository;
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

class SuspiciousPhoneTransfersServiceImpTest {

    @Mock
    private SuspiciousPhoneTransfersRepository sctRepository;
    @InjectMocks
    private SuspiciousPhoneTransfersServiceImp service;
    private final PhoneTransferMapper phoneTransferMapper = PhoneTransferMapper.INSTANCE;
    private SuspiciousPhoneTransfers phoneTransferEntity;
    private PhoneTransfersDTO phoneTransfersDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        phoneTransferEntity = new SuspiciousPhoneTransfers(
                1L,
                12345L,
                false,
                true,
                "activity",
                "Suspicious activity detected");

        phoneTransfersDTO = phoneTransferMapper.toDTO(phoneTransferEntity);
    }

    @Test
    void getListSPhoneTransfers() {

        when(sctRepository.findAll()).thenReturn(List.of(phoneTransferEntity));

        var result = service.getListPhoneTransfers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(phoneTransfersDTO, result.get(0));
        verify(sctRepository, times(1)).findAll();
    }

    @Test
    void save_PhoneTest() {

        when(sctRepository.save(any())).thenReturn(phoneTransferEntity);
        HttpStatus result = service.savePhone(phoneTransfersDTO);

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(any());
    }

    @Test
    void update_PhoneTest() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(phoneTransferEntity));
        PhoneTransfersDTO phoneTransfersDTO = new PhoneTransfersDTO(
                1L,
                231L,
                true,
                true,
                "not",
                "not");

        HttpStatus result = service.updatePhone(phoneTransfersDTO);   //Передача сущности в update и возврат Http статуса 200

        assertEquals(HttpStatus.OK, result);
        verify(sctRepository, times(1)).save(phoneTransferEntity);

        Optional<SuspiciousPhoneTransfers> updatedTransferOpt = sctRepository.findById(1L);    // Извлечение обновленной сущности из БД
        assertTrue(updatedTransferOpt.isPresent());

        SuspiciousPhoneTransfers updatedTransfer = updatedTransferOpt.get();
        SuspiciousPhoneTransfers expectedTransfer = new SuspiciousPhoneTransfers(    // Создание новой сущности для сравнения
                phoneTransfersDTO.getId(),
                phoneTransfersDTO.getPhoneTransferId(),
                phoneTransfersDTO.isBlocked(),
                phoneTransfersDTO.isSuspicious(),
                phoneTransfersDTO.getBlockedReason(),
                phoneTransfersDTO.getSuspiciousReason()
        );
        assertEquals(expectedTransfer, updatedTransfer); // Сравнение обновленной сущности с ожидаемыми значениями
    }

    @Test
    void update_Phone_EntityNotFoundException_Test() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.updatePhone(phoneTransfersDTO);
        });
        assertEquals("PhoneTransfers not found ID: 1", exception.getMessage());
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
    void get_Phone_Transfer() {

        when(sctRepository.findById(1L)).thenReturn(Optional.of(phoneTransferEntity));

        PhoneTransfersDTO result = service.getPhoneTransfer(1L);

        assertNotNull(result);
        assertEquals(phoneTransfersDTO, result);
    }

    @Test
    void getPhoneTransfer_ShouldThrowEntityNotFoundException_WhenPhoneNotFound() {

        when(sctRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.getPhoneTransfer(1L);
        });
        assertEquals("An object with this ID was not found, ID: 1", exception.getMessage());
    }
}

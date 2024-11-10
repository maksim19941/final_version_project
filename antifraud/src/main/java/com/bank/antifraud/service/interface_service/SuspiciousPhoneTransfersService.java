package com.bank.antifraud.service.interface_service;

import com.bank.antifraud.dto.PhoneTransfersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SuspiciousPhoneTransfersService {

    List<PhoneTransfersDTO> getListPhoneTransfers();

    @Transactional
    HttpStatus savePhone(PhoneTransfersDTO newPhoneTransfers);

    @Transactional
    HttpStatus updatePhone(PhoneTransfersDTO updateTrDTO);

    void delete(Long id);

    PhoneTransfersDTO getPhoneTransfer(Long id);
}

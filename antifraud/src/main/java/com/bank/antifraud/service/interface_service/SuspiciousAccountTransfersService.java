package com.bank.antifraud.service.interface_service;

import com.bank.antifraud.dto.AccountTransfersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SuspiciousAccountTransfersService {
    @Transactional
    List<AccountTransfersDTO> getListSAccountTransfers();

    @Transactional
    HttpStatus saveAccount(AccountTransfersDTO newAccountTransfers);

    @Transactional
    HttpStatus updateAccount(AccountTransfersDTO updateTrDTO);

    void deleteAccount(Long id);

    AccountTransfersDTO getAccountTransfer(Long id);
}

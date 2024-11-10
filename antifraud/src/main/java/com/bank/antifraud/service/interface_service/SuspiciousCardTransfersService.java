package com.bank.antifraud.service.interface_service;

import com.bank.antifraud.dto.CardTransfersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SuspiciousCardTransfersService {

    @Transactional
    List<CardTransfersDTO> getListSCardTransfers();

    @Transactional
    HttpStatus saveCard(CardTransfersDTO newCardTransfers);

    @Transactional
    HttpStatus updateCard(CardTransfersDTO updateTrDTO);

    void delete(Long id);

    CardTransfersDTO getCardTransfer(Long id);
}

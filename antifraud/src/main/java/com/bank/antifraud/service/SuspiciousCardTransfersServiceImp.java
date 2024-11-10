package com.bank.antifraud.service;

import com.bank.antifraud.dto.CardTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.CardTransferMapper;
import com.bank.antifraud.model.SuspiciousCardTransfers;
import com.bank.antifraud.repository.SuspiciousCardTransfersRepository;
import com.bank.antifraud.service.interface_service.SuspiciousCardTransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SuspiciousCardTransfersServiceImp implements SuspiciousCardTransfersService {

    private final SuspiciousCardTransfersRepository sctRepository;
    private final CardTransferMapper cardTransferMapper = CardTransferMapper.INSTANCE;

    @Autowired
    public SuspiciousCardTransfersServiceImp(SuspiciousCardTransfersRepository sctRepository) {
        this.sctRepository = sctRepository;
    }

    @Override
    public List<CardTransfersDTO> getListSCardTransfers() {

        log.info("Getting all card transfers");
        List<SuspiciousCardTransfers> cardTransfersListEntity = sctRepository.findAll();
        return cardTransferMapper.toDTOList(cardTransfersListEntity);
    }

    @Override
    @Transactional
    public HttpStatus saveCard(CardTransfersDTO newCardTransfers) {

        log.info("Getting card transfer by id: {}", newCardTransfers);
        sctRepository.save(cardTransferMapper.toEntity(newCardTransfers));
        return HttpStatus.OK;
    }

    @Override
    @Transactional
    public HttpStatus updateCard(CardTransfersDTO updateTrDTO) {

        log.info("Updating card transfer: {}", updateTrDTO);
        SuspiciousCardTransfers cardTransfers = sctRepository.findById(updateTrDTO.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("CardTransfers not found ID: " + updateTrDTO.getId()));

        cardTransferMapper.updateEntityFromDTO(updateTrDTO, cardTransfers);
        sctRepository.save(cardTransfers);
        return HttpStatus.OK;
    }

    @Override
    public void delete(Long id) {

        log.info("Deleting card transfer: {}", id);
        if (!sctRepository.existsById(id)) {
            throw new EntityNotFoundException("An object with this ID was not found, ID: " + id);
        }
        sctRepository.deleteById(id);
    }

    @Override
    public CardTransfersDTO getCardTransfer(Long id) {

        log.info("Getting card transfer by id: {}", id);
        SuspiciousCardTransfers cardTransferEntity = sctRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("An object with this ID was not found, ID: " + id));
        return cardTransferMapper.toDTO(cardTransferEntity);
    }
}


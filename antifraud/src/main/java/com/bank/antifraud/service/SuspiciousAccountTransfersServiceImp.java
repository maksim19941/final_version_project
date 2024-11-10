package com.bank.antifraud.service;

import com.bank.antifraud.dto.AccountTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.AccountTransferMapper;
import com.bank.antifraud.model.SuspiciousAccountTransfers;
import com.bank.antifraud.repository.SuspiciousAccountTransfersRepository;
import com.bank.antifraud.service.interface_service.SuspiciousAccountTransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SuspiciousAccountTransfersServiceImp implements SuspiciousAccountTransfersService {

    private final SuspiciousAccountTransfersRepository satRepository;
    private final AccountTransferMapper accountTransferMapper = AccountTransferMapper.INSTANCE;

    @Autowired
    public SuspiciousAccountTransfersServiceImp(SuspiciousAccountTransfersRepository satRepository) {
        this.satRepository = satRepository;
    }

    @Override
    public AccountTransfersDTO getAccountTransfer(Long id) {

        log.info("Getting account transfer by id: {}", id);
        SuspiciousAccountTransfers accountTransferEntity = satRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("An object with this ID was not found, ID: " + id));

        return accountTransferMapper.toDTO(accountTransferEntity);
    }

    @Override
    public List<AccountTransfersDTO> getListSAccountTransfers() {

        log.info("Getting all account transfers");
        List<SuspiciousAccountTransfers> accountTransfersListEntity = satRepository.findAll();
        return accountTransferMapper.toDTOList(accountTransfersListEntity);
    }

    @Override
    @Transactional
    public HttpStatus saveAccount(AccountTransfersDTO newAccountTransfers) {

        log.info("Saving account transfer: {}", newAccountTransfers);
        satRepository.save(accountTransferMapper.toEntity(newAccountTransfers));
        return HttpStatus.OK;
    }

    @Override
    @Transactional
    public HttpStatus updateAccount(AccountTransfersDTO updateTrDTO) {

        log.info("Updating account transfer: {}", updateTrDTO);
        SuspiciousAccountTransfers cardTransfers = satRepository.findById(updateTrDTO.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("AccountTransfers not found ID: " + updateTrDTO.getId()));

        accountTransferMapper.updateEntityFromDTO(updateTrDTO, cardTransfers);
        satRepository.save(cardTransfers);
        return HttpStatus.OK;
    }

    @Override
    public void deleteAccount(Long id) {

        log.info("Deleting account transfer: {}", id);
        if (!satRepository.existsById(id)) {
            throw new EntityNotFoundException("An object with this ID was not found, ID: " + id);
        }
        satRepository.deleteById(id);
    }
}

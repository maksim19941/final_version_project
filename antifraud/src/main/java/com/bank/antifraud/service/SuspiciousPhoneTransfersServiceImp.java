package com.bank.antifraud.service;

import com.bank.antifraud.dto.PhoneTransfersDTO;
import com.bank.antifraud.exception.EntityNotFoundException;
import com.bank.antifraud.mapper.PhoneTransferMapper;
import com.bank.antifraud.model.SuspiciousPhoneTransfers;
import com.bank.antifraud.repository.SuspiciousPhoneTransfersRepository;
import com.bank.antifraud.service.interface_service.SuspiciousPhoneTransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SuspiciousPhoneTransfersServiceImp implements SuspiciousPhoneTransfersService {

    private final SuspiciousPhoneTransfersRepository sptRepository;
    private final PhoneTransferMapper phoneTransferMapper = PhoneTransferMapper.INSTANCE;

    @Autowired
    public SuspiciousPhoneTransfersServiceImp(SuspiciousPhoneTransfersRepository sptRepository) {
        this.sptRepository = sptRepository;
    }

    @Override
    public List<PhoneTransfersDTO> getListPhoneTransfers() {

        log.info("Getting all phone transfers");
        List<SuspiciousPhoneTransfers> accountTransfersListEntity = sptRepository.findAll();
        return phoneTransferMapper.toDTOList(accountTransfersListEntity);
    }

    @Override
    @Transactional
    public HttpStatus savePhone(PhoneTransfersDTO newPhoneTransfers) {

        log.info("Getting account transfer by id: {}", newPhoneTransfers);
        sptRepository.save(phoneTransferMapper.toEntity(newPhoneTransfers));
        return HttpStatus.OK;
    }

    @Override
    @Transactional
    public HttpStatus updatePhone(PhoneTransfersDTO updateTrDTO) {

        log.info("Creating phone transfer: {}", updateTrDTO);

        SuspiciousPhoneTransfers phoneTransfers = sptRepository.findById(updateTrDTO.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("PhoneTransfers not found ID: " + updateTrDTO.getId()));

        phoneTransferMapper.updateEntityFromDTO(updateTrDTO, phoneTransfers);
        sptRepository.save(phoneTransfers);
        return HttpStatus.OK;
    }

    @Override
    public void delete(Long id) {

        log.info("Deleting phone transfer: {}", id);
        if (!sptRepository.existsById(id)) {
            throw new EntityNotFoundException("An object with this ID was not found, ID: " + id);
        }
        sptRepository.deleteById(id);
    }

    @Override
    public PhoneTransfersDTO getPhoneTransfer(Long id) {

        log.info("Getting phone transfer by id: {}", id);
        SuspiciousPhoneTransfers accountTransferEntity = sptRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("An object with this ID was not found, ID: " + id));
        return phoneTransferMapper.toDTO(accountTransferEntity);
    }
}

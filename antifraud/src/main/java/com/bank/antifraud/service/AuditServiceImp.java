package com.bank.antifraud.service;

import com.bank.antifraud.exception.AuditRecordNotFoundException;
import com.bank.antifraud.mapper.AuditMapper;
import com.bank.antifraud.model.AuditModel;
import com.bank.antifraud.repository.AuditRepository;
import com.bank.antifraud.service.interface_service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AuditServiceImp implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper = AuditMapper.INSTANCE;

    @Autowired
    public AuditServiceImp(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public AuditModel getLatestAuditByJson(String entityType, Long id) {

        AuditModel auditEntity = auditRepository.getLatestAuditByJson(id, entityType);

        return auditEntity;
    }


    @Override
    public void createAudit(AuditModel audit) {

        if (audit == null) {
            log.error("Cannot create audit record: Audit object is null");
            throw new IllegalArgumentException("Audit must not be null");
        }
        log.info("Creating audit record: {}", audit);
        auditRepository.save(audit);
        log.info("Audit record created successfully with ID: {}", audit.getId());
    }


    @Override
    public AuditModel findById(Long id) {

        log.info("Finding audit record with ID: {}", id);
        return auditRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Audit not found with ID: {}", id);
                    return new RuntimeException("Couldn't find a record for this id " + id);
                });
    }

    @Override
    public List<AuditModel> findAll() {

        log.info("Retrieving all audit records");
        List<AuditModel> audits = auditRepository.findAll();
        log.info("Total audits retrieved: {}", audits.size());

        return audits;
    }

    @Override
    public void deleteById(Long id) {

        log.info("Attempting to delete audit record with ID: {}", id);

        if (!auditRepository.existsById(id)) {
            log.error("Cannot delete. Audit not found with ID: {}", id);
            throw new RuntimeException("Cannot delete. Audit not found with ID: " + id);
        }
        auditRepository.deleteById(id);
        log.info("Successfully deleted audit record with ID: {}", id);
    }
}

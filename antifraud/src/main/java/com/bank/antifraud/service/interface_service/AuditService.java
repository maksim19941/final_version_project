package com.bank.antifraud.service.interface_service;

import com.bank.antifraud.model.AuditModel;

import java.util.List;
import java.util.Optional;

public interface AuditService {

    AuditModel getLatestAuditByJson(String entityType, Long id);

    void createAudit(AuditModel newAudit);

    AuditModel findById(Long id);

    List<AuditModel> findAll();

    void deleteById(Long id);
}

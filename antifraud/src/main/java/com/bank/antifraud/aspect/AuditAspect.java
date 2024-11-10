package com.bank.antifraud.aspect;

import com.bank.antifraud.exception.AuditRecordNotFoundException;
import com.bank.antifraud.model.AuditModel;
import com.bank.antifraud.service.interface_service.AuditService;
import com.bank.antifraud.util.Identifiable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @After("execution(* com.bank.antifraud.service.*.save*(..)) && args(update)")
    public void afterResultCreateAdvice(JoinPoint joinPoint, Object update) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        try {
            String json = objectMapper.writeValueAsString(update);

            createAndSave(signature,
                    "Хайзенберг",
                    null,
                    LocalDateTime.now(),
                    null,
                    null,
                    json);

            log.info("Created audit record for entity: {}", update.getClass().getSimpleName());

        } catch (JsonProcessingException e) {
            log.error("JSON processing error for method {}: {}", signature.getName(), e.getMessage());
            throw new RuntimeException("JSON processing error", e);
        }
    }


    @After("execution(* com.bank.antifraud.service.*.update*(..)) && args(update)")
    public void afterResultUpdateAdvice(JoinPoint joinPoint, Object update) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String parameterType = signature.getParameterTypes()[0].getSimpleName();

        try {
            Long id = getID(update);
            String json = objectMapper.writeValueAsString(update);
            AuditModel oldModel = auditService.getLatestAuditByJson(parameterType, id);

            if (oldModel == null) { //Если ранее записи о сущности в аудите не было, то создается сущность с текущими данными
                createAndSave(signature,
                        "Хайзенберг",
                        null,
                        LocalDateTime.now(),
                        null,
                        null,
                        json);
            } else {
                createAndSave(
                        signature,
                        oldModel.getCreatedBy(),
                        "Тони Монтана",
                        oldModel.getCreatedAt(),
                        LocalDateTime.now(),
                        objectMapper.writeValueAsString(update),
                        oldModel.getEntityJson());
            }

            log.info("update audit record for entity: {}", update.getClass().getSimpleName());
        } catch (JsonProcessingException e) {
            log.error("JSON processing error for method {}: {}", signature.getName(), e.getMessage());
            throw new RuntimeException("JSON processing error", e);
        }

    }

    public Long getID(Object o) {

        Long id = 0L;

        if (o instanceof Identifiable) {
            id = (Long) ((Identifiable<?>) o).getId();
            return id;
        } else {
            log.info("An audit object with this ID was not found, ID: " + id);
            throw new AuditRecordNotFoundException("");
        }
    }

    private void createAndSave(MethodSignature signature,
                               String createdBy,
                               String modifiedBy,
                               LocalDateTime createdAt,
                               LocalDateTime modifiedAt,
                               String newEntityJson,
                               String entityJson) {
        AuditModel audit = new AuditModel();
        audit.setEntityType(signature.getParameterTypes()[0].getSimpleName());
        audit.setOperationType(signature.getMethod().getName());
        audit.setCreatedBy(createdBy);
        audit.setModifiedBy(modifiedBy);
        audit.setCreatedAt(createdAt);
        audit.setModifiedAt(modifiedAt);
        audit.setNewEntityJson(newEntityJson);
        audit.setEntityJson(entityJson);

        auditService.createAudit(audit);
    }
}
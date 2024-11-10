package com.bank.antifraud.repository;

import com.bank.antifraud.model.AuditModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditModel, Long> {

    @Query(value = "SELECT * FROM anti_fraud.audit " +
            "WHERE CAST(jsonb_extract_path_text(CAST(entity_json AS jsonb), 'id') AS INTEGER) = :id " +
            "AND entity_type = :entityType " +
            "ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    AuditModel getLatestAuditByJson(@Param("id") Long id, @Param("entityType") String entityType);
}

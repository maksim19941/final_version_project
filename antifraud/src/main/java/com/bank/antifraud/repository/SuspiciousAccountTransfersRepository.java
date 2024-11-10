package com.bank.antifraud.repository;

import com.bank.antifraud.model.SuspiciousAccountTransfers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousAccountTransfersRepository extends JpaRepository<SuspiciousAccountTransfers, Long> {

}

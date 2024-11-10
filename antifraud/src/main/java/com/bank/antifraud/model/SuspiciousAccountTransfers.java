package com.bank.antifraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "suspicious_account_transfer", schema = "anti_fraud")
public class SuspiciousAccountTransfers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_transfer_id")
    private Long accountTransferId;
    @Column(name = "is_blocked")
    private boolean blocked;
    @Column(name = "is_suspicious")
    private boolean suspicious;
    @Column(name = "blocked_reason")
    private String blockedReason;
    @Column(name = "suspicious_reason")
    private String suspiciousReason;
}

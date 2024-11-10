package com.bank.antifraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "suspicious_card_transfer", schema = "anti_fraud")
public class SuspiciousCardTransfers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "card_transfer_id")
    private Long cardTransferId;
    @Column(name = "is_blocked")
    private boolean blocked;
    @Column(name = "is_suspicious")
    private boolean suspicious;
    @Column(name = "blocked_reason")
    private String blockedReason;
    @Column(name = "suspicious_reason")
    private String suspiciousReason;
}

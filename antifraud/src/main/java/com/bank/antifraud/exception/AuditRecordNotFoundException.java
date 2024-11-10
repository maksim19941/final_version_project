package com.bank.antifraud.exception;

import lombok.Getter;

@Getter
public class AuditRecordNotFoundException extends RuntimeException {

    public AuditRecordNotFoundException(String message) {
        super(message);
    }
}

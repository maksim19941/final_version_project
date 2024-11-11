package com.bank.antifraud.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}

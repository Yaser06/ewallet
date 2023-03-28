package com.yaser.ewallet.exception;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class TransactionCreationException extends Exception {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}

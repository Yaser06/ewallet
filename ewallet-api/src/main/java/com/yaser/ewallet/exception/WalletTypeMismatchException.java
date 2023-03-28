package com.yaser.ewallet.exception;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class WalletTypeMismatchException extends Exception {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}

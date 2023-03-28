package com.yaser.ewallet.exception;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class WalletCreationException extends Exception {

    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}

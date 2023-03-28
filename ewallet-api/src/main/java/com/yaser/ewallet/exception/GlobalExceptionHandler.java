package com.yaser.ewallet.exception;

import com.yaser.ewallet.utils.REnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountCreationException.class)
    public ResponseEntity accountCreatException(AccountCreationException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(WalletCreationException.class)
    public ResponseEntity walletCreatException(WalletCreationException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity walletNotFound(WalletNotFoundException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MoneyBalanceNotFoundException.class)
    public ResponseEntity moneyBalanceNotFound(MoneyBalanceNotFoundException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionCreationException.class)
    public ResponseEntity transactionCreatException(TransactionCreationException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity currencyMismatchException(CurrencyMismatchException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WalletTypeMismatchException.class)
    public ResponseEntity walletMismatchException(WalletTypeMismatchException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity unsupportedOperationException(UnsupportedOperationException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity insufficientBalanceException(InsufficientBalanceException ex) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, false);
        hm.put(REnum.errors, ex.getMessage());
        return new ResponseEntity(hm, HttpStatus.PAYMENT_REQUIRED);
    }
}

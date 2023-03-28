package com.yaser.ewallet.controller;

import com.yaser.ewallet.dto.WalletTransactionDto;
import com.yaser.ewallet.exception.UnsupportedOperationException;
import com.yaser.ewallet.exception.*;
import com.yaser.ewallet.model.Transaction;
import com.yaser.ewallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity createTransaction(@RequestBody Transaction transaction)
            throws TransactionCreationException {
        return transactionService.createTransaction(transaction);
    }

    @PutMapping("/transfer")
    public ResponseEntity transferWallettoWallet(@RequestBody WalletTransactionDto walletTransactionDto)
            throws WalletNotFoundException, InsufficientBalanceException,
            CurrencyMismatchException, UnsupportedOperationException, WalletTypeMismatchException {
        return transactionService.transferBalanceWalletToWallet(walletTransactionDto);
    }
}

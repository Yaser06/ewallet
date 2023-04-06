package com.yaser.ewallet.controller;

import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.MoneyBalanceNotFoundException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.service.MoneyBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/moneybalance")
public class MoneyBalanceController {

    private final MoneyBalanceService moneyBalanceService;

    @PostMapping("/create")
    public ResponseEntity<MoneyBalanceDto> createMoneyBalance(@RequestBody MoneyBalance moneyBalance)
            throws WalletNotFoundException {
        return ResponseEntity.ok(moneyBalanceService.createMoneyBalance(moneyBalance));
    }

    @PutMapping("/update")
    public ResponseEntity<TransactionDto> updateMoneyBalance(@RequestParam(value = "walletPublicKey") String walletPublicKey,
                                                             @RequestParam(value = "transactionType") String transactionType,
                                                             @RequestParam(value = "amount") Double amount) throws
            WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {
        return ResponseEntity.ok(moneyBalanceService.updateMoneyBalance(walletPublicKey, transactionType, amount));
    }
}

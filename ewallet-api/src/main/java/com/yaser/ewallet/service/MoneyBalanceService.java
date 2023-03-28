package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.convertar.MoneyBalanceConverter;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.MoneyBalanceNotFoundException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Transaction;
import com.yaser.ewallet.model.TransactionType;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import com.yaser.ewallet.utils.REnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MoneyBalanceService {

    private final MoneyBalanceRepository moneyBalanceRepository;
    private final WalletService walletService;
    private final MoneyBalanceConverter moneyBalanceConverter;
    private final TransactionConverter transactionConverter;
    private final TransactionService transactionService;
    private static Logger logger = LoggerFactory.getLogger(MoneyBalanceService.class);

    public ResponseEntity createMoneyBalance(MoneyBalance moneyBalance) throws WalletNotFoundException {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        MoneyBalance saveMoneyBalance;
        try {
            saveMoneyBalance = moneyBalanceRepository.save(moneyBalance);
            hm.put(REnum.status, true);
            Wallet wallet = walletService.findById(saveMoneyBalance.getWallet().getId()).get();
            saveMoneyBalance.setWallet(wallet);
            hm.put(REnum.result, moneyBalanceConverter.toDto(saveMoneyBalance));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new WalletNotFoundException("Wallet not found exception.");
        }
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }

    public ResponseEntity updateMoneyBalance(String walletPublicKey, String transactionType, double amount)
            throws WalletNotFoundException, MoneyBalanceNotFoundException, InsufficientBalanceException {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Wallet wallet = walletService.getWallet(UUID.fromString(walletPublicKey));
        Optional<MoneyBalance> optionalMoneyBalance = moneyBalanceRepository.findById(wallet.getId());
        MoneyBalance moneyBalance;

        if (optionalMoneyBalance.isPresent()) {
            moneyBalance = optionalMoneyBalance.get();
        } else {
            throw new MoneyBalanceNotFoundException("Money Balance not exist");
        }

        if (wallet == null) {
            throw new WalletNotFoundException("İnvalid Wallet Public Key.");
        }

        if (TransactionType.AddMoney.name().equalsIgnoreCase(transactionType)) {
            moneyBalance.setAmount(moneyBalance.getAmount() + amount);
        } else if (TransactionType.Withdraw.name().equalsIgnoreCase(transactionType)) {
            if (moneyBalance.getAmount() < amount) {
                throw new InsufficientBalanceException("İnsufficient balance..");
            }
            moneyBalance.setAmount(moneyBalance.getAmount() - amount);
        }
        moneyBalanceRepository.save(moneyBalance);

        Transaction transaction = transactionService.saveTransactionOwnWallet(wallet, transactionType, amount);
        hm.put(REnum.status, true);
        hm.put(REnum.result, transactionConverter.toDto(transaction));
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }
}

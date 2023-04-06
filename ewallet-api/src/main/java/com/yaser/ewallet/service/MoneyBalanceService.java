package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.dto.TransactionDto;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public MoneyBalanceDto createMoneyBalance(MoneyBalance moneyBalance) throws WalletNotFoundException {
        MoneyBalance saveMoneyBalance;

        saveMoneyBalance = moneyBalanceRepository.save(moneyBalance);
        Optional<Wallet> wallet = walletService.findById(saveMoneyBalance.getWallet().getId());
        if (wallet.isPresent()) {
            saveMoneyBalance.setWallet(wallet.get());
            return moneyBalanceConverter.toDto(saveMoneyBalance);
        }
        throw new WalletNotFoundException("Wallet not found exception.");
    }

    public TransactionDto updateMoneyBalance(String walletPublicKey, String transactionType, double amount)
            throws WalletNotFoundException, MoneyBalanceNotFoundException, InsufficientBalanceException {

        Wallet wallet = walletService.getWallet(UUID.fromString(walletPublicKey));
        Optional<MoneyBalance> optionalMoneyBalance = moneyBalanceRepository.findById(wallet.getId());
        MoneyBalance moneyBalance;

        if (optionalMoneyBalance.isPresent()) {
            moneyBalance = optionalMoneyBalance.get();
        } else {
            throw new MoneyBalanceNotFoundException("Money Balance not exist");
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
        return transactionConverter.toDto(transaction);
    }
}

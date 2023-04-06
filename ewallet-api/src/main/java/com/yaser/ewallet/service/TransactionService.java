package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.dto.WalletTransactionDto;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.exception.UnsupportedOperationException;
import com.yaser.ewallet.exception.*;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Transaction;
import com.yaser.ewallet.model.TransactionType;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import com.yaser.ewallet.repository.TransactionRepository;
import com.yaser.ewallet.utils.FeeCalculate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final MoneyBalanceRepository moneyBalanceRepository;
    private final TransactionConverter transactionConverter;
    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Transactional
    public TransactionDto createTransaction(Transaction transaction)
            throws TransactionCreationException {

        Transaction savedTransaction;
        try {
            transaction.setCreatedDate(new Date(System.currentTimeMillis()));
            transaction.setTransactionId(UUID.randomUUID());
            savedTransaction = transactionRepository.save(transaction);
            return transactionConverter.toDto(savedTransaction);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            String message = "Error occured while saving transaction..";
            throw new TransactionCreationException(message);
        }
    }

    @Transactional
    public TransactionDto transferBalanceWalletToWallet(WalletTransactionDto walletTransactionDto)
            throws InsufficientBalanceException, WalletNotFoundException, CurrencyMismatchException,
            UnsupportedOperationException, WalletTypeMismatchException {

        String sourceWalletPublicKey = walletTransactionDto.getSourceWalletPublicKey();
        String targetWalletPublickey = walletTransactionDto.getTargetWalletPublicKey();

        Wallet sourceWallet = walletService.getWallet(UUID.fromString(sourceWalletPublicKey));
        Wallet targetWallet = walletService.getWallet(UUID.fromString(targetWalletPublickey));


        MoneyBalance moneyBalanceSource = moneyBalanceRepository.findByWallet(sourceWallet);
        MoneyBalance moneyBalanceTarget = moneyBalanceRepository.findByWallet(targetWallet);

        if (!sourceWallet.getWalletType().equals(targetWallet.getWalletType())) {
            throw new WalletTypeMismatchException("Wallet type different.");
        }

        if (!TransactionType.Remittance.equals(walletTransactionDto.getTransactionType())) {
            throw new UnsupportedOperationException("Transaction type not suitable");
        }
        if (!moneyBalanceSource.getCurrency().equals(moneyBalanceTarget.getCurrency())) {
            throw new CurrencyMismatchException("Currency not the same.");
        }

        Double feePrice = FeeCalculate.feeCalculate(walletTransactionDto.getAmount());
        if (!(moneyBalanceSource.getAmount() > (walletTransactionDto.getAmount() + feePrice))) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        moneyBalanceSource.setAmount(moneyBalanceSource.getAmount() - (walletTransactionDto.getAmount() + feePrice));
        moneyBalanceTarget.setAmount(moneyBalanceTarget.getAmount() + walletTransactionDto.getAmount());

        moneyBalanceRepository.save(moneyBalanceSource);
        moneyBalanceRepository.save(moneyBalanceTarget);


        Transaction transaction = saveTransactionWalletToWallet(sourceWallet, targetWallet, walletTransactionDto);
        TransactionDto transactionDto = transactionConverter.toDto(transaction);
        return transactionDto;
    }

    private Transaction saveTransactionWalletToWallet(Wallet sourceWallet, Wallet targetWallet,
                                                      WalletTransactionDto walletTransactionDto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(walletTransactionDto.getAmount());
        transaction.setDescription(walletTransactionDto.getDescription());
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(walletTransactionDto.getTransactionType());
        return transactionRepository.save(transaction);
    }

    protected Transaction saveTransactionOwnWallet(Wallet wallet, String transactionType, double amount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(amount);
        transaction.setDescription(transactionType);
        transaction.setSourceWallet(wallet);
        transaction.setTargetWallet(wallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.valueOf(transactionType));
        return transactionRepository.save(transaction);
    }
}

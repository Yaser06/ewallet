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
import com.yaser.ewallet.repository.WalletRepository;
import com.yaser.ewallet.utils.FeeCalculate;
import com.yaser.ewallet.utils.REnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final MoneyBalanceRepository moneyBalanceRepository;
    private final TransactionConverter transactionConverter;
    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public ResponseEntity createTransaction(Transaction transaction)
            throws TransactionCreationException {

        Map<REnum, Object> hm = new LinkedHashMap<>();
        Transaction savedTransaction;
        try {
            transaction.setCreatedDate(new Date(System.currentTimeMillis()));
            transaction.setTransactionId(UUID.randomUUID());
            savedTransaction = transactionRepository.save(transaction);
            hm.put(REnum.status, true);
            hm.put(REnum.result, transactionConverter.toDto(savedTransaction));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            String message = "Error occured while saving transaction..";
            throw new TransactionCreationException(message);
        }
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }

    public ResponseEntity transferBalanceWalletToWallet(WalletTransactionDto walletTransactionDto)
            throws InsufficientBalanceException, WalletNotFoundException, CurrencyMismatchException,
            UnsupportedOperationException, WalletTypeMismatchException {

        Map<REnum, Object> hm = new LinkedHashMap<>();
        String sourceWalletPublicKey = walletTransactionDto.getSourceWalletPublicKey();
        String targetWalletPublickey = walletTransactionDto.getTargetWalletPublicKey();

        Wallet sourceWallet = walletService.getWallet(UUID.fromString(sourceWalletPublicKey));
        Wallet targetWallet = walletService.getWallet(UUID.fromString(targetWalletPublickey));


        MoneyBalance moneyBalanceSource = moneyBalanceRepository.findByWallet(sourceWallet);
        MoneyBalance moneyBalanceTarget = moneyBalanceRepository.findByWallet(targetWallet);

        if (!sourceWallet.getWalletType().name().equals(targetWallet.getWalletType().name())) {
            throw new WalletTypeMismatchException("Wallet type different.");
        }

        if (!TransactionType.Remittance.name().equals(walletTransactionDto.getTransactionType().name())) {
            throw new UnsupportedOperationException("Transaction type not suitable");
        }
        if (!moneyBalanceSource.getCurrency().name().equals(moneyBalanceTarget.getCurrency().name())) {
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

        hm.put(REnum.status, true);
        Transaction transaction = saveTransactionWalletToWallet(sourceWallet, targetWallet, walletTransactionDto);
        TransactionDto transactionDto = transactionConverter.toDto(transaction);
        hm.put(REnum.result, transactionDto);
        return new ResponseEntity(hm, HttpStatus.CREATED);
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

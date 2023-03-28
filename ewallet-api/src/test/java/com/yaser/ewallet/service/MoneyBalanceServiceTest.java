package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.dto.convertar.MoneyBalanceConverter;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.MoneyBalanceNotFoundException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MoneyBalanceServiceTest {

    @Mock
    private MoneyBalanceRepository moneyBalanceRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private MoneyBalanceConverter moneyBalanceConverter;

    @Mock
    private TransactionConverter transactionConverter;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private MoneyBalanceService moneyBalanceService;

    @Test
    void createMoneyBalance_ReturnsCreatedStatus_WhenWallettIsValid() throws WalletNotFoundException {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();

        Mockito.when(moneyBalanceRepository.save(moneyBalance)).thenReturn(moneyBalance);
        Mockito.when(walletService.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Mockito.when(moneyBalanceConverter.toDto(moneyBalance)).thenReturn(new MoneyBalanceDto());

        ResponseEntity responseEntity = moneyBalanceService.createMoneyBalance(moneyBalance);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void updateMoneyBalance_ReturnCreatedSuccess_AddMoney() throws WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {

        double amount = 10d;
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        Mockito.when(walletService.getWallet(wallet.getWalletPublicKey())).thenReturn(wallet);
        Mockito.when(transactionConverter.toDto(any())).thenReturn(new TransactionDto());
        Mockito.when(moneyBalanceRepository.findById(wallet.getId())).thenReturn(Optional.of(moneyBalance));
        doReturn(new Transaction()).when(transactionService).saveTransactionOwnWallet(wallet,
                TransactionType.AddMoney.name(), amount);

        ResponseEntity responseEntity = moneyBalanceService
                .updateMoneyBalance(wallet.getWalletPublicKey().toString(),
                        TransactionType.AddMoney.name(), amount);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void updateMoneyBalance_ReturnSuccess_Withdraw() throws WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {

        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();

        Mockito.when(walletService.getWallet(wallet.getWalletPublicKey())).thenReturn(wallet);
        Mockito.when(moneyBalanceRepository.findById(wallet.getId())).thenReturn(Optional.of(moneyBalance));
        Mockito.when(transactionConverter.toDto(Mockito.any())).thenReturn(new TransactionDto());

        moneyBalanceService.updateMoneyBalance(wallet.getWalletPublicKey().toString()
                , TransactionType.Withdraw.name(), moneyBalance.getAmount());
        Mockito.verify(moneyBalanceRepository, Mockito.times(1)).save(moneyBalance);

    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletPublicKey(UUID.randomUUID());
        wallet.setAccount(getAccount());
        wallet.setProvider("WalletProvider");
        wallet.setCreatedDate(new Date());
        wallet.setMoneyBalance(new MoneyBalance());
        return wallet;
    }

    private MoneyBalance getMoneyBalance() {
        MoneyBalance moneyBalance = new MoneyBalance();
        moneyBalance.setId(1l);
        moneyBalance.setCurrency(Currency.Euro);
        moneyBalance.setCurrency(Currency.Euro);
        moneyBalance.setWallet(getWallet());
        moneyBalance.setAmount(100.0);
        return moneyBalance;
    }

    private Account getAccount() {
        Account account = new Account();
        account.setId(1l);
        account.setCreatedDate(new Date());
        account.setEmail("test@gmail.com");
        account.setPassword("123456");
        account.setFirstName("TestName");
        account.setLastName("TestLastName");
        account.setPhone("123456789");
        return account;
    }
}
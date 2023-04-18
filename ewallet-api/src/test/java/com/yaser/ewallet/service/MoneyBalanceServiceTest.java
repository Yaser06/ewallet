package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.dto.convertar.MoneyBalanceConverter;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.dto.convertar.WalletConverter;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.MoneyBalanceNotFoundException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import com.yaser.ewallet.repository.TransactionRepository;
import com.yaser.ewallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;


import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class MoneyBalanceServiceTest {

    private MoneyBalanceRepository moneyBalanceRepository;
    private WalletService walletService;
    private WalletConverter walletConverter;
    private WalletRepository walletRepository;

    private MoneyBalanceConverter moneyBalanceConverter;

    private TransactionConverter transactionConverter;

    private TransactionService transactionService;

    private MoneyBalanceService moneyBalanceService;
    private TransactionRepository transactionRepository;


    @BeforeAll
    public static void setEnviroment() {
        System.setProperty("jasypt.encryptor.password", "my-secret-value");
    }

    @BeforeEach
    public void setUp() {
        moneyBalanceRepository = mock(MoneyBalanceRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        walletRepository = mock(WalletRepository.class);
        transactionConverter = mock(TransactionConverter.class);

        walletConverter = new WalletConverter();
        moneyBalanceConverter = new MoneyBalanceConverter();

        walletService = new WalletService(walletRepository, walletConverter);

        transactionService = new TransactionService(transactionRepository
                , walletService, moneyBalanceRepository, transactionConverter);

        moneyBalanceService = new MoneyBalanceService(moneyBalanceRepository, walletService
                , moneyBalanceConverter, transactionConverter, transactionService);

    }

    @Test
    void createMoneyBalance_ReturnsCreatedStatus_WhenWallettIsValid() throws WalletNotFoundException {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();

        Mockito.when(moneyBalanceRepository.save(moneyBalance)).thenReturn(moneyBalance);
        Mockito.when(walletService.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        MoneyBalanceDto moneyBalanceDto = moneyBalanceService.createMoneyBalance(moneyBalance);
        Assertions.assertNotNull(moneyBalanceDto);
    }
    @Test
    void updateMoneyBalance_ReturnCreatedSuccess_AddMoney() throws WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {

        double amount = 10d;
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        Transaction transaction = new Transaction();
        transaction.setId(1l);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(amount);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.AddMoney);
        transaction.setSourceWallet(wallet);
        transaction.setTargetWallet(wallet);
        TransactionDto dto = getTransactionDto(transaction, TransactionType.AddMoney);


        Mockito.when(walletService.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.findByWalletPublicKey(wallet.getWalletPublicKey())).thenReturn(wallet);
        Mockito.when(moneyBalanceRepository.findById(wallet.getId())).thenReturn(Optional.of(moneyBalance));
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);
        Mockito.when(moneyBalanceService.updateMoneyBalance(wallet.getWalletPublicKey().toString(),
                TransactionType.AddMoney.name(), amount)).thenReturn(dto);
        Assertions.assertEquals(TransactionType.AddMoney, dto.getTransactionType());
        Assertions.assertEquals(amount, dto.getTransactionAmount());
        Assertions.assertEquals(moneyBalance.getAmount(), 110);
        Assertions.assertNotNull(dto.getCreatedDate());

    }
    @Test
    void updateMoneyBalance_ReturnCreatedSuccess_Withdraw() throws WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {

        double amount = 10d;
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        Transaction transaction = new Transaction();
        transaction.setId(1l);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(amount);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.Withdraw);
        transaction.setSourceWallet(wallet);
        transaction.setTargetWallet(wallet);
        TransactionDto dto = getTransactionDto(transaction, TransactionType.Withdraw);


        Mockito.when(walletService.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.findByWalletPublicKey(wallet.getWalletPublicKey())).thenReturn(wallet);
        Mockito.when(moneyBalanceRepository.findById(wallet.getId())).thenReturn(Optional.of(moneyBalance));
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);
        Mockito.when(moneyBalanceService.updateMoneyBalance(wallet.getWalletPublicKey().toString(),
                TransactionType.Withdraw.name(), amount)).thenReturn(dto);
        Assertions.assertEquals(TransactionType.Withdraw, dto.getTransactionType());
        Assertions.assertEquals(amount, dto.getTransactionAmount());
        Assertions.assertEquals(moneyBalance.getAmount(), 90);
        Assertions.assertNotNull(dto.getCreatedDate());

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
        moneyBalance.setWallet(getWallet());
        moneyBalance.setAmount(100.0);
        return moneyBalance;
    }

    private MoneyBalanceDto getMoneyBalanceDto(MoneyBalance moneyBalance) {
        MoneyBalanceDto moneyBalanceDto = new MoneyBalanceDto();
        moneyBalanceDto.setCurrency(moneyBalance.getCurrency());
        moneyBalanceDto.setWallet(moneyBalance.getWallet());
        moneyBalanceDto.setAmount(moneyBalance.getAmount());
        return moneyBalanceDto;
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

    private TransactionDto getTransactionDto(Transaction transaction, TransactionType transactionType) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionAmount(transaction.getTransactionAmount());
        transactionDto.setTransactionType(transactionType);
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setCreatedDate(transaction.getCreatedDate());
        return transactionDto;
    }
}
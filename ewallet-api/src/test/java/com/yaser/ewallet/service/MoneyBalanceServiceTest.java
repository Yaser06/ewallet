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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@AutoConfigureMockMvc
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
        MoneyBalanceDto expected = getMoneyBalanceDto(moneyBalance);

        Mockito.when(moneyBalanceRepository.save(moneyBalance)).thenReturn(moneyBalance);
        Mockito.when(walletService.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        Mockito.when(moneyBalanceConverter.toDto(moneyBalance)).thenReturn(expected);
        MoneyBalanceDto moneyBalanceDto = moneyBalanceService.createMoneyBalance(moneyBalance);

        Assertions.assertNotNull(moneyBalanceDto);
        Assertions.assertEquals(expected, moneyBalanceDto);
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

        TransactionDto transactionDto = moneyBalanceService.updateMoneyBalance(wallet.getWalletPublicKey().toString(),
                TransactionType.AddMoney.name(), amount);

        Assertions.assertNotNull(transactionDto);
    }

    @Test
    void updateMoneyBalance_ReturnSuccess_Withdraw() throws WalletNotFoundException, InsufficientBalanceException, MoneyBalanceNotFoundException {
        double amount = 10d;
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();

        Mockito.when(walletService.getWallet(wallet.getWalletPublicKey())).thenReturn(wallet);
        Mockito.when(moneyBalanceRepository.findById(wallet.getId())).thenReturn(Optional.of(moneyBalance));
        Mockito.when(transactionConverter.toDto(Mockito.any())).thenReturn(new TransactionDto());
        doReturn(new Transaction()).when(transactionService).saveTransactionOwnWallet(wallet,
                TransactionType.Withdraw.name(), amount);

        TransactionDto transactionDto = moneyBalanceService.updateMoneyBalance(wallet.getWalletPublicKey().toString()
                , TransactionType.Withdraw.name(), amount);

        Assertions.assertNotNull(transactionDto);
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
}
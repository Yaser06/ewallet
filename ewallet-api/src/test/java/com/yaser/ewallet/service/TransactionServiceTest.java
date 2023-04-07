package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.dto.WalletTransactionDto;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.exception.UnsupportedOperationException;
import com.yaser.ewallet.exception.*;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import com.yaser.ewallet.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private MoneyBalanceRepository moneyBalanceRepository;

    @Mock
    private TransactionConverter transactionConverter;

    @InjectMocks
    private TransactionService transactionService;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeAll
    public static void setEnviroment() {
        System.setProperty("jasypt.encryptor.password", "my-secret-value");
    }

    @BeforeEach
    public void setup() {
        transactionService = new TransactionService(transactionRepository,
                walletService, moneyBalanceRepository, transactionConverter);

        sourceWallet = new Wallet();
        sourceWallet.setWalletType(WalletType.Open);
        sourceWallet.setWalletPublicKey(UUID.randomUUID());

        targetWallet = new Wallet();
        targetWallet.setWalletType(WalletType.Open);
        targetWallet.setWalletPublicKey(UUID.randomUUID());
    }


    @Test
    public void createTransaction_Success() throws TransactionCreationException {
        Transaction transaction = getTransaction();
        TransactionDto expected = getTransactionDto(transaction);
        Mockito.when(transactionRepository.save(transaction)).thenReturn(transaction);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(expected);

        TransactionDto createdTransaction = transactionService.createTransaction(transaction);
        Assertions.assertNotNull(createdTransaction);
        Assertions.assertEquals(expected, createdTransaction);
    }

    @Test
    public void saveTransactionWalletToWallet_shouldCreateTransaction() throws Exception {

        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setAmount(500.0);
        walletTransactionDto.setTransactionType(TransactionType.Remittance);
        walletTransactionDto.setDescription("Transaction Description");
        walletTransactionDto.setSourceWalletPublicKey(sourceWallet.getWalletPublicKey().toString());
        walletTransactionDto.setTargetWalletPublicKey(targetWallet.getWalletPublicKey().toString());

        MoneyBalance moneyBalanceSource = new MoneyBalance();
        moneyBalanceSource.setAmount(1000.0);
        moneyBalanceSource.setCurrency(Currency.Euro);
        moneyBalanceSource.setWallet(sourceWallet);

        MoneyBalance moneyBalanceTarget = new MoneyBalance();
        moneyBalanceTarget.setAmount(0.0);
        moneyBalanceTarget.setCurrency(Currency.Euro);
        moneyBalanceTarget.setWallet(targetWallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(500.0);
        transaction.setDescription(walletTransactionDto.getDescription());
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(walletTransactionDto.getTransactionType());

        Mockito.when(walletService.getWallet(any(UUID.class)))
                .thenReturn(sourceWallet).thenReturn(targetWallet);
        Mockito.when(walletService.getWallet(sourceWallet.getWalletPublicKey()))
                .thenReturn(sourceWallet);
        Mockito.when(walletService.getWallet(targetWallet.getWalletPublicKey()))
                .thenReturn(targetWallet);

        Mockito.when(moneyBalanceRepository.findByWallet(sourceWallet)).thenReturn(moneyBalanceSource);
        Mockito.when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto dto = getTransactionDto(transaction, sourceWallet, targetWallet);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);
        TransactionDto result = transactionService.transferBalanceWalletToWallet(walletTransactionDto);

        Assertions.assertEquals(walletTransactionDto.getAmount(), result.getTransactionAmount());
        Assertions.assertEquals(walletTransactionDto.getDescription(), result.getDescription());
        Assertions.assertEquals(walletTransactionDto.getTransactionType(), result.getTransactionType());
    }


    @Test
    public void transactionWalletToWallet_ThrowExceptionByInsufficientBalance() throws Exception {

        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setAmount(1200.0);
        walletTransactionDto.setTransactionType(TransactionType.Remittance);
        walletTransactionDto.setDescription("Transaction Description");
        walletTransactionDto.setSourceWalletPublicKey(sourceWallet.getWalletPublicKey().toString());
        walletTransactionDto.setTargetWalletPublicKey(targetWallet.getWalletPublicKey().toString());

        MoneyBalance moneyBalanceSource = new MoneyBalance();
        moneyBalanceSource.setAmount(1000.0);
        moneyBalanceSource.setCurrency(Currency.Euro);
        moneyBalanceSource.setWallet(sourceWallet);

        MoneyBalance moneyBalanceTarget = new MoneyBalance();
        moneyBalanceTarget.setAmount(0.0);
        moneyBalanceTarget.setCurrency(Currency.Euro);
        moneyBalanceTarget.setWallet(targetWallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(1200.0);
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.Remittance);

        Mockito.when(walletService.getWallet(any(UUID.class)))
                .thenReturn(sourceWallet).thenReturn(targetWallet);
        Mockito.when(walletService.getWallet(sourceWallet.getWalletPublicKey()))
                .thenReturn(sourceWallet);
        Mockito.when(walletService.getWallet(targetWallet.getWalletPublicKey()))
                .thenReturn(targetWallet);

        Mockito.when(moneyBalanceRepository.findByWallet(sourceWallet)).thenReturn(moneyBalanceSource);
        Mockito.when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto dto = getTransactionDto(transaction, sourceWallet, targetWallet);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.transferBalanceWalletToWallet(walletTransactionDto);
        });
    }


    @Test
    public void transactionWalletToWallet_ThrowExceptionByCurrencyMismatch() throws Exception {
        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setAmount(500.0);
        walletTransactionDto.setTransactionType(TransactionType.Remittance);
        walletTransactionDto.setDescription("Transaction Description");
        walletTransactionDto.setSourceWalletPublicKey(sourceWallet.getWalletPublicKey().toString());
        walletTransactionDto.setTargetWalletPublicKey(targetWallet.getWalletPublicKey().toString());

        MoneyBalance moneyBalanceSource = new MoneyBalance();
        moneyBalanceSource.setAmount(1000.0);
        moneyBalanceSource.setCurrency(Currency.Dolar);
        moneyBalanceSource.setWallet(sourceWallet);

        MoneyBalance moneyBalanceTarget = new MoneyBalance();
        moneyBalanceTarget.setAmount(0.0);
        moneyBalanceTarget.setCurrency(Currency.Euro);
        moneyBalanceTarget.setWallet(targetWallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(500.0);
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.Remittance);

        Mockito.when(walletService.getWallet(any(UUID.class)))
                .thenReturn(sourceWallet).thenReturn(targetWallet);
        Mockito.when(walletService.getWallet(sourceWallet.getWalletPublicKey()))
                .thenReturn(sourceWallet);
        Mockito.when(walletService.getWallet(targetWallet.getWalletPublicKey()))
                .thenReturn(targetWallet);

        Mockito.when(moneyBalanceRepository.findByWallet(sourceWallet)).thenReturn(moneyBalanceSource);
        Mockito.when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto dto = getTransactionDto(transaction, sourceWallet, targetWallet);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);

        assertThrows(CurrencyMismatchException.class, () -> {
            transactionService.transferBalanceWalletToWallet(walletTransactionDto);
        });
    }

    @Test
    public void transactionWalletToWallet_ThrowExceptionByUnsupportedOperation() throws Exception {

        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setAmount(500.0);
        walletTransactionDto.setTransactionType(TransactionType.AddMoney);
        walletTransactionDto.setDescription("Transaction Description");
        walletTransactionDto.setSourceWalletPublicKey(sourceWallet.getWalletPublicKey().toString());
        walletTransactionDto.setTargetWalletPublicKey(targetWallet.getWalletPublicKey().toString());

        MoneyBalance moneyBalanceSource = new MoneyBalance();
        moneyBalanceSource.setAmount(1000.0);
        moneyBalanceSource.setCurrency(Currency.Euro);
        moneyBalanceSource.setWallet(sourceWallet);

        MoneyBalance moneyBalanceTarget = new MoneyBalance();
        moneyBalanceTarget.setAmount(0.0);
        moneyBalanceTarget.setCurrency(Currency.Euro);
        moneyBalanceTarget.setWallet(targetWallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(500.0);
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.Remittance);

        Mockito.when(walletService.getWallet(any(UUID.class)))
                .thenReturn(sourceWallet).thenReturn(targetWallet);
        Mockito.when(walletService.getWallet(sourceWallet.getWalletPublicKey()))
                .thenReturn(sourceWallet);
        Mockito.when(walletService.getWallet(targetWallet.getWalletPublicKey()))
                .thenReturn(targetWallet);

        Mockito.when(moneyBalanceRepository.findByWallet(sourceWallet)).thenReturn(moneyBalanceSource);
        Mockito.when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto dto = getTransactionDto(transaction, sourceWallet, targetWallet);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);

        assertThrows(UnsupportedOperationException.class, () -> {
            transactionService.transferBalanceWalletToWallet(walletTransactionDto);
        });
    }

    @Test
    public void transactionWalletToWallet_ThrowExceptionByWalletTypeMismatch() throws Exception {

        sourceWallet.setWalletType(WalletType.Closed);
        targetWallet.setWalletType(WalletType.Open);

        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setAmount(500.0);
        walletTransactionDto.setTransactionType(TransactionType.AddMoney);
        walletTransactionDto.setDescription("Transaction Description");
        walletTransactionDto.setSourceWalletPublicKey(sourceWallet.getWalletPublicKey().toString());
        walletTransactionDto.setTargetWalletPublicKey(targetWallet.getWalletPublicKey().toString());

        MoneyBalance moneyBalanceSource = new MoneyBalance();
        moneyBalanceSource.setAmount(1000.0);
        moneyBalanceSource.setCurrency(Currency.Euro);
        moneyBalanceSource.setWallet(sourceWallet);

        MoneyBalance moneyBalanceTarget = new MoneyBalance();
        moneyBalanceTarget.setAmount(0.0);
        moneyBalanceTarget.setCurrency(Currency.Euro);
        moneyBalanceTarget.setWallet(targetWallet);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionAmount(500.0);
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);
        transaction.setCreatedDate(new Date(System.currentTimeMillis()));
        transaction.setTransactionType(TransactionType.Remittance);

        Mockito.when(walletService.getWallet(any(UUID.class)))
                .thenReturn(sourceWallet).thenReturn(targetWallet);
        Mockito.when(walletService.getWallet(sourceWallet.getWalletPublicKey()))
                .thenReturn(sourceWallet);
        Mockito.when(walletService.getWallet(targetWallet.getWalletPublicKey()))
                .thenReturn(targetWallet);

        Mockito.when(moneyBalanceRepository.findByWallet(sourceWallet)).thenReturn(moneyBalanceSource);
        Mockito.when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto dto = getTransactionDto(transaction, sourceWallet, targetWallet);
        Mockito.when(transactionConverter.toDto(transaction)).thenReturn(dto);

        assertThrows(WalletTypeMismatchException.class, () -> {
            transactionService.transferBalanceWalletToWallet(walletTransactionDto);
        });
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletPublicKey(UUID.randomUUID());
        wallet.setAccount(new Account());
        wallet.setProvider("WalletProvider");
        wallet.setCreatedDate(new Date());
        return wallet;
    }

    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(10.0);
        transaction.setTransactionType(TransactionType.AddMoney);
        transaction.setDescription("Transaction");
        Wallet wallet = getWallet();
        transaction.setSourceWallet(wallet);
        transaction.setTargetWallet(wallet);
        return transaction;
    }

    private TransactionDto getTransactionDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionAmount(transaction.getTransactionAmount());
        transactionDto.setTransactionType(TransactionType.Remittance);
        transactionDto.setDescription(transaction.getDescription());
        Wallet wallet = new Wallet();
        transactionDto.setSourceWallet(wallet.getId());
        transactionDto.setTargetWallet(wallet.getId());
        return transactionDto;
    }

    private TransactionDto getTransactionDto(Transaction transaction, Wallet sourceWallet, Wallet targetWallet) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionAmount(transaction.getTransactionAmount());
        transactionDto.setTransactionType(TransactionType.Remittance);
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setSourceWallet(sourceWallet.getId());
        transactionDto.setTargetWallet(targetWallet.getId());
        return transactionDto;
    }

}


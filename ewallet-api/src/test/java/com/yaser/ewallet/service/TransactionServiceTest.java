package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.dto.WalletTransactionDto;
import com.yaser.ewallet.dto.convertar.TransactionConverter;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.repository.MoneyBalanceRepository;
import com.yaser.ewallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepository,
                walletService, moneyBalanceRepository, transactionConverter);
    }

    @Test
    public void saveTransactionOwnWallet_shouldCreateTransaction() {

        Wallet wallet = getWallet();
        Transaction transaction = getTransaction();

        Mockito.lenient().when(transactionConverter.toDto(any())).thenReturn(new TransactionDto());
        when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction transaction1 = transactionService
                .saveTransactionOwnWallet(wallet, TransactionType.AddMoney.name(), 10.0);
        ResponseEntity responseEntity = new ResponseEntity(transaction1, HttpStatus.CREATED);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testTransferBalanceWalletToWallet() throws Exception {
        UUID uuid = UUID.randomUUID();

        Wallet sourceWallet = mock(Wallet.class);
        Wallet targetWallet = mock(Wallet.class);
        when(walletService.getWallet(uuid)).thenReturn(sourceWallet);
        when(walletService.getWallet(uuid)).thenReturn(targetWallet);
        MoneyBalance moneyBalanceTarget = mock(MoneyBalance.class);
        when(moneyBalanceRepository.findByWallet(targetWallet)).thenReturn(moneyBalanceTarget);

        WalletTransactionDto walletTransactionDto = mock(WalletTransactionDto.class);
        when(walletTransactionDto.getSourceWalletPublicKey()).thenReturn(uuid.toString());
        when(walletTransactionDto.getTargetWalletPublicKey()).thenReturn(uuid.toString());
        when(walletTransactionDto.getTransactionType()).thenReturn(TransactionType.Remittance);
        when(walletTransactionDto.getAmount()).thenReturn(100.0);

        when(targetWallet.getWalletType()).thenReturn(WalletType.Open);
        when(moneyBalanceTarget.getCurrency()).thenReturn(Currency.Euro);
        when(moneyBalanceTarget.getAmount()).thenReturn(500.0);

        TransactionDto dto = getTransactionDto(targetWallet, sourceWallet);
        when(transactionConverter.toDto(any())).thenReturn(dto);

        ResponseEntity actualResult = transactionService.transferBalanceWalletToWallet(walletTransactionDto);
        assertEquals(HttpStatus.CREATED, actualResult.getStatusCode());
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
        Wallet wallet = getWallet();
        transaction.setSourceWallet(wallet);
        transaction.setTargetWallet(wallet);
        return transaction;
    }

    private TransactionDto getTransactionDto(Wallet targetWallet, Wallet sourceWallet) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(UUID.randomUUID());
        dto.setCreatedDate(new Date());
        dto.setDescription("EFT");
        dto.setTargetWallet(targetWallet.getId());
        dto.setSourceWallet(sourceWallet.getId());
        dto.setTransactionAmount(100.0);
        return dto;
    }
}


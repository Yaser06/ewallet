package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.convertar.WalletConverter;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.model.WalletType;
import com.yaser.ewallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletConverter walletConverter;
    @Mock
    private WalletService walletService;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.openMocks(this);

        walletService = new WalletService(walletRepository, walletConverter);
        sourceWallet = getWallet();
        targetWallet = getWallet();
    }

    @Test
    public void createWallet_ReturnsCreatedStatus() throws Exception {
        Wallet wallet = getWallet();

        ResponseEntity response = walletService.createWallet(wallet);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void getWallet_ReturnsResultSame() throws Exception {
        UUID walletPublicKey = UUID.randomUUID();
        Wallet wallet = getWallet();
        Mockito.when(walletRepository.findByWalletPublicKey(walletPublicKey))
                .thenReturn(wallet);

        Wallet result = walletService.getWallet(walletPublicKey);
        Assertions.assertEquals(wallet, result);
    }

    @Test
    public void getWalletById_ReturnsResultSame() {
        Long walletId = 1L;
        Optional<Wallet> optionalWallet = Optional.of(sourceWallet);
        when(walletRepository.findById(walletId)).thenReturn(optionalWallet);
        Optional<Wallet> result = walletService.findById(walletId);

        assertEquals(optionalWallet, result);
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletPublicKey(UUID.randomUUID());
        wallet.setAccount(new Account());
        wallet.setProvider("WalletProvider");
        wallet.setCreatedDate(new Date());
        wallet.setWalletType(WalletType.Open);
        return wallet;
    }
}

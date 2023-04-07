package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.WalletDto;
import com.yaser.ewallet.dto.convertar.WalletConverter;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.model.WalletType;
import com.yaser.ewallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletConverter walletConverter;
    @Mock
    private WalletService walletService;
    private Wallet sourceWallet;
    private Wallet targetWallet;

    @BeforeAll
    public static void setEnviroment() {
        System.setProperty("jasypt.encryptor.password", "my-secret-value");
    }

    @BeforeEach
    public void setup() {
        walletService = new WalletService(walletRepository, walletConverter);
        sourceWallet = getWallet();
        targetWallet = getWallet();
    }

    @Test
    public void createWallet_ReturnsCreatedStatus() throws Exception {
        Wallet wallet = getWallet();
        WalletDto expected = getWalletDto(wallet);
        Mockito.when(walletRepository.save(wallet)).thenReturn(wallet);
        Mockito.when(walletConverter.toDTO(wallet)).thenReturn(expected);
        WalletDto actual = walletService.createWallet(wallet);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
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

    private WalletDto getWalletDto(Wallet wallet) {
        WalletDto walletDto = new WalletDto();
        walletDto.setId(wallet.getId());
        walletDto.setWalletPublicKey(wallet.getWalletPublicKey());
        walletDto.setProvider(wallet.getProvider());
        walletDto.setCreatedDate(wallet.getCreatedDate());
        walletDto.setMoneyBalance(wallet.getMoneyBalance());
        return walletDto;
    }
}

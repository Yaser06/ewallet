package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.dto.convertar.AnotherCardInformationConverter;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.AnotherCardInformation;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.AnotherCardInformationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class AnotherCardInformationServiceTest {

    @Mock
    private AnotherCardInformationRepository informationRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AnotherCardInformationConverter anotherCardInformationConverter;

    @InjectMocks
    private AnotherCardInformationService informationService;

    private AnotherCardInformation information;

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        information = getAnotherCardInformation();
        wallet = getWallet();
    }

    @Test
    public void createAnotherCardInformation_WhenWalletExists() throws WalletNotFoundException {
        AnotherCardInformationDto expected = new AnotherCardInformationDto();
        expected.setCreatedDate(information.getCreatedDate());
        expected.setWallet(wallet);
        Mockito.when(walletService.findById(Mockito.anyLong())).thenReturn(Optional.of(wallet));
        Mockito.when(informationRepository.save(Mockito.any(AnotherCardInformation.class))).thenReturn(information);
        Mockito.when(anotherCardInformationConverter.toDto(Mockito.any(AnotherCardInformation.class))).thenReturn(expected);
        AnotherCardInformationDto actual = informationService.createAnotherCardInformation(information);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createAnotherCardInformation_ThrowExceptionByWalletNotFound() {
        Mockito.when(walletService.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> informationService.createAnotherCardInformation(information));
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

    private AnotherCardInformation getAnotherCardInformation() {
        AnotherCardInformation information = new AnotherCardInformation();
        information.setId(1L);
        information.setCardNumber("1234567812345678");
        information.setCreatedDate(new Date(System.currentTimeMillis()));
        information.setWallet(getWallet());
        return information;
    }
}

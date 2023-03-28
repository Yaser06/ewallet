package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.dto.convertar.AnotherCardInformationConverter;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.AnotherCardInformation;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.AnotherCardInformationRepository;
import com.yaser.ewallet.utils.REnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class AnotherCardInformationServiceTest {

    @InjectMocks
    private AnotherCardInformationService anotherCardInformationService;

    @Mock
    private AnotherCardInformationRepository anotherCardInformationRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AnotherCardInformationConverter anotherCardInformationConverter;

    @Test
    public void createAnotherCardInformation_ReturnsCreatedStatus_WhenWalletIsValid() throws WalletNotFoundException {
        Wallet wallet = getWallet();
        AnotherCardInformation information = getAnotherCardInformation();

        Mockito.when(anotherCardInformationRepository.save(Mockito.any())).thenReturn(information);
        Mockito.when(walletService.findById(Mockito.any())).thenReturn(Optional.of(wallet));
        Mockito.when(anotherCardInformationConverter.toDto(Mockito.any())).thenReturn(new AnotherCardInformationDto());

        ResponseEntity response = anotherCardInformationService.createAnotherCardInformation(information);

        Map<REnum, Object> expectedResponse = new LinkedHashMap<>();
        expectedResponse.put(REnum.status, true);
        expectedResponse.put(REnum.result, new AnotherCardInformationDto());
        Assertions.assertEquals(new ResponseEntity(expectedResponse, HttpStatus.CREATED), response);
    }

    @Test
    void createAnotherCardInformation_WalletNotFound() {

        Wallet wallet = getWallet();
        AnotherCardInformation information = getAnotherCardInformation();
        information.setWallet(wallet);

        Mockito.when(walletService.findById(Mockito.eq(wallet.getId()))).thenReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class,
                () -> anotherCardInformationService.createAnotherCardInformation(information));
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

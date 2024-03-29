package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.service.AnotherCardInformationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AnotherCardInformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnotherCardInformationService anotherCardInformationService;

    @BeforeAll
    public static void setEnviroment() {
        System.setProperty("jasypt.encryptor.password", "my-secret-value");
    }

    @Test
    void createAnotherCard_ReturnsOk_WhenAnotherCardInformationIsValid() throws Exception {
        Wallet wallet = getWallet();
        AnotherCardInformation anotherCardInformation = getInformation();
        anotherCardInformation.setWallet(wallet);
        AnotherCardInformationDto expected = getInformationDto(anotherCardInformation);

        given(anotherCardInformationService.createAnotherCardInformation(anotherCardInformation))
                .willReturn(expected);
        mockMvc.perform(post("/api/v1/anothercard/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(anotherCardInformation)))
                .andExpect(status().isOk());
    }

    @Test
    void createAnotherCard_ReturnsNotFoundStatus_WhenWalletNotFound() throws Exception {
        Wallet wallet = null;
        AnotherCardInformation anotherCardInformation = getInformation();
        anotherCardInformation.setWallet(wallet);

        doThrow(new WalletNotFoundException("Test")).when(anotherCardInformationService)
                .createAnotherCardInformation(anotherCardInformation);

        mockMvc.perform(post("/api/v1/anothercard/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(anotherCardInformation)))
                .andExpect(status().isNotFound());
    }

    private AnotherCardInformation getInformation() {
        AnotherCardInformation anotherCardInformation = new AnotherCardInformation();
        anotherCardInformation.setId(1l);
        anotherCardInformation.setAnotherCardInformationType(AnotherCardInformationType.DriverLicense);
        anotherCardInformation.setCardNumber("1234567890123456");
        anotherCardInformation.setCreatedDate(new Date());
        return anotherCardInformation;
    }

    private AnotherCardInformationDto getInformationDto(AnotherCardInformation anotherCardInformation) {
        AnotherCardInformationDto anotherCardInformationDto = new AnotherCardInformationDto();
        anotherCardInformationDto.setAnotherCardInformationType(anotherCardInformation.getAnotherCardInformationType());
        anotherCardInformationDto.setCardNumber(anotherCardInformation.getCardNumber());
        anotherCardInformationDto.setCreatedDate(anotherCardInformation.getCreatedDate());
        return anotherCardInformationDto;
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

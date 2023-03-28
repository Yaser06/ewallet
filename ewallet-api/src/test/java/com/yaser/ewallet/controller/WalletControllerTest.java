package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.exception.WalletCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Test
    void createWallet_ReturnsOkStatus_WhenWalletIsValid() throws Exception {
        Wallet wallet = getWallet();

        given(walletService.createWallet(any(Wallet.class))).willReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/api/v1/wallet/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(wallet)))
                .andExpect(status().isOk());
    }

    @Test
    void createWallet_ReturnsBadRequestStatus_WhenWalletIsInvalid() throws Exception {
        Wallet wallet = getWallet();

        given(walletService.createWallet(any(Wallet.class))).
                willThrow(new WalletCreationException("Wallet Not Found"));
        mockMvc.perform(post("/api/v1/wallet/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(wallet)))
                .andExpect(status().is4xxClientError());
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

    private Account getAccount() {
        Account account = new Account();
        account.setId(1l);
        account.setCreatedDate(new Date());
        account.setEmail("test@gmail.com");
        account.setPassword("123456");
        account.setFirstName("TestFirstName");
        account.setLastName("TestLastName");
        account.setPhone("123456789");
        List<Wallet> walletList = new ArrayList<>();
        account.setWallets(walletList);
        return account;
    }
}

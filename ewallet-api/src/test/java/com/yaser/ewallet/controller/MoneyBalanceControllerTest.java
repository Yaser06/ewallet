package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.Currency;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.service.MoneyBalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MoneyBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MoneyBalanceService moneyBalanceService;

    @Test
    void createMoneyBalance_ReturnsOkStatus_WhenMoneyBalanceIsValid() throws Exception {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        moneyBalance.setWallet(wallet);

        given(moneyBalanceService.createMoneyBalance(any(MoneyBalance.class)))
                .willReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/api/v1/moneybalance/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(moneyBalance)))
                .andExpect(status().isOk());
    }

    @Test
    void createMoneyBalance_ReturnsNotFoundStatus_WhenWalletNotFound() throws Exception {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        moneyBalance.setWallet(wallet);

        doThrow(new WalletNotFoundException("Wallet Not Found")).when(moneyBalanceService)
                .createMoneyBalance(any(MoneyBalance.class));
        mockMvc.perform(post("/api/v1/moneybalance/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(moneyBalance)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMoneyBalance_ReturnsOkStatus_WhenMoneyBalanceUpdatedSuccessfully() throws Exception {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        moneyBalance.setWallet(wallet);

        given(moneyBalanceService.updateMoneyBalance(anyString(), anyString(), anyDouble()))
                .willReturn(ResponseEntity.ok().build());
        mockMvc.perform(put("/api/v1/moneybalance/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .param("walletPublicKey", "testPublicKey")
                        .param("transactionType", "DEPOSIT")
                        .param("amount", "500.0"))
                .andExpect(status().isOk());
    }

    @Test
    void updateMoneyBalance_ReturnsNotFoundStatus_WhenWalletNotFound() throws Exception {
        doThrow(new WalletNotFoundException("Wallet Not Found Exception"))
                .when(moneyBalanceService).updateMoneyBalance(anyString(), anyString(), anyDouble());

        mockMvc.perform(put("/api/v1/moneybalance/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .param("walletPublicKey", "testPublicKey")
                        .param("transactionType", "DEPOSIT")
                        .param("amount", "500.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMoneyBalance_ReturnsPaymentRequiredStatus_WhenAmountNotEnough() throws Exception {
        String errorMessage = "Insufficient Balance Exception.";
        doThrow(new InsufficientBalanceException(errorMessage)).when(moneyBalanceService)
                .updateMoneyBalance(anyString(), eq("Withdraw"), anyDouble());

        mockMvc.perform(put("/api/v1/moneybalance/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .param("transactionType", "Withdraw")
                        .param("walletPublicKey", "TestKey")
                        .param("amount", "200.0"))
                .andExpect(status().isPaymentRequired())
                .andExpect(content()
                        .string("{\"status\":false,\"errors\":\"Insufficient Balance Exception.\"}"));
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
        moneyBalance.setCurrency(Currency.Euro);
        moneyBalance.setAmount(100.0);
        return moneyBalance;
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
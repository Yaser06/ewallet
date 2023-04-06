package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.exception.InsufficientBalanceException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.*;
import com.yaser.ewallet.service.MoneyBalanceService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private MoneyBalanceDto moneyBalanceDto;

    private TransactionDto transactionDto;
    private static final String WALLET_PUBLIC_KEY = "3f3d9a2a-5425-43f5-b8a5-b16e5b1601d6";
    private static final double AMOUNT = 2000.0;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        Wallet wallet = new Wallet();
        wallet.setWalletPublicKey(UUID.fromString(WALLET_PUBLIC_KEY));

        moneyBalanceDto = new MoneyBalanceDto();
        moneyBalanceDto.setAmount(2000.0);
        moneyBalanceDto.setWallet(wallet);

        transactionDto = new TransactionDto();
        transactionDto.setTransactionAmount(AMOUNT);
        transactionDto.setTransactionType(TransactionType.Remittance);
        transactionDto.setSourceWallet(wallet.getId());
        transactionDto.setTargetWallet(wallet.getId());
    }

    @Test
    void createMoneyBalance_ReturnsOkStatus_WhenMoneyBalanceIsValid() throws Exception {
        Wallet wallet = getWallet();
        MoneyBalance moneyBalance = getMoneyBalance();
        moneyBalance.setWallet(wallet);
        MoneyBalanceDto moneyBalanceDto = getMonetBalanceDto(moneyBalance);

        given(moneyBalanceService.createMoneyBalance(any(MoneyBalance.class)))
                .willReturn(moneyBalanceDto);
        mockMvc.perform(post("/api/v1/moneybalance/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(moneyBalance)))
                .andExpect(status().isOk());
    }

    @Test
    void createMoneyBalance_ReturnsNotFoundStatus_WhenWalletNotFound() throws Exception {
        MoneyBalance moneyBalance = getMoneyBalance();

        doThrow(new WalletNotFoundException("Wallet Not Found")).when(moneyBalanceService)
                .createMoneyBalance(moneyBalance);
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
        MoneyBalanceDto moneyBalanceDto = getMonetBalanceDto(moneyBalance);
        TransactionDto transactionDto = mock(TransactionDto.class);
        when(moneyBalanceService.updateMoneyBalance(WALLET_PUBLIC_KEY, TransactionType.AddMoney.name(), AMOUNT))
                .thenReturn(transactionDto);

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
                .andExpect(status().isPaymentRequired());
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
        moneyBalance.setAmount(100.0);
        return moneyBalance;
    }

    private MoneyBalanceDto getMonetBalanceDto(MoneyBalance moneyBalance) {
        MoneyBalanceDto moneyBalanceDto = new MoneyBalanceDto();
        moneyBalance.setCurrency(moneyBalance.getCurrency());
        moneyBalance.setAmount(moneyBalance.getAmount());
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
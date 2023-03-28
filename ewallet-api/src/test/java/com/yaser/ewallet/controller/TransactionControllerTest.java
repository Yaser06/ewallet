package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.dto.WalletTransactionDto;
import com.yaser.ewallet.exception.TransactionCreationException;
import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Transaction;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.service.TransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createTransaction_ReturnsOkStatus_WhenTransactionIsValid() throws Exception {

        Wallet sourceWallet = getWallet();
        Wallet targetWallet = getWallet();

        Transaction transaction = getTransaction();
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);

        given(transactionService.createTransaction(any(Transaction.class)))
                .willReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/api/v1/transaction/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());
    }

    @Test
    void createTransaction_ReturnsBadRequestStatus_WhenTransactionIsInvalid() throws Exception {
        Wallet sourceWallet = getWallet();
        Wallet targetWallet = getWallet();

        Transaction transaction = getTransaction();
        transaction.setSourceWallet(sourceWallet);
        transaction.setTargetWallet(targetWallet);

        given(transactionService.createTransaction(any(Transaction.class)))
                .willThrow(new TransactionCreationException("Invalid transaction"));
        mockMvc.perform(post("/api/v1/transaction/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void transferWallettoWallet_ReturnsOkStatus_WhenTransactionIsValid() throws Exception {
        WalletTransactionDto walletTransactionDto = getWalletTransactionDto();

        given(transactionService.transferBalanceWalletToWallet(any(WalletTransactionDto.class)))
                .willReturn(ResponseEntity.ok().build());
        mockMvc.perform(put("/api/v1/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(walletTransactionDto)))
                .andExpect(status().isOk());
    }

    @Test
    void transferWallettoWallet_ReturnsBadRequestStatus_WhenTransactionIsInvalid() throws Exception {
        WalletTransactionDto walletTransactionDto = null;

        mockMvc.perform(put("/api/v1/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(walletTransactionDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setDescription("Transaction Test");
        transaction.setCreatedDate(new Date());
        return transaction;
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletPublicKey(UUID.randomUUID());
        wallet.setProvider("WalletProvider");
        wallet.setCreatedDate(new Date());
        wallet.setMoneyBalance(new MoneyBalance());
        return wallet;
    }

    private WalletTransactionDto getWalletTransactionDto() {
        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setTargetWalletPublicKey(UUID.randomUUID().toString());
        walletTransactionDto.setSourceWalletPublicKey(UUID.randomUUID().toString());
        walletTransactionDto.setDescription("Description");
        walletTransactionDto.setAmount(50.0);
        return walletTransactionDto;
    }
}
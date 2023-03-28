package com.yaser.ewallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_ReturnsOkStatus_WhenAccountIsValid() throws Exception {
        Account account = getAccount();
        given(accountService.createAccount(account)).willReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/v1/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                .content(new ObjectMapper().writeValueAsString(account))).andExpect(status().isOk());
    }

    @Test
    void createAccount_ReturnsBadRequestStatus_WhenAccountIsInvalid() throws Exception {
        Account account = getAccount();
        ResponseEntity responseEntity = new ResponseEntity(account, HttpStatus.BAD_REQUEST);
        given(accountService.createAccount(account)).willReturn(responseEntity);

        mockMvc.perform(post("/api/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                        .content(objectMapper.writeValueAsString(account))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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


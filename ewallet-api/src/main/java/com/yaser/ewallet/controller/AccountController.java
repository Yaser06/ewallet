package com.yaser.ewallet.controller;

import com.yaser.ewallet.exception.AccountCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity createAccount(@RequestBody Account account) throws AccountCreationException {
        return accountService.createAccount(account);
    }
}

package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.AccountDto;
import com.yaser.ewallet.dto.convertar.AccountConverter;
import com.yaser.ewallet.exception.AccountCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;
    private static Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountDto createAccount(Account account) throws AccountCreationException {
        Account saveAccount;
        try {
            account.setCreatedDate(new Date(System.currentTimeMillis()));
            saveAccount = accountRepository.save(account);
            return accountConverter.toDTO(saveAccount);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new AccountCreationException("Error occurred while saving...");
        }
    }
}

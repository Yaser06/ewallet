package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.convertar.AccountConverter;
import com.yaser.ewallet.exception.AccountCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.repository.AccountRepository;
import com.yaser.ewallet.utils.REnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;
    private static Logger logger = LoggerFactory.getLogger(AccountService.class);

    public ResponseEntity createAccount(Account account) throws AccountCreationException {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Account saveAccount;
        try {
            account.setCreatedDate(new Date(System.currentTimeMillis()));
            saveAccount = accountRepository.save(account);
            hm.put(REnum.status, true);
            hm.put(REnum.result, accountConverter.toDTO(saveAccount));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new AccountCreationException("Error occurred while saving...");
        }
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }
}

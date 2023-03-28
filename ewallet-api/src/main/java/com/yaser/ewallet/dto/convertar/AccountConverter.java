package com.yaser.ewallet.dto.convertar;

import com.yaser.ewallet.dto.AccountDto;
import com.yaser.ewallet.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {

    public AccountDto toDTO(Account account) {
        AccountDto dto = new AccountDto();
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setPhone(account.getPhone());
        dto.setEmail(account.getEmail());
        dto.setPassword(account.getPassword());
        dto.setWallets(account.getWallets());
        return dto;
    }

    public Account toEntity(AccountDto dto) {
        Account account = new Account();
        account.setFirstName(dto.getFirstName());
        account.setLastName(dto.getLastName());
        account.setPhone(dto.getPhone());
        account.setEmail(dto.getEmail());
        account.setPassword(dto.getPassword());
        return account;
    }
}

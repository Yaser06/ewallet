package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.convertar.AccountConverter;
import com.yaser.ewallet.exception.AccountCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.AccountRepository;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Spy
    private AccountRepository accountRepository;

    @Spy
    private AccountConverter accountConverter;


    @Test
    public void createAccount_WhenAccountIsValid() throws AccountCreationException {
        Account account = getAccount();

        List<Wallet> walletList = new ArrayList<>();
        account.setWallets(walletList);

        Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);

        ResponseEntity<Account> res = accountService.createAccount(account);
        Assertions.assertNotNull(res);
    }

    @Test
    public void createAccount_ThrowExceptionByRepositoryTest() {
        Account account = getAccount();

        List<Wallet> walletList = new ArrayList<>();
        account.setWallets(walletList);

        doThrow(HibernateException.class).when(accountRepository).save(any());

        Executable executable = () -> accountService.createAccount(account);

        Assertions.assertThrows(AccountCreationException.class, executable);
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

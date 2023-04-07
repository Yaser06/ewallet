package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.AccountDto;
import com.yaser.ewallet.dto.convertar.AccountConverter;
import com.yaser.ewallet.exception.AccountCreationException;
import com.yaser.ewallet.model.Account;
import com.yaser.ewallet.repository.AccountRepository;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountServiceTest {

    private AccountService accountService;
    private AccountRepository accountRepository;
    private AccountConverter accountConverter;

    @BeforeAll
    public static void setEnviroment() {
        System.setProperty("jasypt.encryptor.password", "my-secret-value");
    }

    @BeforeEach
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = mock(AccountService.class);
        accountConverter = mock(AccountConverter.class);
        accountService = new AccountService(accountRepository, accountConverter);
    }


    @Test
    public void createAccount_WhenAccountIsValid() throws AccountCreationException {
        Account account = getAccount();
        AccountDto expected = getAccountDto(account);
        Mockito.when(accountRepository.save(account)).thenReturn(account);
        Mockito.when(accountConverter.toDTO(account)).thenReturn(expected);
        AccountDto actual = accountService.createAccount(account);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createAccount_ThrowExceptionByRepositoryTest() {
        Account account = getAccount();
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

    private AccountDto getAccountDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(account.getEmail());
        accountDto.setPassword(account.getPassword());
        accountDto.setFirstName(account.getFirstName());
        accountDto.setLastName(account.getLastName());
        accountDto.setPhone(account.getPhone());
        return accountDto;
    }

}

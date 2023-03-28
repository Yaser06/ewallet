package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.Wallet;
import lombok.Data;

import java.util.List;

@Data
public class AccountDto {

    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private String password;

    private List<Wallet> wallets;
}

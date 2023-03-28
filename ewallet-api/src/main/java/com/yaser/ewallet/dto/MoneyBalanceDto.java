package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.Currency;
import com.yaser.ewallet.model.Wallet;
import lombok.Data;

@Data
public class MoneyBalanceDto {

    private Double amount;

    private Wallet wallet;

    private Currency currency;
}

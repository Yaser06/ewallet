package com.yaser.ewallet.dto.convertar;

import com.yaser.ewallet.dto.MoneyBalanceDto;
import com.yaser.ewallet.model.MoneyBalance;
import org.springframework.stereotype.Component;

@Component
public class MoneyBalanceConverter {

    public MoneyBalanceDto toDto(MoneyBalance moneyBalance) {
        MoneyBalanceDto dto = new MoneyBalanceDto();
        dto.setAmount(moneyBalance.getAmount());
        dto.setWallet(moneyBalance.getWallet());
        dto.setCurrency(moneyBalance.getCurrency());
        return dto;
    }

    public MoneyBalance toEntity(MoneyBalanceDto dto) {
        MoneyBalance moneyBalance = new MoneyBalance();
        moneyBalance.setAmount(dto.getAmount());
        moneyBalance.setCurrency(dto.getCurrency());
        return moneyBalance;
    }
}

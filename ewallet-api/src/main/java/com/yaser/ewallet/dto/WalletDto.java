package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.WalletType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class WalletDto {

    private Long id;

    private UUID walletPublicKey;

    private Long accountId;

    private WalletType walletType;

    private MoneyBalance moneyBalance;

    private Date createdDate;

    private String provider;
}

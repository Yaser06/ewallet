package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.TransactionType;
import lombok.Data;

@Data
public class WalletTransactionDto {

    private Double amount;

    private TransactionType transactionType;

    private String description;

    private String sourceWalletPublicKey;

    private String targetWalletPublicKey;
}

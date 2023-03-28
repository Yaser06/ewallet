package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.TransactionType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TransactionDto {

    private UUID transactionId;

    private Double transactionAmount;

    private Date createdDate;

    private TransactionType transactionType;

    private String description;

    private Long sourceWallet;

    private Long targetWallet;
}

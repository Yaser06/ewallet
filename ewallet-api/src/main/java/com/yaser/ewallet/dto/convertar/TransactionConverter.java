package com.yaser.ewallet.dto.convertar;

import com.yaser.ewallet.dto.TransactionDto;
import com.yaser.ewallet.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter {

    public TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setCreatedDate(transaction.getCreatedDate());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setDescription(transaction.getDescription());
        dto.setSourceWallet(transaction.getSourceWallet().getId());
        dto.setTargetWallet(transaction.getTargetWallet().getId());
        return dto;
    }
}

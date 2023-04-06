package com.yaser.ewallet.dto;

import com.yaser.ewallet.model.AnotherCardInformationType;
import com.yaser.ewallet.model.Wallet;
import lombok.Data;

import java.util.Date;

@Data
public class AnotherCardInformationDto {

    private Wallet wallet;

    private AnotherCardInformationType anotherCardInformationType;

    private String cardNumber;

    private Date createdDate;
}

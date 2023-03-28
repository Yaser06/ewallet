package com.yaser.ewallet.dto.convertar;

import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.model.AnotherCardInformation;
import org.springframework.stereotype.Component;

@Component
public class AnotherCardInformationConverter {

    public AnotherCardInformationDto toDto(AnotherCardInformation anotherCardInformation) {
        AnotherCardInformationDto dto = new AnotherCardInformationDto();
        dto.setAnotherCardInformationType(anotherCardInformation.getAnotherCardInformationType());
        dto.setCardNumber(anotherCardInformation.getCardNumber());
        dto.setCreatedDate(anotherCardInformation.getCreatedDate());
        dto.setWallet(anotherCardInformation.getWallet());
        return dto;
    }

    public AnotherCardInformation toEntity(AnotherCardInformationDto dto) {
        AnotherCardInformation anotherCardInformation = new AnotherCardInformation();
        anotherCardInformation.setAnotherCardInformationType(dto.getAnotherCardInformationType());
        anotherCardInformation.setCardNumber(dto.getCardNumber());
        anotherCardInformation.setCreatedDate(dto.getCreatedDate());
        return anotherCardInformation;
    }
}







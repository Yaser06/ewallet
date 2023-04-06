package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.dto.convertar.AnotherCardInformationConverter;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.AnotherCardInformation;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.AnotherCardInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnotherCardInformationService {

    private final AnotherCardInformationRepository informationRepository;
    private final WalletService walletService;
    private final AnotherCardInformationConverter anotherCardInformationConverter;

    public AnotherCardInformationDto createAnotherCardInformation(AnotherCardInformation information)
            throws WalletNotFoundException {

        AnotherCardInformation saveInformation;
        information.setCreatedDate(new Date(System.currentTimeMillis()));
        Optional<Wallet> wallet = walletService.findById(information.getWallet().getId());
        if (wallet.isPresent()) {
            saveInformation = informationRepository.save(information);
            saveInformation.setWallet(wallet.get());
            return anotherCardInformationConverter.toDto(saveInformation);
        } else {
            throw new WalletNotFoundException("Wallet Not Found");
        }
    }
}

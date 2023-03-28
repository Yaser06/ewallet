package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.convertar.AnotherCardInformationConverter;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.AnotherCardInformation;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.AnotherCardInformationRepository;
import com.yaser.ewallet.utils.REnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnotherCardInformationService {

    private final AnotherCardInformationRepository informationRepository;
    private final WalletService walletService;
    private final AnotherCardInformationConverter anotherCardInformationConverter;
    private static Logger logger = LoggerFactory.getLogger(AnotherCardInformationService.class);

    public ResponseEntity createAnotherCardInformation(AnotherCardInformation information)
            throws WalletNotFoundException {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        AnotherCardInformation saveInformation;
        try {
            information.setCreatedDate(new Date(System.currentTimeMillis()));
            Optional<Wallet> wallet = walletService.findById(information.getWallet().getId());
            if (wallet.isPresent()) {
                saveInformation = informationRepository.save(information);
                saveInformation.setWallet(wallet.get());
                hm.put(REnum.status, true);
                hm.put(REnum.result, anotherCardInformationConverter.toDto(saveInformation));
            } else {
                throw new WalletNotFoundException("Wallet Not Found");
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new WalletNotFoundException("Wallet not found exception.");
        }
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }
}

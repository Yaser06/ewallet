package com.yaser.ewallet.controller;

import com.yaser.ewallet.dto.AnotherCardInformationDto;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.AnotherCardInformation;
import com.yaser.ewallet.service.AnotherCardInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/anothercard")
public class AnotherCardInformationController {

    private final AnotherCardInformationService anotherCardInformationService;

    @PostMapping("/create")
    public ResponseEntity<AnotherCardInformationDto> createAnotherCard(@RequestBody AnotherCardInformation anotherCardInformation)
            throws WalletNotFoundException {
        return ResponseEntity.ok(anotherCardInformationService
                .createAnotherCardInformation(anotherCardInformation));
    }
}

package com.yaser.ewallet.controller;

import com.yaser.ewallet.exception.WalletCreationException;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.WalletRepository;
import com.yaser.ewallet.service.WalletService;
import com.yaser.ewallet.utils.REnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    @PostMapping("/create")
    public ResponseEntity createWallet(@RequestBody Wallet wallet) throws WalletCreationException {
        return walletService.createWallet(wallet);
    }

    @GetMapping("/list")
    public ResponseEntity getAllList() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, true);
        hm.put(REnum.result, walletRepository.findAll());
        List<Wallet> walletList = walletRepository.findAll();
        //System.out.println(hm);
        return new ResponseEntity(hm, HttpStatus.CREATED);
    }
}

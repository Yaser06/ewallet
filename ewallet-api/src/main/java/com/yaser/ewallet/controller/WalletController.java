package com.yaser.ewallet.controller;

import com.yaser.ewallet.dto.WalletDto;
import com.yaser.ewallet.exception.WalletCreationException;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.WalletRepository;
import com.yaser.ewallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    @PostMapping("/create")
    public ResponseEntity<WalletDto> createWallet(@RequestBody Wallet wallet) throws WalletCreationException {
        return ResponseEntity.ok(walletService.createWallet(wallet));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Wallet>> getAllList() {
        return ResponseEntity.ok(walletRepository.findAll());
    }
}

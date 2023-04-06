package com.yaser.ewallet.service;

import com.yaser.ewallet.dto.WalletDto;
import com.yaser.ewallet.dto.convertar.WalletConverter;
import com.yaser.ewallet.exception.WalletCreationException;
import com.yaser.ewallet.exception.WalletNotFoundException;
import com.yaser.ewallet.model.Wallet;
import com.yaser.ewallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletConverter walletConverter;
    private static Logger logger = LoggerFactory.getLogger(WalletService.class);

    public WalletDto createWallet(Wallet wallet) throws WalletCreationException {
        Wallet saveWallet;
        try {
            wallet.setWalletPublicKey(UUID.randomUUID());
            wallet.setCreatedDate(new Date(System.currentTimeMillis()));
            saveWallet = walletRepository.save(wallet);
            return walletConverter.toDTO(saveWallet);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new WalletCreationException("Error occurred while saving...");
        }
    }

    protected Wallet getWallet(UUID uuid) throws WalletNotFoundException {
        Wallet wallet = walletRepository.findByWalletPublicKey(uuid);
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet Not Found");
        }
        return wallet;
    }

    protected Optional<Wallet> findById(Long id) {
        return walletRepository.findById(id);
    }
}

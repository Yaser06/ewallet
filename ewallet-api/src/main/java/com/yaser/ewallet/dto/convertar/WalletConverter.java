package com.yaser.ewallet.dto.convertar;

import com.yaser.ewallet.dto.WalletDto;
import com.yaser.ewallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletConverter {

    public WalletDto toDTO(Wallet wallet) {
        WalletDto walletDto = new WalletDto();
        walletDto.setId(wallet.getId());
        walletDto.setWalletPublicKey(wallet.getWalletPublicKey());
        walletDto.setAccountId(wallet.getAccount().getId());
        walletDto.setWalletType(wallet.getWalletType());
        walletDto.setMoneyBalance(wallet.getMoneyBalance());
        walletDto.setCreatedDate(wallet.getCreatedDate());
        walletDto.setProvider(wallet.getProvider());
        return walletDto;
    }
}

package com.yaser.ewallet.repository;

import com.yaser.ewallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByWalletPublicKey(UUID uuid);

    Optional<Wallet> findById(Long id);
}
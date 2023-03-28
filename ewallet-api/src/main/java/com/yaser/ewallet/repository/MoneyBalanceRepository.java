package com.yaser.ewallet.repository;

import com.yaser.ewallet.model.MoneyBalance;
import com.yaser.ewallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyBalanceRepository extends JpaRepository<MoneyBalance, Long> {

    MoneyBalance findByWallet(Wallet wallet);

    @Override
    Optional<MoneyBalance> findById(Long aLong);
}
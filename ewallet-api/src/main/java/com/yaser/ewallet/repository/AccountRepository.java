package com.yaser.ewallet.repository;

import com.yaser.ewallet.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
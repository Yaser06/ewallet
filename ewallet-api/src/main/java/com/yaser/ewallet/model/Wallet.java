package com.yaser.ewallet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID walletPublicKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletType walletType;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "wallet")
    @JsonIgnoreProperties
    private MoneyBalance moneyBalance;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceWallet")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties
    private List<Transaction> sourceWalletTransactions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "targetWallet")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties
    private List<Transaction> targetWalletTransactions;

    private Date createdDate;

    private String provider;
}

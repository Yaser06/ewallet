package com.yaser.ewallet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID transactionId;

    private Double transactionAmount;

    private Date createdDate;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_wallet_id", nullable = false)
    private Wallet sourceWallet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_wallet_id", nullable = false)
    private Wallet targetWallet;
}

package com.yaser.ewallet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AnotherCardInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnotherCardInformationType anotherCardInformationType;

    private String cardNumber;

    private Date createdDate;
}

//Visual information can be obtained for the extra card.

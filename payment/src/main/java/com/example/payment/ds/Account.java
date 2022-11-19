package com.example.payment.ds;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double amount;
    private String accountNumber;


    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public Account(String name, double amount, String accountNumber, AccountType accountType) {
        this.name = name;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public Account() {
    }
}

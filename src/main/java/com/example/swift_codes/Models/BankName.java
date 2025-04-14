package com.example.swift_codes.Models;

import jakarta.persistence.*;

@Entity
public class BankName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bankNameID;

    @Column(nullable = false, unique = true)
    private String bankName;


    public long getBankNameID() {
        return bankNameID;
    }

    public void setBankNameID(long bankNameID) {
        this.bankNameID = bankNameID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}

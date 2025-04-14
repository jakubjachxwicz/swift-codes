package com.example.swift_codes.Models;

import jakarta.persistence.*;

@Entity
public class SwiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long swiftCodeID;

    @Column(nullable = false, length = 11)
    private String swiftCode;

    @Column(nullable = false, length = 10)
    private String codeType;

    @Column(nullable = false)
    private boolean isHeadquarters;

    @ManyToOne
    @JoinColumn(name = "bankNameID")
    private BankName bankName;

    @ManyToOne
    @JoinColumn(name = "addressID")
    private BankAddress bankAddress;

    @ManyToOne
    @JoinColumn(name = "countryID")
    private Country country;


    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public void setSwiftCodeID(long swiftCodeID) {
        this.swiftCodeID = swiftCodeID;
    }

    public void setHeadquarters(boolean headquarters) {
        this.isHeadquarters = headquarters;
    }

    public boolean getIsHeadquarters() {
        return isHeadquarters;
    }

    public String getCodeType() {
        return codeType;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public long getSwiftCodeID() {
        return swiftCodeID;
    }

    public BankName getBankName() {
        return bankName;
    }

    public void setBankName(BankName bankName) {
        this.bankName = bankName;
    }

    public BankAddress getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(BankAddress bankAddress) {
        this.bankAddress = bankAddress;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

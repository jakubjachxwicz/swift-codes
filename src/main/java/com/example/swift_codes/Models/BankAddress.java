package com.example.swift_codes.Models;

import jakarta.persistence.*;

@Entity
public class BankAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long addressID;

    @Column(nullable = true, unique = true)
    private String address;

    @Column(nullable = false)
    private String townName;


    public long getAddressID() {
        return addressID;
    }

    public void setAddressID(long addressID) {
        this.addressID = addressID;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

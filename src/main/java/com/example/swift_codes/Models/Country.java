package com.example.swift_codes.Models;

import jakarta.persistence.*;

@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long countryID;

    @Column(nullable = false, unique = true, length = 2)
    private String countryCode;

    @Column(nullable = false, unique = true, length = 64)
    private String countryName;

    @Column(nullable = false, length = 64)
    private String timeZone;


    public long getCountryID() {
        return countryID;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getTimeZone() {
        return timeZone;
    }


    public void setCountryID(long countryID) {
        this.countryID = countryID;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.*;
import com.example.swift_codes.Repos.*;

import java.util.regex.Pattern;

public class ControllerHelpers
{
    private final static Pattern countryCodePattern = Pattern.compile("^[A-Z]{2}$");
    private final static Pattern swiftCodePattern = Pattern.compile("^[A-Z0-9]{11}$");
    private final static Pattern codeTypePattern = Pattern.compile("^.{1,10}$");
    private final static Pattern bankNamePattern = Pattern.compile("^.{1,255}$");
    private final static Pattern bankAddressPattern = Pattern.compile("^.{0,255}$");
    private final static Pattern townNamePattern = Pattern.compile("^.{0,255}$");
    private final static Pattern countryNamePattern = Pattern.compile("^[A-Z ]{1,64}$");
    private final static Pattern timeZonePattern = Pattern.compile("^.{0,64}$");


    public static Country getOrCreateCountry(ICountryRepo repo, String code, String name, String timeZone)
    {
        return repo.findByCountryCode(code).orElseGet(() -> {
            Country country = new Country();
            country.setCountryCode(code);
            country.setCountryName(name);
            country.setTimeZone(timeZone);

            return repo.save(country);
        });
    }

    public static BankName getOrCreateBankName(IBankNameRepo repo, String name)
    {
        return repo.findByBankName(name).orElseGet(() -> {
            BankName bankName = new BankName();
            bankName.setBankName(name);

            return repo.save(bankName);
        });
    }

    public static BankAddress getOrCreateAddress(IBankAddressRepo repo, String address, String townName)
    {
        return repo.findByAddress(address).orElseGet(() -> {
            BankAddress bankAddress = new BankAddress();
            bankAddress.setAddress(address);
            bankAddress.setTownName(townName);

            return repo.save(bankAddress);
        });
    }

    public static boolean validateIpnutLine(String[] line)
    {
        if (!countryCodePattern.matcher(line[0]).matches())
            return false;
        if (!swiftCodePattern.matcher(line[1]).matches())
            return false;
        if (!codeTypePattern.matcher(line[2]).matches())
            return false;
        if (!bankNamePattern.matcher(line[3]).matches())
            return false;
        if (!bankAddressPattern.matcher(line[4]).matches())
            return false;
        if (!townNamePattern.matcher(line[5]).matches())
            return false;
        if (!countryNamePattern.matcher(line[6]).matches())
            return false;
        if (!timeZonePattern.matcher(line[7]).matches())
            return false;

        return true;
    }

    public static boolean validateNewRecord(String address, String bankName, String ISO2, String countryName, String swiftCode)
    {
        if (!bankAddressPattern.matcher(address).matches())
            return false;
        if (!bankNamePattern.matcher(bankName).matches())
            return false;
        if (!countryCodePattern.matcher(ISO2).matches())
            return false;
        if (!countryNamePattern.matcher(countryName).matches())
            return false;
        if (!swiftCodePattern.matcher(swiftCode).matches())
            return false;

        return true;
    }
}

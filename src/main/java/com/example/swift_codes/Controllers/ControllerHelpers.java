package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.*;
import com.example.swift_codes.Repos.*;

public class ControllerHelpers
{
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
}

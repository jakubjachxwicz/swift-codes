package com.example.swift_codes.Controllers;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Repos.IBankAddressRepo;
import com.example.swift_codes.Repos.IBankNameRepo;
import com.example.swift_codes.Repos.ICountryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@ExtendWith(MockitoExtension.class)
public class ControllerHelpersTests
{
    @Mock
    private ICountryRepo countryRepo;
    @Mock
    private IBankAddressRepo bankAddressRepo;
    @Mock
    private IBankNameRepo bankNameRepo;


    @Test
    void shouldReturnFalseIfInputsAreInvalid()
    {
        String ISO2 = "PL";
        String swiftCode = "BCHICLR10R2";
        String codeType = "BIC11";
        String bankName = "NBANK";
        String address = "KWIECISTA 17";
        String townName = "WARSAW";
        String countryName = "POLAND";
        String timeZone = "Europe/Warsaw";

        boolean result1 = ControllerHelpers.validateIpnutLine(new String[] {
                "PLL", swiftCode, codeType, bankName, address, townName, countryName, timeZone
        });
        boolean result2 = ControllerHelpers.validateNewRecord(
                address, bankName, "PLL", countryName, swiftCode
        );
        boolean result3 = ControllerHelpers.validateIpnutLine(new String[] {
                ISO2, "XXXXXXXXXXXXXX", codeType, bankName, address, townName, countryName, timeZone
        });
        boolean result4 = ControllerHelpers.validateNewRecord(
                address, bankName, ISO2, countryName, "XXXXXXXXXXXXXX"
        );
        boolean result5 = ControllerHelpers.validateIpnutLine(new String[] {
                ISO2, swiftCode, codeType, bankName, address, townName, "12345", timeZone
        });
        boolean result6 = ControllerHelpers.validateNewRecord(
                address, bankName, ISO2, "12345", swiftCode
        );

        assertTrue("Data validation", !result1);
        assertTrue("Data validation", !result2);
        assertTrue("Data validation", !result3);
        assertTrue("Data validation", !result4);
        assertTrue("Data validation", !result5);
        assertTrue("Data validation", !result6);
    }

    @Test
    void shouldReturnTrueIfInputsAreValid()
    {
        String ISO2 = "PL";
        String swiftCode = "BCHICLR10R2";
        String codeType = "BIC11";
        String bankName = "NBANK";
        String address = "KWIECISTA 17";
        String townName = "WARSAW";
        String countryName = "POLAND";
        String timeZone = "Europe/Warsaw";

        boolean result1 = ControllerHelpers.validateIpnutLine(new String[] {
                ISO2, swiftCode, codeType, bankName, address, townName, countryName, timeZone
        });
        boolean result2 = ControllerHelpers.validateNewRecord(
                address, bankName, ISO2, countryName, swiftCode
        );

        assertTrue("Input line validation", result1);
        assertTrue("New record validation", result2);
    }

    @Test
    void shouldReturnCountryIfExists()
    {
        Country country = new Country();
        country.setCountryCode("PL");
        country.setCountryName("POLAND");

        when(countryRepo.findByCountryCode("PL")).thenReturn(Optional.of(country));

        Country result = ControllerHelpers.getOrCreateCountry(countryRepo, "PL", "POLAND", "");

        assertEquals("Objects match", country.getCountryCode(), result.getCountryCode());
        assertEquals("Objects match", country.getCountryName(), result.getCountryName());
        assertEquals("Objects match", country.getTimeZone(), result.getTimeZone());
    }

    @Test
    void shouldCreateNewCountryIfNotExists()
    {
        Country country = new Country();
        country.setCountryCode("PL");
        country.setCountryName("POLAND");
        country.setTimeZone("Europe/Warsaw");

        when(countryRepo.findByCountryCode("PL")).thenReturn(Optional.empty());
        when(countryRepo.save(any(Country.class))).thenReturn(country);

        Country result = ControllerHelpers.getOrCreateCountry(countryRepo, "PL", "POLAND", "");

        assertEquals("Objects match", country.getCountryCode(), result.getCountryCode());
        assertEquals("Objects match", country.getCountryName(), result.getCountryName());
        assertEquals("Objects match", country.getTimeZone(), result.getTimeZone());
    }

    @Test
    void shouldReturnBankNameIfExists()
    {
        BankName bankName = new BankName();
        bankName.setBankName("NBANK");

        when(bankNameRepo.findByBankName("NBANK")).thenReturn(Optional.of(bankName));

        BankName result = ControllerHelpers.getOrCreateBankName(bankNameRepo, "NBANK");

        assertEquals("Objects match", bankName.getBankName(), result.getBankName());
    }

    @Test
    void shouldCreateNewBankNameIfNotExists()
    {
        BankName bankName = new BankName();
        bankName.setBankName("NBANK");

        when(bankNameRepo.findByBankName("NBANK")).thenReturn(Optional.empty());
        when(bankNameRepo.save(any(BankName.class))).thenReturn(bankName);

        BankName result = ControllerHelpers.getOrCreateBankName(bankNameRepo, "NBANK");

        assertEquals("Objects match", bankName.getBankName(), result.getBankName());
    }

    @Test
    void shouldReturnBankAddressIfExists()
    {
        BankAddress bankAddress = new BankAddress();
        bankAddress.setTownName("CRACOW");
        bankAddress.setAddress("JOZEFA 14");

        when(bankAddressRepo.findByAddress("JOZEFA 14")).thenReturn(Optional.of(bankAddress));

        BankAddress result = ControllerHelpers.getOrCreateAddress(bankAddressRepo, "JOZEFA 14", "");

        assertEquals("Objects match", bankAddress.getAddress(), result.getAddress());
        assertEquals("Objects match", bankAddress.getTownName(), result.getTownName());
    }

    @Test
    void shouldCreateNewBankAddressIfNotExists()
    {
        BankAddress bankAddress = new BankAddress();
        bankAddress.setTownName("CRACOW");
        bankAddress.setAddress("JOZEFA 14");

        when(bankAddressRepo.findByAddress("JOZEFA 14")).thenReturn(Optional.empty());
        when(bankAddressRepo.save(any(BankAddress.class))).thenReturn(bankAddress);

        BankAddress result = ControllerHelpers.getOrCreateAddress(bankAddressRepo, "JOZEFA 14", "");

        assertEquals("Objects match", bankAddress.getAddress(), result.getAddress());
        assertEquals("Objects match", bankAddress.getTownName(), result.getTownName());
    }
}

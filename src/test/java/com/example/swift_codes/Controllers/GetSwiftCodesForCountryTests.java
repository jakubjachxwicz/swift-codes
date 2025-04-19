package com.example.swift_codes.Controllers;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.ICountryRepo;
import com.example.swift_codes.Repos.ISwiftCodeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class GetSwiftCodesForCountryTests
{
    @InjectMocks
    private ApiController apiController;

    @Mock
    private ICountryRepo countryRepo;

    @Mock
    private ISwiftCodeRepo swiftCodeRepo;


    @Test
    void shouldReturnBadRequestWhenEmptyISO2()
    {
        String iso2 = "   ";

        ResponseEntity<String> response = apiController.getSwiftCodesForCountry(iso2);

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"countryISO2 is empty\"}", response.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenCountryNotExists()
    {
        String iso2 = "PL";
        when(countryRepo.findByCountryCode(iso2)).thenReturn(Optional.empty());

        ResponseEntity<String> response = apiController.getSwiftCodesForCountry(iso2);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"Country not found\"}", response.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenSwiftCodeAssociatedInfoMissing()
    {
        String iso2 = "PL";

        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setBankAddress(null);
        swiftCode.setBankName(null);

        Country country = new Country();

        when(swiftCodeRepo.getByCountryCodes(iso2)).thenReturn(List.of(swiftCode));
        when(countryRepo.findByCountryCode(iso2)).thenReturn(Optional.of(country));

        ResponseEntity<String> response = apiController.getSwiftCodesForCountry(iso2);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"Missing associated SWIFT code data\"}", response.getBody());
    }

    @Test
    void shouldReturnSwiftCodesWhenTheyExist()
    {
        String iso2 = "PL";

        Country country = new Country();
        country.setCountryCode("PL");
        country.setCountryName("POLAND");

        BankAddress bankAddress = new BankAddress();
        bankAddress.setAddress("TOPOLOWA 12");
        BankName bankName = new BankName();
        bankName.setBankName("NBANK");

        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setBankAddress(bankAddress);
        swiftCode1.setBankName(bankName);
        swiftCode1.setCountry(country);
        swiftCode1.setSwiftCode("SWIFTCODE1");
        swiftCode1.setHeadquarters(false);

        SwiftCode swiftCode2 = new SwiftCode();
        swiftCode2.setBankAddress(bankAddress);
        swiftCode2.setBankName(bankName);
        swiftCode2.setCountry(country);
        swiftCode2.setSwiftCode("SWIFTCODE2");
        swiftCode2.setHeadquarters(true);

        when(countryRepo.findByCountryCode(iso2)).thenReturn(Optional.of(country));
        when(swiftCodeRepo.getByCountryCodes(iso2)).thenReturn(List.of(swiftCode1, swiftCode2));

        ResponseEntity<String> response = apiController.getSwiftCodesForCountry(iso2);

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Response message", response.getBody().contains("SWIFTCODE1"));
        assertTrue("Response message", response.getBody().contains("SWIFTCODE2"));
        assertTrue("Response message", response.getBody().contains("PL"));
        assertTrue("Response message", response.getBody().contains(iso2));
        assertTrue("Response message", response.getBody().contains("NBANK"));
        assertTrue("Response message", response.getBody().contains("swiftCodes"));
        assertTrue("Response message", response.getBody().contains("POLAND"));

    }
}

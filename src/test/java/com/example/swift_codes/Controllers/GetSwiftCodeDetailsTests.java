package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GetSwiftCodeDetailsTests
{
    @InjectMocks
    private ApiController apiController;

    @Mock
    private ISwiftCodeRepo swiftCodeRepo;

    @BeforeEach
    void cleanDb()
    {
        swiftCodeRepo.deleteAll();
    }


    @Test
    void shouldReturnBadRequestWhenCodeIsEmpty()
    {
        ResponseEntity<String> response = apiController.getSwiftCodeDetails("   ");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"SWIFT code is empty\"}", response.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenSwiftCodeNotExists()
    {
        String code = "ABCD";
        when(swiftCodeRepo.findBySwiftCode(code)).thenReturn(Optional.empty());

        ResponseEntity<String> response = apiController.getSwiftCodeDetails(code);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"SWIFT code not found\"}", response.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenAssociatedDataIsMissing()
    {
        String code = "ABCD";
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(code);
        swiftCode.setCountry(null);
        swiftCode.setBankAddress(null);
        swiftCode.setBankName(null);

        when(swiftCodeRepo.findBySwiftCode(code)).thenReturn(Optional.of(swiftCode));

        ResponseEntity<String> response = apiController.getSwiftCodeDetails(code);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"Missing associated SWIFT code data\"}", response.getBody());
    }

    @Test
    void shouldReturnBranchDataWhenIsNotHeadquarter()
    {
        String code = "ABCD";
        SwiftCode swiftCode = getCode(code, false);

        when(swiftCodeRepo.findBySwiftCode(code)).thenReturn(Optional.of(swiftCode));

        ResponseEntity<String> response = apiController.getSwiftCodeDetails(code);

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Response message", response.getBody().contains("TOPOLOWA 2"));
        assertTrue("Response message", response.getBody().contains("ABCD"));
        assertTrue("Response message", response.getBody().contains("countryISO2"));
    }

    @Test
    void shouldReturnHeadquarterDataWhenIsHeadquarter()
    {
        String code = "ABCD";
        SwiftCode swiftCode = getCode(code, true);
        SwiftCode branch = getCode("XYZ", false);

        when(swiftCodeRepo.findBySwiftCode(code)).thenReturn(Optional.of(swiftCode));
        when(swiftCodeRepo.getBankBranches(code)).thenReturn(List.of(branch));

        ResponseEntity<String> response = apiController.getSwiftCodeDetails(code);

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertTrue("Response message", response.getBody().contains("TOPOLOWA 2"));
        assertTrue("Response message", response.getBody().contains("ABCD"));
        assertTrue("Response message", response.getBody().contains("countryISO2"));
    }

    @Test
    void shouldReturnNotFoundWhenBranchAssociatedDataIsMissing()
    {
        String code = "ABCD";
        SwiftCode swiftCode = getCode(code, true);
        SwiftCode branch = new SwiftCode();

        branch.setBankName(null);
        branch.setBankAddress(null);
        branch.setCountry(null);

        when(swiftCodeRepo.findBySwiftCode(code)).thenReturn(Optional.of(swiftCode));
        when(swiftCodeRepo.getBankBranches(code)).thenReturn(List.of(branch));

        ResponseEntity<String> response = apiController.getSwiftCodeDetails(code);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "{ \"error\": \"Missing associated SWIFT code data\"}", response.getBody());
    }

    private static SwiftCode getCode(String code, boolean headquarters) {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(code);
        swiftCode.setHeadquarters(headquarters);
        swiftCode.setCodeType("BIC11");

        Country country = new Country();
        country.setCountryCode("PL");
        country.setCountryName("POLAND");
        country.setTimeZone("Europe/Warsaw");

        BankAddress bankAddress = new BankAddress();
        bankAddress.setAddress("TOPOLOWA 2");
        bankAddress.setTownName("CRACOW");

        BankName bankName = new BankName();
        bankName.setBankName("BankM");

        swiftCode.setCountry(country);
        swiftCode.setBankAddress(bankAddress);
        swiftCode.setBankName(bankName);

        return swiftCode;
    }
}

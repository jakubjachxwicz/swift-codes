package com.example.swift_codes.Controllers;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.ICountryRepo;
import com.example.swift_codes.Repos.ISwiftCodeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AddNewCodeTests
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
    void shouldReturnBadRequestWhenRequestBodyIsMissing()
    {
        ResponseEntity<Map<String, String>> response = apiController.addNewCode(null);

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "Request body is missing", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequestWhenAnyFieldIsMissing()
    {
        Map<String, Object> requestBody1 = new HashMap<>();
        requestBody1.put("address", "TOPOLOWA 12");
        requestBody1.put("countryISO2", "PL");
        requestBody1.put("countryName", "POLAND");
        requestBody1.put("isHeadquarter", true);
        requestBody1.put("swiftCode", "ABCDEFGHIJK");

        Map<String, Object> requestBody2 = new HashMap<>();
        requestBody2.put("address", "TOPOLOWA 12");
        requestBody2.put("bankName", "NBANK");
        requestBody2.put("countryName", "POLAND");
        requestBody2.put("isHeadquarter", true);
        requestBody2.put("swiftCode", "ABCDEFGHIJK");

        Map<String, Object> requestBody3 = new HashMap<>();
        requestBody3.put("address", "TOPOLOWA 12");
        requestBody3.put("countryISO2", "PL");
        requestBody3.put("countryName", "POLAND");
        requestBody3.put("isHeadquarter", true);
        requestBody3.put("bankName", "NBANK");

        ResponseEntity<Map<String, String>> response1 = apiController.addNewCode(requestBody1);
        ResponseEntity<Map<String, String>> response2 = apiController.addNewCode(requestBody2);
        ResponseEntity<Map<String, String>> response3 = apiController.addNewCode(requestBody3);

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals("Status code", HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals("Status code", HttpStatus.BAD_REQUEST, response3.getStatusCode());

        assertEquals("Response message", "bankName field is missing", response1.getBody().get("error"));
        assertEquals("Response message", "countryISO2 field is missing", response2.getBody().get("error"));
        assertEquals("Response message", "swiftCode field is missing", response3.getBody().get("error"));
    }

    @Test
    void shouldReturnStatusOkWhenRequestValid()
    {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("address", "TOPOLOWA 12");
        requestBody.put("countryISO2", "PL");
        requestBody.put("countryName", "POLAND");
        requestBody.put("isHeadquarter", true);
        requestBody.put("swiftCode", "ALBPPLP1BMW");
        requestBody.put("bankName", "NBANK");

        BankName bankName = new BankName();
        bankName.setBankName("NBANK");

        BankAddress bankAddress = new BankAddress();
        bankAddress.setAddress("TOPOLOWA 12");
        bankAddress.setTownName("CRACOW");

        Country country = new Country();
        country.setCountryName("POLAND");
        country.setCountryCode("PL");

        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGHIJK");
        swiftCode.setCountry(country);
        swiftCode.setBankAddress(bankAddress);
        swiftCode.setBankName(bankName);
        swiftCode.setCountry(country);
        swiftCode.setCodeType("BIC11");

        MockedStatic<ControllerHelpers> mockedStatic = Mockito.mockStatic(ControllerHelpers.class);
        mockedStatic.when(() -> ControllerHelpers.getOrCreateAddress(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(bankAddress);
        mockedStatic.when(() -> ControllerHelpers.getOrCreateBankName(Mockito.any(), Mockito.anyString()))
                .thenReturn(bankName);
        mockedStatic.when(() -> ControllerHelpers.getOrCreateCountry(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(country);
        mockedStatic.when(() -> ControllerHelpers.validateNewRecord(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(true);

        Mockito.when(swiftCodeRepo.save(Mockito.any(SwiftCode.class))).thenReturn(swiftCode);

        ResponseEntity<Map<String, String>> response = apiController.addNewCode(requestBody);

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Response message", "Swift code added to the database", response.getBody().get("message"));
    }
}

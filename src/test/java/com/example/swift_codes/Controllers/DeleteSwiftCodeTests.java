package com.example.swift_codes.Controllers;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.ISwiftCodeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DeleteSwiftCodeTests
{
    @InjectMocks
    private ApiController apiController;

    @Mock
    private ISwiftCodeRepo swiftCodeRepo;


    @Test
    void shouldReturnBadRequestWhenCode()
    {
        ResponseEntity<Map<String, String>> response = apiController.deleteSwiftCode("   ");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "Invalid swift code", response.getBody().get("error"));
    }

    @Test
    void shouldReturnNotFoundWhenCodeNotExists()
    {
        String code = "XXXXXXXXXXX";
        when(swiftCodeRepo.existsBySwiftCode(code)).thenReturn(false);

        ResponseEntity<Map<String, String>> response = apiController.deleteSwiftCode(code);

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Response message", "Swift code not found", response.getBody().get("error"));
    }

    @Test
    void shouldReturnOkWhenSwiftCodeDeleted()
    {
        String code = "XXXXXXXXXXX";
        when(swiftCodeRepo.existsBySwiftCode(code)).thenReturn(true);

        ResponseEntity<Map<String, String>> response = apiController.deleteSwiftCode(code);

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Response message", "Swift code deleted from the database", response.getBody().get("message"));
    }
}

package com.example.swift_codes.Controllers;

import com.example.swift_codes.Repos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LoadDataControllerTests
{
    @InjectMocks
    private LoadDataController loadDataController;

    @Mock
    private ISwiftCodeRepo swiftCodeRepo;
    @Mock
    private ICountryRepo countryRepo;
    @Mock
    private IBankNameRepo bankNameRepo;
    @Mock
    private IBankAddressRepo bankCodeRepo;


    @Test
    void shouldReturnBadRequestWhenEmptyFilename()
    {
        ResponseEntity<Map<String, String>> response = loadDataController.parseData("   ");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "Filename cannot be empty", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPath()
    {
        ResponseEntity<Map<String, String>> response1 = loadDataController.parseData("../file");
        ResponseEntity<Map<String, String>> response2 = loadDataController.parseData("\\file");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals("Response message", "Filepath is invalid", response1.getBody().get("error"));
        assertEquals("Status code", HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals("Response message", "Filepath is invalid", response2.getBody().get("error"));
    }

    @Test
    void shouldReturnNotFoundWhenNoFileExists()
    {
        ResponseEntity<Map<String, String>> response = loadDataController.parseData("thisfiledoesnotexists");

        assertEquals("Status code", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestWhenWrongColumnNumber()
    {
        ResponseEntity<Map<String, String>> response = loadDataController.parseData("invalid1.csv");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "Problem with a structure of a file in line 1", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidFieldsInFile()
    {
        ResponseEntity<Map<String, String>> response = loadDataController.parseData("invalid2.csv");

        assertEquals("Status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Response message", "Problem during validating data in line 1", response.getBody().get("error"));
    }

    @Test
    void shouldReturnOkWhenFileIsCorrect()
    {
        ResponseEntity<Map<String, String>> response = loadDataController.parseData("validtestfile.csv");

        assertEquals("Status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Response message", "Data successfully added to the database", response.getBody().get("message"));
    }
}

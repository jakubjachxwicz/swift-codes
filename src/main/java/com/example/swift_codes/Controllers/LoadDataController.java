package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.*;
import com.example.swift_codes.Repos.IBankAddressRepo;
import com.example.swift_codes.Repos.IBankNameRepo;
import com.example.swift_codes.Repos.ICountryRepo;
import com.example.swift_codes.Repos.ISwiftCodeRepo;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoadDataController
{
    private final ICountryRepo countryRepo;
    private final ISwiftCodeRepo swiftCodeRepo;
    private final IBankNameRepo bankNameRepo;
    private final IBankAddressRepo bankAddressRepo;

    public LoadDataController(ICountryRepo countryRepo,
                              ISwiftCodeRepo swiftCodeRepo,
                              IBankNameRepo bankNameRepo,
                              IBankAddressRepo bankAddressRepo)
    {
        this.countryRepo = countryRepo;
        this.swiftCodeRepo = swiftCodeRepo;
        this.bankNameRepo = bankNameRepo;
        this.bankAddressRepo = bankAddressRepo;
    }


    @PostMapping(value = "/admin/parse-data")
    public ResponseEntity<Map<String, String>> parseData(@RequestParam String filename)
    {
        if (filename == null || filename.trim().isEmpty())
        {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Filename cannot be empty"));
        }

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\"))
        {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Filepath is invalid"));
        }

        swiftCodeRepo.deleteAll();
        countryRepo.deleteAll();
        bankAddressRepo.deleteAll();
        bankNameRepo.deleteAll();

        CSVReader csvReader = null;
        String[] record;
        int line = 0;

        try
        {
            ClassPathResource resource = new ClassPathResource(filename);

            if (!resource.exists())
            {
                return ResponseEntity.notFound().build();
            }

            csvReader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream()))
                    .withCSVParser(new CSVParserBuilder().build())
                    .withSkipLines(1)
                    .build();

            while ((record = csvReader.readNext()) != null)
            {
                line++;

                if (record.length != 8)
                {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Problem with a structure of a file in line " + line));
                }

                if (!ControllerHelpers.validateIpnutLine(record))
                {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Problem during validating data in line " + line));
                }

                String countryCode = record[0];
                String swiftCode = record[1];
                String codeType = record[2];
                String name = record[3];
                String address = record[4];
                String townName = record[5];
                String countryName = record[6];
                String timeZone = record[7];

                Country country = ControllerHelpers.getOrCreateCountry(countryRepo, countryCode, countryName, timeZone);
                BankName bankName = ControllerHelpers.getOrCreateBankName(bankNameRepo, name);
                BankAddress bankAddress = ControllerHelpers.getOrCreateAddress(bankAddressRepo, address, townName);

                SwiftCode swiftCodeRecord = new SwiftCode();
                swiftCodeRecord.setSwiftCode(swiftCode);
                swiftCodeRecord.setCodeType(codeType);
                swiftCodeRecord.setCountry(country);
                swiftCodeRecord.setBankName(bankName);
                swiftCodeRecord.setBankAddress(bankAddress);
                swiftCodeRecord.setHeadquarters(swiftCode.endsWith("XXX"));

                swiftCodeRepo.save(swiftCodeRecord);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", "Unexpected error: " + e.getMessage()));
        }
        finally {
            if (csvReader != null)
            {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    System.out.println("Error closing CSV reader: " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Data successfully added to the database"));
    }
}

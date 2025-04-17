package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ApiController
{
    private final ICountryRepo countryRepo;
    private final ISwiftCodeRepo swiftCodeRepo;
    private final IBankNameRepo bankNameRepo;
    private final IBankAddressRepo bankAddressRepo;

    public ApiController(ICountryRepo countryRepo,
                              ISwiftCodeRepo swiftCodeRepo,
                              IBankNameRepo bankNameRepo,
                              IBankAddressRepo bankAddressRepo)
    {
        this.countryRepo = countryRepo;
        this.swiftCodeRepo = swiftCodeRepo;
        this.bankNameRepo = bankNameRepo;
        this.bankAddressRepo = bankAddressRepo;
    }


    @GetMapping(value = "/v1/swift-codes/{swift-code}")
    public ResponseEntity<String> getSwiftCodeDetails(@PathVariable("swift-code") String code)
    {
        try
        {
            if (code == null || code.trim().isEmpty())
            {
                return ResponseEntity.badRequest().body("{ \"error\": \"SWIFT code is empty\"}");
            }

            SwiftCode swiftCodeRecord = swiftCodeRepo.findBySwiftCode(code)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "{ \"error\": \"SWIFT code not found\"}"));

            if (swiftCodeRecord.getBankName() == null
                    || swiftCodeRecord.getBankAddress() == null
                    || swiftCodeRecord.getCountry() == null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"Missing associated SWIFT code data\"}");
            }

            ObjectNode response = JsonNodeFactory.instance.objectNode()
                    .put("address", swiftCodeRecord.getBankAddress().getAddress())
                    .put("bankName", swiftCodeRecord.getBankName().getBankName())
                    .put("countryISO2", swiftCodeRecord.getCountry().getCountryCode())
                    .put("countryName", swiftCodeRecord.getCountry().getCountryName())
                    .put("isHeadquarter", swiftCodeRecord.getIsHeadquarters())
                    .put("swiftCode", swiftCodeRecord.getSwiftCode());

            if (swiftCodeRecord.getIsHeadquarters())
            {
                ArrayNode branchesArray = response.putArray("branches");

                List<SwiftCode> bankBranches = swiftCodeRepo.getBankBranches(code);
                for (var bankBranch : bankBranches)
                {
                    if (bankBranch.getBankName() == null
                            || bankBranch.getBankAddress() == null
                            || bankBranch.getCountry() == null)
                    {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"Missing associated SWIFT code data\"}");
                    }

                    branchesArray.addObject()
                            .put("address", bankBranch.getBankAddress().getAddress())
                            .put("bankName", bankBranch.getBankName().getBankName())
                            .put("countryISO2", bankBranch.getCountry().getCountryCode())
                            .put("isHeadquarter", bankBranch.getIsHeadquarters())
                            .put("swiftCode", bankBranch.getSwiftCode());
                }
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"error\": \""
                    + "Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping(value = "/v1/swift-codes/country/{countryISO2code}")
    public ResponseEntity<String> getSwiftCodesForCountry(@PathVariable("countryISO2code") String countryISO2)
    {
        try
        {
            if (countryISO2 == null || countryISO2.trim().isEmpty())
            {
                return ResponseEntity.badRequest().body("{ \"error\": \"countryISO2 is empty\"}");
            }

            Country countryRecord = countryRepo.findByCountryCode(countryISO2)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "{ \"error\": \"SWIFT code not found\"}"));

            ObjectNode response = JsonNodeFactory.instance.objectNode()
                    .put("countryISO2", countryISO2)
                    .put("countryName", countryRecord.getCountryName());

            ArrayNode swiftCodesArray = response.putArray("swiftCodes");

            List<SwiftCode> swiftCodes = swiftCodeRepo.getByCountryCodes(countryISO2);
            for (var swiftCode : swiftCodes)
            {
                if (swiftCode.getBankName() == null
                        || swiftCode.getBankAddress() == null
                        || swiftCode.getCountry() == null)
                {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{ \"error\": \"Missing associated SWIFT code data\"}");
                }

                swiftCodesArray.addObject()
                        .put("address", swiftCode.getBankAddress().getAddress())
                        .put("bankName", swiftCode.getBankName().getBankName())
                        .put("countryISO2", swiftCode.getCountry().getCountryCode())
                        .put("isHeadquarter", swiftCode.getIsHeadquarters())
                        .put("swiftCode", swiftCode.getSwiftCode());
            }

            return ResponseEntity.ok(response.toString());
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{ \"error\": \""
                    + "Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/v1/swift-codes")
    public ResponseEntity<Map<String, String>> addNewCode(@RequestBody Map<String, Object> requestBody)
    {
        if (requestBody == null)
        {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Request body is missing"));
        }

        try
        {
            String address = Optional.ofNullable((String)requestBody.get("address"))
                    .orElseThrow(() -> new InvalidFieldException("address field is missing"));
            String bankName = Optional.ofNullable((String)requestBody.get("bankName"))
                    .orElseThrow(() -> new InvalidFieldException("bankName field is missing"));
            String countryISO2 = Optional.ofNullable((String)requestBody.get("countryISO2"))
                    .orElseThrow(() -> new InvalidFieldException("countryISO2 field is missing"));
            String countryName = Optional.ofNullable((String)requestBody.get("countryName"))
                    .orElseThrow(() -> new InvalidFieldException("countryName field is missing"));
            boolean isHeadquarter = Optional.ofNullable((boolean)requestBody.get("isHeadquarter"))
                    .orElseThrow(() -> new InvalidFieldException("isHeadquarter field is missing"));
            String swiftCode = Optional.ofNullable((String)requestBody.get("swiftCode"))
                    .orElseThrow(() -> new InvalidFieldException("swiftCode field is missing"));

            if (!ControllerHelpers.validateNewRecord(
                    address,
                    bankName,
                    countryISO2,
                    countryName,
                    swiftCode))
            {
                throw new InvalidFieldException("invalid value");
            }

            BankAddress bankAddress = ControllerHelpers.getOrCreateAddress(bankAddressRepo, address, "");
            BankName bName = ControllerHelpers.getOrCreateBankName(bankNameRepo, bankName);
            Country country = ControllerHelpers.getOrCreateCountry(countryRepo, countryISO2, countryName, "");

            SwiftCode swiftCodeRecord = new SwiftCode();
            swiftCodeRecord.setSwiftCode(swiftCode);
            swiftCodeRecord.setCountry(country);
            swiftCodeRecord.setHeadquarters(isHeadquarter);
            swiftCodeRecord.setBankAddress(bankAddress);
            swiftCodeRecord.setBankName(bName);
            swiftCodeRecord.setCodeType("BIC11");

            swiftCodeRepo.save(swiftCodeRecord);

            return ResponseEntity.ok(Collections.singletonMap("message", "Swift code added to the database"));
        } catch (InvalidFieldException e)
        {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Unexpected error: " + e.getMessage()));
        }
    }

    @DeleteMapping(value = "/v1/swift-codes/{swift-code}")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSwiftCode(@PathVariable("swift-code") String code)
    {
        if (code == null || code.trim().isEmpty())
        {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid swift code"));
        }

        try
        {
            if (!swiftCodeRepo.existsBySwiftCode(code))
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Swift code not found"));
            }

            swiftCodeRepo.deleteBySwiftCode(code);

            swiftCodeRepo.deleteOrphanedAddresses();
            swiftCodeRepo.deleteOrphanedNames();
            swiftCodeRepo.deleteOrphanedCountries();


            return ResponseEntity.ok(Collections.singletonMap("message", "Swift code deleted from the database"));
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Unexpected error: " + e.getMessage()));
        }
    }

    class InvalidFieldException extends Exception
    {
        public InvalidFieldException(String message)
        {
            super(message);
        }
    }
}

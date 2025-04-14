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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        SwiftCode swiftCodeRecord = swiftCodeRepo.findBySwiftCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SWIFT code not found"));

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
            bankBranches.forEach(b -> branchesArray.addObject()
                    .put("address", b.getBankAddress().getAddress())
                    .put("bankName", b.getBankName().getBankName())
                    .put("countryISO2", b.getCountry().getCountryCode())
                    .put("isHeadquarter", b.getIsHeadquarters())
                    .put("swiftCode", b.getSwiftCode()
            ));
        }

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping(value = "/v1/swift-codes/country/{countryISO2code}")
    public ResponseEntity<String> getSwiftCodesForCountry(@PathVariable("countryISO2code") String countryISO2)
    {
        Country countryRecord = countryRepo.findByCountryCode(countryISO2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SWIFT code not found"));

        ObjectNode response = JsonNodeFactory.instance.objectNode()
                .put("countryISO2", countryISO2)
                .put("countryName", countryRecord.getCountryName());

        ArrayNode swiftCodesArray = response.putArray("swiftCodes");

        List<SwiftCode> swiftCodes = swiftCodeRepo.getByCountryCodes(countryISO2);
        swiftCodes.forEach(s -> swiftCodesArray.addObject()
                .put("address", s.getBankAddress().getAddress())
                .put("bankName", s.getBankName().getBankName())
                .put("countryISO2", s.getCountry().getCountryCode())
                .put("isHeadquarter", s.getIsHeadquarters())
                .put("swiftCode", s.getSwiftCode())
        );

        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(value = "/v1/swift-codes")
    public ResponseEntity<Map<String, String>> addNewCode(@RequestBody Map<String, Object> requestBody)
    {
        String address = (String)requestBody.get("address");
        String bankName = (String)requestBody.get("bankName");
        String countryISO2 = (String)requestBody.get("countryISO2");
        String countryName = (String)requestBody.get("countryName");
        boolean isHeadquarter = (boolean)requestBody.get("isHeadquarter");
        String swiftCode = (String)requestBody.get("swiftCode");

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
    }

    @DeleteMapping(value = "/v1/swift-codes/{swift-code}")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSwiftCode(@PathVariable("swift-code") String code)
    {
        swiftCodeRepo.deleteBySwiftCode(code);


        return ResponseEntity.ok(Collections.singletonMap("message", "Swift code deleted from the database"));
    }
}

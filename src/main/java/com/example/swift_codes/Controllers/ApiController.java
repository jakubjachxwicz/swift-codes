package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
}

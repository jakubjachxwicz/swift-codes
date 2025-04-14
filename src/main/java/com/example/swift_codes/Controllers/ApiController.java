package com.example.swift_codes.Controllers;

import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.SwiftCode;
import com.example.swift_codes.Repos.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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
}

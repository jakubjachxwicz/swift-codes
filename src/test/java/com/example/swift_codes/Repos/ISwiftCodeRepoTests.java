package com.example.swift_codes.Repos;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import com.example.swift_codes.Models.SwiftCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest
@EntityScan("com.example.swift_codes.Models")
@EnableJpaRepositories("com.example.swift_codes.Repos")
public class ISwiftCodeRepoTests
{
    @Autowired
    private ISwiftCodeRepo swiftCodeRepo;

    @Autowired
    private ICountryRepo countryRepo;

    @Autowired
    private IBankNameRepo bankNameRepo;

    @Autowired
    private IBankAddressRepo bankAddressRepo;


    @Test
    void shouldFindBySwiftCode()
    {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCodeType("BIC11");
        swiftCode.setSwiftCode("XXXXXXXXXXX");
        swiftCode.setHeadquarters(true);

        swiftCodeRepo.save(swiftCode);
        Optional<SwiftCode> found = swiftCodeRepo.findBySwiftCode("XXXXXXXXXXX");

        assertTrue("Record added", found.isPresent());
        assertEquals("Record correct", "XXXXXXXXXXX", found.get().getSwiftCode());
        assertEquals("Record correct", "BIC11", found.get().getCodeType());
        assertEquals("Record correct", true, found.get().getIsHeadquarters());
    }

    @Test
    void shouldDeleteAllSwiftCodes()
    {
        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setCodeType("BIC11");
        swiftCode1.setSwiftCode("XXXXXXXXXXX");
        swiftCode1.setHeadquarters(true);

        SwiftCode swiftCode2 = new SwiftCode();
        swiftCode2.setCodeType("BIC11");
        swiftCode2.setSwiftCode("YYYYYYYYYYY");
        swiftCode2.setHeadquarters(false);

        swiftCodeRepo.save(swiftCode1);
        swiftCodeRepo.save(swiftCode2);

        swiftCodeRepo.deleteAll();

        assertTrue("Records deleted", swiftCodeRepo.findAll().isEmpty());
    }

    @Test
    void shouldReturnIfSwiftCodeExists()
    {
        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setCodeType("BIC11");
        swiftCode1.setSwiftCode("XXXXXXXXXXX");
        swiftCode1.setHeadquarters(true);

        swiftCodeRepo.save(swiftCode1);

        boolean shouldBeTrue = swiftCodeRepo.existsBySwiftCode("XXXXXXXXXXX");
        boolean shouldBeFalse = swiftCodeRepo.existsBySwiftCode("YYYYYYYYXXXX");

        assertTrue("Code exists", shouldBeTrue);
        assertTrue("Code not exists", !shouldBeFalse);
    }

    @Test
    void shouldReturnBankBranches()
    {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCodeType("BIC11");
        swiftCode.setSwiftCode("XXXXXXXXXXX");
        swiftCode.setHeadquarters(true);

        SwiftCode branch1 = new SwiftCode();
        branch1.setCodeType("BIC11");
        branch1.setSwiftCode("XXXXXXXXYYY");
        branch1.setHeadquarters(false);

        SwiftCode branch2 = new SwiftCode();
        branch2.setCodeType("BIC11");
        branch2.setSwiftCode("XXXXXXXXZZZ");
        branch2.setHeadquarters(false);

        SwiftCode notBranch = new SwiftCode();
        notBranch.setCodeType("BIC11");
        notBranch.setSwiftCode("ZZZZZZZZYYY");
        notBranch.setHeadquarters(false);

        swiftCodeRepo.save(swiftCode);
        swiftCodeRepo.save(branch1);
        swiftCodeRepo.save(branch2);
        swiftCodeRepo.save(notBranch);

        List<SwiftCode> branches = swiftCodeRepo.getBankBranches("XXXXXXXXXXX");

        assertEquals("Number of branches", 2, branches.size());
    }

    @Test
    void shouldReturnSwiftCodesByCountryCode()
    {
        Country country1 = new Country();
        country1.setTimeZone("Europe/Berlin");
        country1.setCountryCode("DE");
        country1.setCountryName("GERMANY");

        Country country2 = new Country();
        country2.setTimeZone("Europe/Warsaw");
        country2.setCountryCode("PL");
        country2.setCountryName("POLAND");

        countryRepo.save(country1);
        countryRepo.save(country2);

        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setCodeType("BIC11");
        swiftCode1.setSwiftCode("XXXXXXXXXXX");
        swiftCode1.setHeadquarters(true);
        swiftCode1.setCountry(country1);

        SwiftCode swiftCode2 = new SwiftCode();
        swiftCode2.setCodeType("BIC11");
        swiftCode2.setSwiftCode("YYYYYYYYYYY");
        swiftCode2.setHeadquarters(false);
        swiftCode2.setCountry(country1);

        SwiftCode swiftCode3 = new SwiftCode();
        swiftCode3.setCodeType("BIC11");
        swiftCode3.setSwiftCode("ZZZZZZZZZZZ");
        swiftCode3.setHeadquarters(false);
        swiftCode3.setCountry(country2);

        swiftCodeRepo.save(swiftCode1);
        swiftCodeRepo.save(swiftCode2);
        swiftCodeRepo.save(swiftCode3);

        List<SwiftCode> codes = swiftCodeRepo.getByCountryCodes("DE");

        assertEquals("Number of swift codes", 2, codes.size());
    }

    @Test
    void shouldDeleteBySwiftCode()
    {
        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setCodeType("BIC11");
        swiftCode1.setSwiftCode("XXXXXXXXXXX");
        swiftCode1.setHeadquarters(true);

        SwiftCode swiftCode2 = new SwiftCode();
        swiftCode2.setCodeType("BIC11");
        swiftCode2.setSwiftCode("YYYYYYYYYYY");
        swiftCode2.setHeadquarters(false);

        SwiftCode swiftCode3 = new SwiftCode();
        swiftCode3.setCodeType("BIC11");
        swiftCode3.setSwiftCode("ZZZZZZZZZZZ");
        swiftCode3.setHeadquarters(false);

        swiftCodeRepo.save(swiftCode1);
        swiftCodeRepo.save(swiftCode2);
        swiftCodeRepo.save(swiftCode3);

        swiftCodeRepo.deleteBySwiftCode("XXXXXXXXXXX");
        swiftCodeRepo.deleteBySwiftCode("ZZZZZZZZZZZ");

        assertEquals("Number of swift codes", 1, swiftCodeRepo.findAll().size());
    }

    @Test
    void shouldDeleteOrphanedData()
    {
        Country country = new Country();
        country.setTimeZone("Europe/Warsaw");
        country.setCountryCode("PL");
        country.setCountryName("POLAND");

        BankName bankName = new BankName();
        bankName.setBankName("OKP");

        BankAddress bankAddress = new BankAddress();
        bankAddress.setTownName("CRACOW");
        bankAddress.setAddress("RAJSKA 18");

        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCodeType("BIC11");
        swiftCode.setSwiftCode("XXXXXXXXXXX");
        swiftCode.setHeadquarters(true);
        swiftCode.setCountry(country);
        swiftCode.setBankName(bankName);
        swiftCode.setBankAddress(bankAddress);

        countryRepo.save(country);
        bankNameRepo.save(bankName);
        bankAddressRepo.save(bankAddress);
        swiftCodeRepo.save(swiftCode);

        assertEquals("Bank name added", 1, bankNameRepo.findAll().size());
        assertEquals("Bank address added", 1, bankAddressRepo.findAll().size());
        assertEquals("Country added", 1, countryRepo.findAll().size());

        swiftCodeRepo.deleteBySwiftCode("XXXXXXXXXXX");
        swiftCodeRepo.deleteOrphanedAddresses();
        swiftCodeRepo.deleteOrphanedCountries();
        swiftCodeRepo.deleteOrphanedNames();

        assertEquals("Record deleted", 0, swiftCodeRepo.findAll().size());
        assertEquals("Bank name added", 0, bankNameRepo.findAll().size());
        assertEquals("Bank address added", 0, bankAddressRepo.findAll().size());
        assertEquals("Country added", 0, countryRepo.findAll().size());
    }
}

package com.example.swift_codes.Repos;


import com.example.swift_codes.Models.BankAddress;
import com.example.swift_codes.Models.BankName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest
@EntityScan("com.example.swift_codes.Models")
@EnableJpaRepositories("com.example.swift_codes.Repos")
public class IBankAddressRepoTests
{
    @Autowired
    private IBankAddressRepo bankAddressRepo;


    @Test
    void shouldFindByBankAddress()
    {
        BankAddress bankAddress = new BankAddress();
        bankAddress.setAddress("UL. JASNA 34");
        bankAddress.setTownName("CRACOW");

        bankAddressRepo.save(bankAddress);
        Optional<BankAddress> found = bankAddressRepo.findByAddress("UL. JASNA 34");

        assertTrue("Record added", found.isPresent());
        assertEquals("Record correct", "UL. JASNA 34", found.get().getAddress());
        assertEquals("Record correct", "CRACOW", found.get().getTownName());
    }

    @Test
    void shouldDeleteAllBankAddresses()
    {
        BankAddress bankAddress1 = new BankAddress();
        bankAddress1.setAddress("UL. JASNA 34");
        bankAddress1.setTownName("CRACOW");

        BankAddress bankAddress2 = new BankAddress();
        bankAddress2.setAddress("ZIELONA 18");
        bankAddress2.setTownName("WARSAW");

        bankAddressRepo.save(bankAddress1);
        bankAddressRepo.save(bankAddress2);

        bankAddressRepo.deleteAll();

        assertTrue("Records deleted", bankAddressRepo.findAll().isEmpty());
    }
}

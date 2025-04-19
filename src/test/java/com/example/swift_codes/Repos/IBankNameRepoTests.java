package com.example.swift_codes.Repos;


import com.example.swift_codes.Models.BankName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.Optional;

@DataJpaTest
@EntityScan("com.example.swift_codes.Models")
@EnableJpaRepositories("com.example.swift_codes.Repos")
public class IBankNameRepoTests
{
    @Autowired
    private IBankNameRepo bankNameRepo;


    @Test
    void shouldFindByBankName()
    {
        BankName bankName = new BankName();
        bankName.setBankName("NBANK");

        bankNameRepo.save(bankName);
        Optional<BankName> found = bankNameRepo.findByBankName("NBANK");

        assertTrue("Record added", found.isPresent());
        assertEquals("Record correct", "NBANK", found.get().getBankName());
    }

    @Test
    void shouldDeleteAllBankNames()
    {
        BankName bankName1 = new BankName();
        bankName1.setBankName("NBANK");

        BankName bankName2 = new BankName();
        bankName2.setBankName("OKP");

        bankNameRepo.save(bankName1);
        bankNameRepo.save(bankName2);

        bankNameRepo.deleteAll();

        assertTrue("Records deleted", bankNameRepo.findAll().isEmpty());
    }
}

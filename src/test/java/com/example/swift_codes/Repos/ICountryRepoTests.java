package com.example.swift_codes.Repos;


import com.example.swift_codes.Models.BankName;
import com.example.swift_codes.Models.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest
@EntityScan("com.example.swift_codes.Models")
@EnableJpaRepositories("com.example.swift_codes.Repos")
@Transactional
public class ICountryRepoTests
{
    @Autowired
    private ICountryRepo countryRepo;


    @Test
    void shouldFindByCountryCode()
    {
        Country country = new Country();
        country.setTimeZone("Europe/Berlin");
        country.setCountryCode("DE");
        country.setCountryName("GERMANY");

        countryRepo.save(country);
        Optional<Country> found = countryRepo.findByCountryCode("DE");

        assertTrue("Record added", found.isPresent());
        assertEquals("Record correct", "DE", found.get().getCountryCode());
        assertEquals("Record correct", "Europe/Berlin", found.get().getTimeZone());
        assertEquals("Record correct", "GERMANY", found.get().getCountryName());
    }

    @Test
    void shouldDeleteAllCountries()
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

        countryRepo.deleteAll();

        assertTrue("Records deleted", countryRepo.findAll().isEmpty());
    }
}

package com.example.swift_codes.Repos;

import com.example.swift_codes.Models.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICountryRepo extends JpaRepository<Country, Long>
{
    Optional<Country> findByCountryCode(String countryCode);
}

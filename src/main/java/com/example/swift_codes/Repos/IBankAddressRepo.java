package com.example.swift_codes.Repos;

import com.example.swift_codes.Models.BankAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IBankAddressRepo extends JpaRepository<BankAddress, Long> 
{
    Optional<BankAddress> findByAddress(String address);

    @Modifying
    @Query("DELETE FROM BankAddress")
    void deleteAll();
}

package com.example.swift_codes.Repos;

import com.example.swift_codes.Models.BankName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IBankNameRepo extends JpaRepository<BankName, Long>
{
    Optional<BankName> findByBankName(String bankName);

    @Modifying
    @Query("DELETE FROM BankName")
    void deleteAll();
}

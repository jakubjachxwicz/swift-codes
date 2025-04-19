package com.example.swift_codes.Repos;

import com.example.swift_codes.Models.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ISwiftCodeRepo extends JpaRepository<SwiftCode, Long>
{
    Optional<SwiftCode> findBySwiftCode(String swiftCode);

    @Query("SELECT sc FROM SwiftCode sc " +
            "WHERE SUBSTRING(sc.swiftCode, 1, 8) = SUBSTRING(:code, 1, 8) " +
            "AND sc.swiftCode <> :code")
    List<SwiftCode> getBankBranches(@Param("code") String code);

    boolean existsBySwiftCode(String swiftCode);

    @Query("SELECT sc FROM SwiftCode sc " +
            "JOIN sc.country c " +
            "WHERE c.countryCode = :countryCode")
    List<SwiftCode> getByCountryCodes(@Param("countryCode") String countryCode);

    void deleteBySwiftCode(String swiftCode);

    @Modifying
    @Query("DELETE FROM SwiftCode")
    void deleteAll();

    @Modifying
    @Query("DELETE FROM BankAddress ba WHERE NOT EXISTS (SELECT 1 FROM SwiftCode sc WHERE sc.bankAddress = ba)")
    void deleteOrphanedAddresses();

    @Modifying
    @Query("DELETE FROM BankName bn WHERE NOT EXISTS (SELECT 1 FROM SwiftCode sc WHERE sc.bankName = bn)")
    void deleteOrphanedNames();

    @Modifying
    @Query("DELETE FROM Country c WHERE NOT EXISTS (SELECT 1 FROM SwiftCode sc WHERE sc.country = c)")
    void deleteOrphanedCountries();
}

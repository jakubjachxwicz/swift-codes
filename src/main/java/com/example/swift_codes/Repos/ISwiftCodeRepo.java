package com.example.swift_codes.Repos;

import com.example.swift_codes.Models.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISwiftCodeRepo extends JpaRepository<SwiftCode, Long> {
}

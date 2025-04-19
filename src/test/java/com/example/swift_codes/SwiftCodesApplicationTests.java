package com.example.swift_codes;

import com.example.swift_codes.Models.SwiftCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SwiftCodesApplicationTests
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldLoadReturnAndDeleteData() throws Exception
    {
        mvc.perform(post("/admin/parse-data")
                .param("filename", "validtestfile.csv"))
                .andExpect(status().isOk());

        // First endpoint
        MvcResult result = mvc.perform(get("/v1/swift-codes/AAISALTRXXX"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<String, Object> swiftCode = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> branches = (List<Map<String, Object>>)swiftCode.get("branches");

        assertEquals("Swift code retrieved", "AAISALTRXXX", swiftCode.get("swiftCode"));
        assertEquals("Swift code retrieved", 1, branches.size());


        // Second endpoint
        result = mvc.perform(get("/v1/swift-codes/country/BG"))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        Map<String, Object> resultMap = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> swiftCodes = (List<Map<String, Object>>)resultMap.get("swiftCodes");

        assertEquals("Data retrieved", "BULGARIA", resultMap.get("countryName"));
        assertEquals("Data retrieved", 2, swiftCodes.size());


        // Third endpoint
        String requestBody = "{\n" +
                "    \"address\": \"TOPOLOWA 12\",\n" +
                "    \"bankName\": \"NBANK\",\n" +
                "    \"countryISO2\": \"PL\",\n" +
                "    \"countryName\": \"POLAND\",\n" +
                "    \"isHeadquarter\": true,\n" +
                "    \"swiftCode\": \"ALBPPLP1BMW\"\n" +
                "}";

        mvc.perform(post("/v1/swift-codes")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());

        mvc.perform(get("/v1/swift-codes/ALBPPLP1BMW"))
                .andExpect(status().isOk());


        // Fourth endpoint
        mvc.perform(delete("/v1/swift-codes/ALBPPLP1BMW"))
                .andExpect(status().isOk());

        mvc.perform(get("/v1/swift-codes/ALBPPLP1BMW"))
                .andExpect(status().isNotFound());
    }
}

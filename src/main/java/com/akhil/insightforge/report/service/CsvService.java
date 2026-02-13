package com.akhil.insightforge.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CsvService {

    public String readRawCsv(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    public String parsePreviewJson(String rawCsv) throws IOException {

        Reader reader = new StringReader(rawCsv);

        CSVFormat format = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setAllowMissingColumnNames(true)
                .build();

        CSVParser parser = new CSVParser(reader, format);

        List<String> headers = parser.getHeaderNames()
                .stream()
                .filter(h -> h != null && !h.trim().isEmpty())
                .toList();

        if (headers.isEmpty()) {
            throw new IllegalArgumentException("CSV file has no valid headers.");
        }

        List<Map<String, String>> records = new ArrayList<>();

        for (CSVRecord record : parser) {

            Map<String, String> row = new LinkedHashMap<>();

            for (String header : headers) {
                row.put(header, record.get(header));
            }

            records.add(row);
        }

        parser.close();

        List<Map<String, String>> preview = records.stream()
                .limit(50)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(preview);
    }
}

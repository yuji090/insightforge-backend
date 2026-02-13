package com.akhil.insightforge.report.controller;

import com.akhil.insightforge.report.model.Report;
import com.akhil.insightforge.report.service.CsvService;
import com.akhil.insightforge.report.service.OpenAiService;
import com.akhil.insightforge.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {

    private final CsvService csvService;
    private final ReportService reportService;
    private final OpenAiService openAiService;

    // STEP 1 - Upload (NO SAVE)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only CSV files allowed");
        }

        String rawCsv = csvService.readRawCsv(file);
        String previewJson = csvService.parsePreviewJson(rawCsv);

        return ResponseEntity.ok(Map.of(
                "fileName", file.getOriginalFilename(),
                "rawCsv", rawCsv,
                "previewData", previewJson
        ));
    }

    // STEP 2 - Analyze (NO SAVE)
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> body) {

        String rawCsv = body.get("rawCsv");

        String previewJson;
        try {
            previewJson = csvService.parsePreviewJson(rawCsv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String summary = openAiService.generateInsights(previewJson);

        return ResponseEntity.ok(Map.of(
                "summary", summary
        ));
    }

    // STEP 3 - Save Report (EXPLICIT SAVE)
    @PostMapping("/save")
    public Report save(@RequestBody Map<String, String> body) {

        return reportService.saveReport(
                body.get("fileName"),
                body.get("rawCsv"),
                body.get("summary")
        );
    }

    // HISTORY
    @GetMapping
    public List<Report> getLastFiveReports() {
        return reportService.getLastFiveReports();
    }

    // DOWNLOAD ORIGINAL CSV
    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id) {

        Report report = reportService.getReportById(id);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + report.getFileName())
                .body(report.getRawCsvContent());
    }
}

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

    // =========================================
    // STEP 1 - Upload (NO SAVE)
    // =========================================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
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

    // =========================================
    // STEP 2 - Analyze (NO SAVE)
    // =========================================
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> body) {

        String rawCsv = body.get("rawCsv");

        if (rawCsv == null || rawCsv.isBlank()) {
            return ResponseEntity.badRequest().body("No CSV data provided");
        }

        try {
            String previewJson = csvService.parsePreviewJson(rawCsv);
            String summary = openAiService.generateInsights(previewJson);

            return ResponseEntity.ok(Map.of("summary", summary));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to generate insights");
        }
    }

    // =========================================
    // STEP 3 - FOLLOW-UP QUESTION (NO SAVE)
    // =========================================
    @PostMapping("/followup")
    public ResponseEntity<?> followUp(@RequestBody Map<String, String> body) {

        String rawCsv = body.get("rawCsv");
        String summary = body.get("summary");
        String question = body.get("question");

        String answer = openAiService.generateFollowUpAnswer(rawCsv, summary, question);

        return ResponseEntity.ok(Map.of(
                "answer", answer
        ));
    }


    // =========================================
    // STEP 4 - SAVE REPORT (EXPLICIT SAVE)
    // =========================================
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {

        String fileName = body.get("fileName");
        String rawCsv = body.get("rawCsv");
        String summary = body.get("summary");

        if (fileName == null || rawCsv == null || summary == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        Report saved = reportService.saveReport(
                fileName,
                rawCsv,
                summary
        );

        return ResponseEntity.ok(saved);
    }

    // =========================================
    // HISTORY
    // =========================================
    @GetMapping
    public List<Report> getLastFiveReports() {
        return reportService.getLastFiveReports();
    }

    // =========================================
    // GET SINGLE REPORT
    // =========================================
    @GetMapping("/{id}")
    public Report getReport(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    // =========================================
    // DOWNLOAD ORIGINAL CSV
    // =========================================
    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id) {

        Report report = reportService.getReportById(id);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + report.getFileName())
                .body(report.getRawCsvContent());
    }


    @GetMapping("/latest")
    public ResponseEntity<?> getLatestReport() {

        List<Report> reports = reportService.getLastFiveReports();

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Report latest = reports.get(0);

        return ResponseEntity.ok(Map.of(
                "fileName", latest.getFileName(),
                "rawCsv", latest.getRawCsvContent(),
                "summary", latest.getSummaryData()
        ));
    }

}

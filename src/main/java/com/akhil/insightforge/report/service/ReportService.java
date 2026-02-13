package com.akhil.insightforge.report.service;

import com.akhil.insightforge.report.model.Report;
import com.akhil.insightforge.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Report saveReport(String fileName, String rawCsv, String summary) {

        Report report = new Report();
        report.setFileName(fileName);
        report.setRawCsvContent(rawCsv);
        report.setSummaryData(summary);
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    public List<Report> getLastFiveReports() {
        return reportRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }
}

package com.akhil.insightforge.report.controller;

import com.akhil.insightforge.report.repository.ReportRepository;
import com.akhil.insightforge.report.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final ReportRepository reportRepository;
    private final OpenAiService openAiService;

    @GetMapping("/api/health")
    public Map<String, String> health() {

        String dbStatus = "UP";
        String openAiStatus = "UP";

        try {
            reportRepository.count();
        } catch (Exception e) {
            dbStatus = "DOWN";
        }

        try {
            openAiService.generateInsights("[{\"test\":\"value\"}]");
        } catch (Exception e) {
            openAiStatus = "DOWN";
        }

        return Map.of(
                "app", "UP",
                "database", dbStatus,
                "openai", openAiStatus
        );
    }

}

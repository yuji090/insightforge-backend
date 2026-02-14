package com.akhil.insightforge.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .build();

    public String generateInsights(String dataJson) {

        String prompt = """
    You are a data analyst.

    Analyze the dataset below and return ONLY valid JSON in this format:

    {
      "summary": "...",
      "key_trends": ["..."],
      "outliers": ["..."],
      "recommended_checks": ["..."]
    }

    Do not include markdown or backticks.
    
    Dataset:
    """ + dataJson;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        String response = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String content = root
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

            return content;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }


    public String generateFollowUpAnswer(String rawCsv, String summary, String question) {

        String prompt = """
    You are a data analyst.

    Dataset:
    """ + rawCsv + """

    Previous Insights:
    """ + summary + """

    User Question:
    """ + question + """

    Give a clear and direct answer in plain text.
    Do NOT return JSON.
    Do NOT use markdown.
    Just return a simple explanation.
    """;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        String response = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            return root
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse follow-up response", e);
        }
    }


}


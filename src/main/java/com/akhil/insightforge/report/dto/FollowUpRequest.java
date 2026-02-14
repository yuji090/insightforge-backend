package com.akhil.insightforge.report.dto;

import lombok.Data;

@Data
public class FollowUpRequest {
    private String rawCsv;
    private String summary;
    private String question;
}

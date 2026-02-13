package com.akhil.insightforge.report.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(columnDefinition = "LONGTEXT")
    private String rawCsvContent;   // full original CSV

    @Column(columnDefinition = "TEXT")
    private String summaryData;     // AI JSON output

    private LocalDateTime createdAt;
}

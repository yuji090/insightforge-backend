package com.akhil.insightforge.report.repository;

import com.akhil.insightforge.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findTop5ByOrderByCreatedAtDesc();
}

package com.projectlyrics.server.domain.report.repository;

import com.projectlyrics.server.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCommandRepository extends JpaRepository<Report, Long> {
}

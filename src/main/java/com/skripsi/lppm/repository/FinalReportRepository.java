package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.FinalReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinalReportRepository extends JpaRepository<FinalReport, Long> {
    void deleteByProposalId(Long id);
}
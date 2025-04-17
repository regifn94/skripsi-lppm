package com.skripsi.lppm.service;

import com.skripsi.lppm.model.FinalReport;
import com.skripsi.lppm.model.ProgressReport;
import com.skripsi.lppm.repository.FinalReportRepository;
import com.skripsi.lppm.repository.ProgressReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReportService {
    @Autowired
    private ProgressReportRepository progressRepo;
    @Autowired
    private FinalReportRepository finalRepo;

    public ProgressReport submitProgressReport(ProgressReport report) {
        report.setSubmittedAt(LocalDate.now());
        return progressRepo.save(report);
    }

    public FinalReport submitFinalReport(FinalReport report) {
        return finalRepo.save(report);
    }
}
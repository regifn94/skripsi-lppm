package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.FinalReport;
import com.skripsi.lppm.model.ProgressReport;
import com.skripsi.lppm.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("/progress")
    public ResponseEntity<ProgressReport> submitProgress(@RequestBody ProgressReport report) {
        return ResponseEntity.ok(reportService.submitProgressReport(report));
    }

    @PostMapping("/final")
    public ResponseEntity<FinalReport> submitFinal(@RequestBody FinalReport report) {
        return ResponseEntity.ok(reportService.submitFinalReport(report));
    }
}

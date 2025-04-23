package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.ProgramStudy;
import com.skripsi.lppm.service.ProgramStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-studies")
@RequiredArgsConstructor
public class ProgramStudyController {
    private final ProgramStudyService programStudyService;

    @PostMapping
    public ResponseEntity<ProgramStudy> createProgramStudy(@RequestBody ProgramStudy programStudy) {
        return ResponseEntity.ok(programStudyService.createProgramStudy(programStudy));
    }

    @GetMapping
    public ResponseEntity<List<ProgramStudy>> getAllProgramStudies() {
        return ResponseEntity.ok(programStudyService.getAllProgramStudies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramStudy> getProgramStudyById(@PathVariable Long id) {
        return ResponseEntity.ok(programStudyService.getProgramStudyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramStudy> updateProgramStudy(@PathVariable Long id, @RequestBody ProgramStudy programStudy) {
        return ResponseEntity.ok(programStudyService.updateProgramStudy(id, programStudy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgramStudy(@PathVariable Long id) {
        programStudyService.deleteProgramStudy(id);
        return ResponseEntity.noContent().build();
    }
}

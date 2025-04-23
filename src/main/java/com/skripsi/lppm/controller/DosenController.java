package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.Dosen;
import com.skripsi.lppm.service.DosenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dosen")
@RequiredArgsConstructor
public class DosenController {

    private final DosenService dosenService;

    @PostMapping
    public ResponseEntity<Dosen> createDosen(@RequestBody Dosen dosen) {
        return ResponseEntity.ok(dosenService.createDosen(dosen));
    }

    @GetMapping
    public ResponseEntity<List<Dosen>> getAll() {
        return ResponseEntity.ok(dosenService.getAllDosens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dosen> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dosenService.getDosenById(id));
    }
}
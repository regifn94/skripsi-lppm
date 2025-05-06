package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/word")
public class WordController {

    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateWordFile() {
        try {
            wordService.generateLembarPengesahan("Lembar_Pengesahan.docx");
            return ResponseEntity.ok("Berhasil membuat file Word!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal membuat file Word.");
        }
    }
}

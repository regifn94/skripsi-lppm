package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/proposal")
    public ResponseEntity<byte[]> generateProposalPdf() throws Exception {
        // Isi data lain sesuai kebutuhan
        Proposal data = new Proposal();
        data.setJudul("Pengembangan Smart AI di Universitas Klabat");
        byte[] pdfBytes = pdfGeneratorService.generateProposalPdf(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "proposal.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}

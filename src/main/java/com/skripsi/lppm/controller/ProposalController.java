package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Proposal> submitProposal(@RequestBody Proposal proposal, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(proposalService.submitProposalWithFile(proposal, file));
    }

    @GetMapping
    public ResponseEntity<List<Proposal>> getAllProposals() {
        return ResponseEntity.ok(proposalService.getAllProposals());
    }
}

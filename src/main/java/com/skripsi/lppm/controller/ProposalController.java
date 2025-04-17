package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @PostMapping
    public ResponseEntity<Proposal> submitProposal(@RequestBody Proposal proposal) {
        return ResponseEntity.ok(proposalService.submitProposal(proposal));
    }

    @GetMapping
    public ResponseEntity<List<Proposal>> getAllProposals() {
        return ResponseEntity.ok(proposalService.getAllProposals());
    }
}

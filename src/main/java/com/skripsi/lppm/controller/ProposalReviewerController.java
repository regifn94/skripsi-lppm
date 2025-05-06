package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.ProposalReviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/propsal-review")
public class ProposalReviewerController {
    private final ProposalReviewerService proposalReviewerService;

    @PostMapping("add-reviewer/{reviewerId}/{proposalId}")
    public ResponseEntity<?> setReviewer(@PathVariable Long reviewerId, @PathVariable Long proposalId){
        proposalReviewerService.tunjukReviewer(reviewerId, proposalId);
        return ResponseEntity.ok().body("Success add reviewer");
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<?> getListProposal(@PathVariable Long reviewerId){
        return proposalReviewerService.getListProposalByReviewerId(reviewerId);
    }


}

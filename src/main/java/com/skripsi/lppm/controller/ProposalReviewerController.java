package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.ProposalReviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

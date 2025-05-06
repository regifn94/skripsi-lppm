package com.skripsi.lppm.controller;

import com.skripsi.lppm.dto.FacultyHeadReviewRequest;
import com.skripsi.lppm.dto.ProposalEvaluationRequest;
import com.skripsi.lppm.dto.ReviewerAddRequest;
import com.skripsi.lppm.dto.ReviewerRejectedRequest;
import com.skripsi.lppm.service.ProposalReviewerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/proposal-review")
public class ProposalReviewerController {
    private final ProposalReviewerService proposalReviewerService;

    @PostMapping("/{proposalId}/add-reviewer")
    public ResponseEntity<?> setReviewer(@PathVariable Long proposalId, @RequestBody ReviewerAddRequest request){
        return proposalReviewerService.setAsReviewer(proposalId, request);
    }

    @GetMapping("/list-proposal-by-reviewer/{reviewerId}")
    public ResponseEntity<?> getListProposal(@PathVariable Long reviewerId){
        return proposalReviewerService.getListProposalByReviewerId(reviewerId);
    }

    @GetMapping("/accepted/{proposalId}/{userId}")
    @Operation(
            summary = "Accepted as Reviewer",
            description = "Accepted as Reviewer"
    )
    public ResponseEntity<?> acceptedAsReviewer(@PathVariable Long proposalId, @PathVariable Long userId){
        return proposalReviewerService.acceptAsReviewer(proposalId, userId);
    }

    @GetMapping("/rejected/{proposalId}/{userId}")
    @Operation(
            summary = "Rejected as Reviewer",
            description = "Rejected as Reviewer"
    )
    public ResponseEntity<?> rejectedAsReviewer(
            @PathVariable Long proposalId,
            @PathVariable Long userId,
            @RequestBody ReviewerRejectedRequest request
            ){
        return proposalReviewerService.rejectedAsReviewer(proposalId, userId, request.getReason());
    }

    @PostMapping("/form-evaluation")
    public ResponseEntity<?> submitFormEvaluation(@RequestBody ProposalEvaluationRequest request){
        return proposalReviewerService.inputEvaluation(request);
    }

    /**
     * ketua penelitian fakultas akan menilai (reject / approved)
     * */
    @PostMapping("/proposal-rejected")
    @Operation(
            summary = "Reject proposal dari ketua penelitian fakultas",
            description = "Reject proposal dari ketua penelitian fakultas"
    )
    public ResponseEntity<?> rejectedFromLeadResearchFaculty(@RequestBody FacultyHeadReviewRequest request){
        return proposalReviewerService.rejectedProposal(request);
    }

    @PostMapping("/proposal-accepted")
    @Operation(
            summary = "Accepted proposal dari ketua penelitian fakultas",
            description = "Accepted proposal dari ketua penelitian fakultas"
    )
    public ResponseEntity<?> acceptedFromLeadResearchFaculty(@RequestBody FacultyHeadReviewRequest request){
        return proposalReviewerService.acceptedProposal(request);
    }

    @GetMapping("/dean-approve")
    public ResponseEntity<?> deanAcceptedApproval(@PathVariable Long proposalId){
        return proposalReviewerService.deanApproveProposal(proposalId);
    }
}

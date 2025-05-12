package com.skripsi.lppm.controller;

import com.skripsi.lppm.dto.*;
import com.skripsi.lppm.service.ProposalDecisionService;
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
    private final ProposalDecisionService proposalDecisionService;

    @PostMapping("/{proposalId}/add-reviewer")
    public ResponseEntity<?> setReviewer(@PathVariable Long proposalId, @RequestBody ReviewerAddRequest request){
        return proposalReviewerService.setAsReviewer(proposalId, request);
    }

    @GetMapping
    @Operation(
            summary = "List Proposal by reviewer id",
            description = "only show data by roles reviewer"
    )
    public ResponseEntity<?> getAllProposal(){
        return proposalReviewerService.getAllProposal();
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(
            summary = "List Proposal by reviewer id",
            description = "only show data by roles reviewer"
    )
    public ResponseEntity<?> getListProposal(@PathVariable Long reviewerId){
        return proposalReviewerService.getListProposalByReviewerId(reviewerId);
    }

    @GetMapping("/{proposalId}/reviewer/{reviewerId}/reject")
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

    @GetMapping("/{proposalId}/reviewer/{reviewerId}/accept")
    @Operation(
            summary = "Accepted as Reviewer",
            description = "Accepted as Reviewer"
    )
    public ResponseEntity<?> acceptedAsReviewer(@PathVariable Long proposalId, @PathVariable Long reviewerId){
        return proposalReviewerService.acceptAsReviewer(proposalId, reviewerId);
    }


    @PostMapping("/form-evaluation")
    public ResponseEntity<?> submitFormEvaluation(@RequestBody ProposalEvaluationRequest request){
        return proposalReviewerService.inputEvaluation(request);
    }

    @GetMapping("/list-evaluation")
    public ResponseEntity<?> getListProposalEvaluation(){
        return proposalReviewerService.getListProposalEvaluation();
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

    @GetMapping("/dean-approve/{proposalId}")
    public ResponseEntity<?> deanAcceptedApproval(@PathVariable Long proposalId){
        return proposalReviewerService.deanApproveProposal(proposalId);
    }

    @PostMapping("/make-decision")
    @Operation(
            summary = "Make decision from Dean",
            description = "Make decision from Dean"
    )
    public ResponseEntity<?> makeDecision(@RequestBody ProposalDecisionRequest request) {
        return proposalDecisionService.makeDecision(request);
    }

    @GetMapping("/download-approval-sheet/{proposalId}")
    public ResponseEntity<?> downloadApprovalSheet(@PathVariable Long proposalId){
        return proposalDecisionService.downloadApprovalSheet(proposalId);
    }
}

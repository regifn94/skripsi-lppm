package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.ProposalReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/propsal-review")
public class ProposalReviewerController {
    private final ProposalReviewService proposalReviewService;

}

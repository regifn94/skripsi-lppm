package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.ProposalReview;
import com.skripsi.lppm.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ProposalReview> submitReview(@RequestBody ProposalReview review) {
        return ResponseEntity.ok(reviewService.submitReview(review));
    }
}
package com.skripsi.lppm.service;

import com.skripsi.lppm.model.ProposalReview;
import com.skripsi.lppm.repository.ProposalReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ProposalReviewRepository reviewRepository;

    public ProposalReview submitReview(ProposalReview review) {
        return reviewRepository.save(review);
    }
}
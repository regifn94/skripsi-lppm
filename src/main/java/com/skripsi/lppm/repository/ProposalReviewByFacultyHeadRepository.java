package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalReviewByFacultyHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalReviewByFacultyHeadRepository extends JpaRepository<ProposalReviewByFacultyHead, Long> {
    List<ProposalReviewByFacultyHead> findByReviewedById(Long userId);
}

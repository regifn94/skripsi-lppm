package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalReviewer;
import com.skripsi.lppm.model.enums.StatusApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProposalReviewerRepository extends JpaRepository<ProposalReviewer, Long> {
    Optional<ProposalReviewer> findByProposalIdAndReviewerId(Long proposalId, Long reviewerId);
    List<ProposalReviewer> findByProposalId(Long proposalId);

    List<ProposalReviewer> findAllByProposal_IdAndStatus(Long proposalId, StatusApproval status);

    List<ProposalReviewer> findByReviewerId(Long reviewerId);

    boolean existsByProposalIdAndReviewerId(Long proposalId, Long id);

    void deleteByProposalId(Long id);
}

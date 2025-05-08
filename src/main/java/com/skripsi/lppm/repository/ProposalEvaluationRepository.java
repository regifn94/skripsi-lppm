package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalEvaluationRepository extends JpaRepository<ProposalEvaluation, Long> {
    int countByProposalId(Long proposalId);
}

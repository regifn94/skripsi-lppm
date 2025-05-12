package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalDecisionRepository extends JpaRepository<ProposalDecision, Long> {
    ProposalDecision findByProposalId(Long proposalId);
    List<ProposalDecision> findByDecidedBy_Id(Long userId);
}
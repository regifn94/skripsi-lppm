package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProposalMemberRepository extends JpaRepository<ProposalMember, Long> {
    List<ProposalMember> findByProposalId(Long proposalId);
    Optional<ProposalMember> findByProposalIdAndUserId(Long proposalId, Long userId);
}

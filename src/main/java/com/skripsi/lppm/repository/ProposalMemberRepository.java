package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.ProposalMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProposalMemberRepository extends JpaRepository<ProposalMember, Long> {
    List<ProposalMember> findByProposalId(Long proposalId);
    List<ProposalMember> findByUserId(Long userId);
    Optional<ProposalMember> findByProposalIdAndUserId(Long proposalId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProposalMember pm WHERE pm.proposal.id = :proposalId")
    void deleteAllByProposalId(@Param("proposalId") Long proposalId);
}

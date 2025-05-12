package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("SELECT p FROM Proposal p WHERE p.ketuaPeneliti.id = :userId")
    List<Proposal> findByKetuaPenelitiId(@Param("userId") Long userId);

    List<Proposal> findByIdIn(List<Long> proposalIds);
    @Query("""
    SELECT DISTINCT p FROM Proposal p
    LEFT JOIN p.proposalMember pm
    LEFT JOIN p.proposalReviewer pr
    LEFT JOIN p.reviewByFacultyHead fh
    LEFT JOIN p.proposalEvaluation pe
    WHERE p.ketuaPeneliti.id = :userId
    OR pm.user.id = :userId
    OR pr.reviewer.id = :userId
    OR fh.reviewedBy.id = :userId
    OR pe.reviewer.id = :userId
    """)
    List<Proposal> findProposalsByUserInAnyRole(@Param("userId") Long userId);

    @Query("""
        SELECT DISTINCT pm.proposal FROM ProposalMember pm
        WHERE pm.user.id = :userId
    """)
    List<Proposal> findByProposalMemberUserId(@Param("userId") Long userId);

    // Berdasarkan ketua peneliti
    List<Proposal> findByKetuaPeneliti_Id(Long userId);

    // Berdasarkan status proposal
    List<Proposal> findByStatus(String status);

    // Gabungan ketua peneliti + status
    List<Proposal> findByKetuaPeneliti_IdAndStatus(Long userId, String status);

    // Berdasarkan anggota proposal
    @Query("SELECT pm.proposal FROM ProposalMember pm WHERE pm.user.id = :userId")
    List<Proposal> findProposalsByMemberId(@Param("userId") Long userId);

    // Berdasarkan anggota proposal + status
    @Query("SELECT pm.proposal FROM ProposalMember pm WHERE pm.user.id = :userId AND pm.proposal.status = :status")
    List<Proposal> findProposalsByMemberIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // Berdasarkan reviewer proposal
    @Query("SELECT pr.proposal FROM ProposalReviewer pr WHERE pr.reviewer.id = :userId")
    List<Proposal> findProposalsByReviewerId(@Param("userId") Long userId);

    // Berdasarkan reviewer proposal + status
    @Query("SELECT pr.proposal FROM ProposalReviewer pr WHERE pr.reviewer.id = :userId AND pr.proposal.status = :status")
    List<Proposal> findProposalsByReviewerIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // Berdasarkan faculty head review
    @Query("SELECT pr.proposal FROM ProposalReviewByFacultyHead pr WHERE pr.reviewedBy.id = :userId")
    List<Proposal> findProposalsByFacultyHead(@Param("userId") Long userId);

    // Berdasarkan evaluasi reviewer
    @Query("SELECT pe.proposal FROM ProposalEvaluation pe WHERE pe.reviewer.id = :userId")
    List<Proposal> findProposalsByEvaluator(@Param("userId") Long userId);

}
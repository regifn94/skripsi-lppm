package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.ProposalDecisionRequest;
import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.model.ProposalDecision;
import com.skripsi.lppm.model.enums.DecisionStatus;
import com.skripsi.lppm.model.enums.ProposalStatus;
import com.skripsi.lppm.repository.ProposalDecisionRepository;
import com.skripsi.lppm.repository.ProposalRepository;
import com.skripsi.lppm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProposalDecisionService {

    private final ProposalDecisionRepository proposalDecisionRepository;

    private final ProposalRepository proposalRepository;

    private final UserRepository userRepository;

    public ResponseEntity<?> makeDecision(ProposalDecisionRequest request) {
        try {
            Optional<Proposal> optionalProposal = proposalRepository.findById(request.getProposalId());
            if (optionalProposal.isEmpty()) {
                return ResponseEntity.status(404).body("Proposal not found with id: " + request.getProposalId());
            }
            var decidedByUser = userRepository.findById(request.getDecidedByUserId());
            if (decidedByUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found with ID: " + request.getDecidedByUserId());
            }

            Proposal proposal = optionalProposal.get();

            ProposalDecision decision = proposal.getProposalDecision();
            if (decision == null) {
                decision = new ProposalDecision();
                decision.setProposal(proposal);
            }
            decision.setDecisionStatus(request.getStatus());
            decision.setDecisionNote(request.getNote());
            decision.setDecidedBy(decidedByUser.get());
            decision.setDecisionDate(LocalDateTime.now());

            proposal.setStatus(ProposalStatus.WAITING_DEAN_APPROVAL.name());
            proposal.setProposalDecision(decision);
            ProposalDecision savedDecision = proposalDecisionRepository.save(decision);
            return ResponseEntity.ok(savedDecision);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to make decision: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getDecisionByProposalId(Long proposalId) {
        try {
            ProposalDecision decision = proposalDecisionRepository.findByProposalId(proposalId);
            if (decision == null) {
                return ResponseEntity.status(404).body("No decision found for proposal id: " + proposalId);
            }
            return ResponseEntity.ok(decision);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve decision: " + e.getMessage());
        }
    }
}

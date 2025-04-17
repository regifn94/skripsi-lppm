package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.repository.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProposalService {
    @Autowired
    private ProposalRepository proposalRepository;

    public Proposal submitProposal(Proposal proposal) {
        proposal.setStatus("SUBMITTED");
        return proposalRepository.save(proposal);
    }

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }
}
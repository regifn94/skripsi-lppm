package com.skripsi.lppm.service;

import com.skripsi.lppm.model.FundingClaim;
import com.skripsi.lppm.repository.FundingClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundingService {
    @Autowired
    private FundingClaimRepository claimRepository;

    public FundingClaim submitClaim(FundingClaim claim) {
        claim.setStatus("PENDING");
        return claimRepository.save(claim);
    }
}

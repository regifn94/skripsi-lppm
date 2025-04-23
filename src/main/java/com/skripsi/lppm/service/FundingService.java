package com.skripsi.lppm.service;

import com.skripsi.lppm.model.FundingClaim;
import com.skripsi.lppm.repository.FundingClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FundingService {
    private final FundingClaimRepository claimRepository;
    public FundingClaim submitClaim(FundingClaim claim) {
        claim.setStatus("PENDING");
        return claimRepository.save(claim);
    }
}

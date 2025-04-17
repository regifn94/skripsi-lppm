package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.FundingClaim;
import com.skripsi.lppm.service.FundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funding")
public class FundingController {
    @Autowired
    private FundingService fundingService;

    @PostMapping("/claim")
    public ResponseEntity<FundingClaim> claimFunding(@RequestBody FundingClaim claim) {
        return ResponseEntity.ok(fundingService.submitClaim(claim));
    }
}

package com.skripsi.lppm.controller;

import com.skripsi.lppm.service.ProposalMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/proposal-members")
public class ProposalMemberController {
    private final ProposalMemberService proposalMemberService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findProposalByUserId(@PathVariable("id") Long userId){
        return proposalMemberService.findByUserId(userId);
    }
}

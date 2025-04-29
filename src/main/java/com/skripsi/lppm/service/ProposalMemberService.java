package com.skripsi.lppm.service;

import com.skripsi.lppm.repository.ProposalMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class ProposalMemberService {
    private final ProposalMemberRepository proposalMemberRepository;
    public ResponseEntity<?> findByUserId(Long userId){
        return ResponseEntity.ok(proposalMemberRepository.findByUserId(userId));
    }
}

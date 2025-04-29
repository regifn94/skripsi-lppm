package com.skripsi.lppm.controller;

import com.skripsi.lppm.dto.ProposalDTO;
import com.skripsi.lppm.dto.StatusUpdateDTO;
import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {
    @Autowired
    private ProposalService proposalService;

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id){
        return proposalService.detail(id);
    }
    @PostMapping("/without-file")
    public ResponseEntity<?> submitWithoutFile(@RequestBody ProposalDTO proposal){
        var save = proposalService.submitProposalWithoutFile(proposal);
        return ResponseEntity.ok(save);
    }

    @PostMapping(path = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) {
        var upload = proposalService.uploadFile(file);
        return ResponseEntity.ok(upload);
    }

    @PutMapping("/proposals/{id}/status")
    public ResponseEntity<Proposal> updateStatus(@PathVariable Long id,
                                                 @RequestBody StatusUpdateDTO statusDTO) {
        Proposal updated = proposalService.updateProposalStatus(id, statusDTO.getStatus(), statusDTO.getReason());
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Proposal>> getAllProposals(@PathVariable("id") Long id) {
        return ResponseEntity.ok(proposalService.getAllProposals(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProposal(@PathVariable("id") Long id){
        return ResponseEntity.ok(proposalService.deleteProposal(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProposalWithoutFile(@RequestBody ProposalDTO proposalDTO) {
        Object response = proposalService.updateProposalWithoutFile(proposalDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{proposalId}/approve-member/{userId}")
    public ResponseEntity<?> approveSebagaiAnggota(@PathVariable Long proposalId, @PathVariable Long userId) {
        proposalService.approvedMembers(proposalId, userId);
        return ResponseEntity.ok("Berhasil approve sebagai anggota proposal.");
    }

    @PostMapping("/{proposalId}/reject-member/{userId}")
    public ResponseEntity<?> rejectSebagaiAnggota(@PathVariable Long proposalId, @PathVariable Long userId) {
        proposalService.rejectedMembers(proposalId, userId);
        return ResponseEntity.ok("Berhasil menolak sebagai anggota proposal.");
    }
}

package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.ReviewerAddRequest;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.ProposalStatus;
import com.skripsi.lppm.model.enums.StatusApproval;
import com.skripsi.lppm.repository.EvaluasiProposalRepository;
import com.skripsi.lppm.repository.ProposalRepository;
import com.skripsi.lppm.repository.ProposalReviewerRepository;
import com.skripsi.lppm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalReviewerService {

    private final ProposalRepository proposalRepository;
    private final ProposalReviewerRepository reviewerRepository;
    private final EvaluasiProposalRepository evaluasiRepository;
    private final UserRepository userRepository;
    private final NotificationService notifikasiService;

    public void tunjukReviewer(Long proposalId, ReviewerAddRequest request) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
        var reviewers = userRepository.findByIdIn(request.getReviewerIds());

        for(var reviewer : reviewers){
            boolean sudahPernahDitugaskan = reviewerRepository
                    .existsByProposalIdAndReviewerId(proposalId, reviewer.getId());
            if (sudahPernahDitugaskan) {
                continue;
            }

            ProposalReviewer pr = new ProposalReviewer();
            pr.setProposal(proposal);
            pr.setReviewer(reviewer);
            pr.setAssignedAt(LocalDateTime.now());
            pr.setStatus(StatusApproval.PENDING);

            proposal.setStatus(ProposalStatus.WAITING_REVIEWER_RESPONSE.toString());

            proposalRepository.save(proposal);

            reviewerRepository.save(pr);

            notifikasiService.sendNotification(reviewer,
                    "Anda ditunjuk untuk mereview proposal: " + proposal.getJudul(),
                    "ReviewProposal", proposalId);
        }
    }

    public ResponseEntity<?> accepted(Long proposalId, Long reviewerId) {
        try {
            Proposal proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
            User reviewer = userRepository.findById(reviewerId)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            ProposalReviewer pr = new ProposalReviewer();
            pr.setProposal(proposal);
            pr.setReviewer(reviewer);
            pr.setAssignedAt(LocalDateTime.now());
            pr.setStatus(StatusApproval.ACCEPTED);

            reviewerRepository.save(pr);

            notifikasiService.sendNotification(reviewer,
                    "Anda ditunjuk untuk mereview proposal: " + proposal.getJudul(),
                    "ReviewProposal", proposalId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    public ResponseEntity<?> getListProposalByReviewerId(Long reviewerId){
        try {
            var reviewers = reviewerRepository.findByReviewerId(reviewerId);
            List<Long> proposalIds = new ArrayList<>();
            for (var reviewer : reviewers) {
                proposalIds.add(reviewer.getProposal().getId());
            }
            var proposals = proposalRepository.findByIdIn(proposalIds);
            return ResponseEntity.status(HttpStatus.OK).body(proposals);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : " + e.getMessage());
        }
    }

    // 2.2 Reviewer tolak undangan
    public ResponseEntity<?> rejectUndangan(Long proposalId, Long reviewerId, String reason) {
        try {
            ProposalReviewer reviewer = reviewerRepository.findByProposalIdAndReviewerId(proposalId, reviewerId)
                    .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));

            reviewer.setStatus(StatusApproval.REJECTED);
            reviewer.setReason(reason);
            reviewerRepository.save(reviewer);
            var proposalOpt = proposalRepository.findById(proposalId);
            Long facultyId = 0L;
            if (proposalOpt.isPresent()) {
                var proposal = proposalOpt.get();
                proposal.setStatus(ProposalStatus.REVIEW_COMPLETE.name());
                proposalRepository.save(proposal);
                facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();
            }

            // Kirim notifikasi ke Ketua Penelitian Fakultas
            List<User> ketuaList = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);
            for (User ketua : ketuaList) {
                notifikasiService.sendNotification(ketua,
                        "Reviewer menolak undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                        "ReviewProposal", proposalId);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    // 2.3 Reviewer terima undangan
    public ResponseEntity<?> acceptUndangan(Long proposalId, Long reviewerId) {
        try {
            ProposalReviewer reviewer = reviewerRepository.findByProposalIdAndReviewerId(proposalId, reviewerId)
                    .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));

            reviewer.setStatus(StatusApproval.ACCEPTED);
            reviewerRepository.save(reviewer);
            var proposalOpt = proposalRepository.findById(proposalId);
            Long facultyId = 0L;
            if (proposalOpt.isPresent()) {
                var proposal = proposalOpt.get();
                proposal.setStatus(ProposalStatus.REVIEW_IN_PROGRESS.toString());
                proposalRepository.save(proposal);
                facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();
            }

            List<User> ketuaList = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);
            for (User ketua : ketuaList) {
                notifikasiService.sendNotification(ketua,
                        "Reviewer menerima undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                        "ReviewProposal", proposalId);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    // 2.3 Reviewer isi form evaluasi
    public void isiEvaluasi(Long proposalId, Long reviewerId, EvaluasiProposal evaluasiInput) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));

        EvaluasiProposal evaluasi = new EvaluasiProposal();
        evaluasi.setProposal(proposal);
        evaluasi.setReviewer(reviewer);
        evaluasi.setKomentar(evaluasiInput.getKomentar());
        evaluasi.setNilai(evaluasiInput.getNilai());
        evaluasi.setRekomendasi(evaluasiInput.getRekomendasi());
        evaluasi.setTanggalEvaluasi(LocalDateTime.now());

        evaluasiRepository.save(evaluasi);
    }
}

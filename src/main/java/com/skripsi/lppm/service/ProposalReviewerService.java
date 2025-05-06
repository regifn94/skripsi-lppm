package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.FacultyHeadReviewRequest;
import com.skripsi.lppm.dto.ProposalEvaluationRequest;
import com.skripsi.lppm.dto.ReviewerAddRequest;
import com.skripsi.lppm.helper.NotificationHelper;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.FacultyHeadReview;
import com.skripsi.lppm.model.enums.ProposalStatus;
import com.skripsi.lppm.model.enums.StatusApproval;
import com.skripsi.lppm.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

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
    private final NotificationHelper notificationHelper;
    private final ProposalEvaluationRepository proposalEvaluationRepository;
    private final ProposalReviewByFacultyHeadRepository proposalReviewByFacultyHeadRepository;

    public ResponseEntity<?> setAsReviewer(Long proposalId, ReviewerAddRequest request) {
        try {
            Proposal proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
            var reviewers = userRepository.findByIdIn(request.getReviewerIds());

            for (var reviewer : reviewers) {
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

                notificationHelper.sendNotification(reviewer,
                        "Anda ditunjuk untuk mereview proposal: " + proposal.getJudul(),
                        "ReviewProposal", proposalId);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
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

            notificationHelper.sendNotification(reviewer,
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
    public ResponseEntity<?> rejectedAsReviewer(Long proposalId, Long reviewerId, String reason) {
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

            List<User> ketuaList = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);
            for (User ketua : ketuaList) {
                notificationHelper.sendNotification(ketua,
                        "Reviewer menolak undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                        "ReviewProposal", proposalId);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    public ResponseEntity<?> acceptAsReviewer(Long proposalId, Long reviewerId) {
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
                notificationHelper.sendNotification(ketua,
                        "Reviewer menerima undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                        "ReviewProposal", proposalId);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }
    public ResponseEntity<?> inputEvaluation(ProposalEvaluationRequest request) {
        try {
            Proposal proposal = proposalRepository.findById(request.getProposalId())
                    .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
            User reviewer = userRepository.findById(request.getReviewerId())
                    .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));
            ProposalEvaluation evaluation = new ProposalEvaluation();
            evaluation.setProposal(proposal);
            evaluation.setReviewer(reviewer);
            evaluation.setKomentar(request.getKomentar());
            evaluation.setNilaiKualitasDanKebaruan(request.getNilaiKualitasDanKebaruan());
            evaluation.setNilaiRoadmap(request.getNilaiRoadmap());
            evaluation.setNilaiTinjauanPustaka(request.getNilaiTinjauanPustaka());
            evaluation.setNilaiKemutakhiranSumber(request.getNilaiKemutakhiranSumber());
            evaluation.setNilaiMetodologi(request.getNilaiMetodologi());
            evaluation.setNilaiTargetLuaran(request.getNilaiTargetLuaran());
            evaluation.setNilaiKompetensiDanTugas(request.getNilaiKompetensiDanTugas());
            evaluation.setNilaiPenulisan(request.getNilaiPenulisan());
            evaluation.setDanaDiusulkan(request.getDanaDiusulkan());
            evaluation.setTanggalEvaluasi(request.getTanggalEvaluasi());

            var proposalEvaluation = proposalEvaluationRepository.save(evaluation);
            return ResponseEntity.ok(proposalEvaluation);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error " + e.getMessage());
        }
    }

    // ketua penelitian fakultas rejected proposal
    public ResponseEntity<?> rejectedProposal(FacultyHeadReviewRequest request){
        try {
            var user = userRepository.findById(request.getReviewedById());
            if(user.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body("user not found");
            }
            var proposalOpt = proposalRepository.findById(request.getProposalId());
            if (proposalOpt.isPresent()) {
                var proposal = proposalOpt.get();
                ProposalReviewByFacultyHead facultyHead = new ProposalReviewByFacultyHead();
                facultyHead.setReviewedBy(user.get());
                facultyHead.setProposal(proposal);
                facultyHead.setStatus(FacultyHeadReview.REJECTED.name());
                facultyHead.setNotes(request.getNotes());
                facultyHead.setReviewedAt(LocalDateTime.now());

                proposal.setStatus(ProposalStatus.REVIEW_COMPLETE.name());
                proposalRepository.save(proposal);
                proposalReviewByFacultyHeadRepository.save(facultyHead);
                var ketuaPenelitian = proposal.getKetuaPeneliti();

                notificationHelper.sendNotification(ketuaPenelitian,
                        "Proposal anda dengan judul " + proposal.getJudul() + "telah di tolak",
                        "ReviewProposal", proposal.getId());
                return ResponseEntity.status(HttpStatus.OK).body("success rejected proposal");
            }
            return ResponseEntity.status(HttpStatus.OK).body("proposal not found");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    // ketua penelitian fakultas approve proposal
    public ResponseEntity<?> acceptedProposal(FacultyHeadReviewRequest request){
        try {
            var user = userRepository.findById(request.getReviewedById());
            if(user.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body("user not found");
            }
            var proposalOpt = proposalRepository.findById(request.getProposalId());
            if (proposalOpt.isPresent()) {
                var proposal = proposalOpt.get();
                ProposalReviewByFacultyHead facultyHead = new ProposalReviewByFacultyHead();
                facultyHead.setReviewedBy(user.get());
                facultyHead.setProposal(proposal);
                facultyHead.setStatus(FacultyHeadReview.ACCEPTED.name());
                facultyHead.setNotes(request.getNotes());
                facultyHead.setReviewedAt(LocalDateTime.now());

                proposal.setStatus(ProposalStatus.WAITING_DEAN_APPROVAL.name());
                proposalRepository.save(proposal);
                proposalReviewByFacultyHeadRepository.save(facultyHead);

                var ketuaPenelitian = proposal.getKetuaPeneliti();
                notificationHelper.sendNotification(ketuaPenelitian,
                        "Proposal anda dengan judul " + proposal.getJudul() + "telah di diterima",
                        "ReviewProposal", proposal.getId());

                Long facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();

                List<User> deans = userRepository.findByRoleAndFaculty("DEKAN", facultyId);
                for(var dean :deans ){
                    notificationHelper.sendNotification(dean,
                            "Proposal berjudul" + proposal.getJudul() + "membutuhkan persetujuan Anda.",
                            "ReviewProposal", proposal.getId());
                }

                return ResponseEntity.status(HttpStatus.OK).body("success rejected proposal");
            }
            return ResponseEntity.status(HttpStatus.OK).body("proposal not found");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    public ResponseEntity<?> deanApproveProposal(Long proposalId){
        var proposalOpt = proposalRepository.findById(proposalId);
        if(proposalOpt.isPresent()){
            var proposal = proposalOpt.get();
            proposal.setApprovedByDean(true);
            proposalRepository.save(proposal);
        }
        return ResponseEntity.ok().build();
    }
}
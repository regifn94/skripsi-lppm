package com.skripsi.lppm.service;

import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.StatusApproval;
import com.skripsi.lppm.repository.EvaluasiProposalRepository;
import com.skripsi.lppm.repository.ProposalRepository;
import com.skripsi.lppm.repository.ProposalReviewerRepository;
import com.skripsi.lppm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalReviewerService {

    private final ProposalRepository proposalRepository;
    private final ProposalReviewerRepository reviewerRepository;
    private final EvaluasiProposalRepository evaluasiRepository;
    private final UserRepository userRepository;
    private final NotificationService notifikasiService;

    // 2.1 Menunjuk reviewer
    public void tunjukReviewer(Long proposalId, Long reviewerId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        ProposalReviewer pr = new ProposalReviewer();
        pr.setProposal(proposal);
        pr.setReviewer(reviewer);
        pr.setAssignedAt(LocalDateTime.now());
        pr.setStatus(StatusApproval.PENDING);

        reviewerRepository.save(pr);

        notifikasiService.sendNotification(reviewer,
                "Anda ditunjuk untuk mereview proposal: " + proposal.getJudul(),
                "ReviewProposal", proposalId);
    }

    // 2.2 Reviewer tolak undangan
    public void rejectUndangan(Long proposalId, Long reviewerId, String reason) {
        ProposalReviewer reviewer = reviewerRepository.findByProposalIdAndReviewerId(proposalId, reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));

        reviewer.setStatus(StatusApproval.REJECTED);
        reviewer.setReason(reason);
        reviewerRepository.save(reviewer);
        var proposalOpt = proposalRepository.findById(proposalId);
        Long facultyId = 0L;
        if(proposalOpt.isPresent()){
            var proposal = proposalOpt.get();
            facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();
        }

        // Kirim notifikasi ke Ketua Penelitian Fakultas
        List<User> ketuaList = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);
        for (User ketua : ketuaList) {
            notifikasiService.sendNotification(ketua,
                    "Reviewer menolak undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                    "ReviewProposal", proposalId);
        }
    }

    // 2.3 Reviewer terima undangan
    public void acceptUndangan(Long proposalId, Long reviewerId) {
        ProposalReviewer reviewer = reviewerRepository.findByProposalIdAndReviewerId(proposalId, reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer tidak ditemukan"));

        reviewer.setStatus(StatusApproval.ACCEPTED);
        reviewerRepository.save(reviewer);
        var proposalOpt = proposalRepository.findById(proposalId);
        Long facultyId = 0L;
        if(proposalOpt.isPresent()){
            var proposal = proposalOpt.get();
            facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();
        }

        List<User> ketuaList = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);
        for (User ketua : ketuaList) {
            notifikasiService.sendNotification(ketua,
                    "Reviewer menerima undangan untuk proposal: " + reviewer.getProposal().getJudul(),
                    "ReviewProposal", proposalId);
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

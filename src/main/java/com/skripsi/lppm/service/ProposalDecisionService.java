package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.ProposalDecisionRequest;
import com.skripsi.lppm.dto.ProposalToPdf;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.ProposalStatus;
import com.skripsi.lppm.repository.ProposalDecisionRepository;
import com.skripsi.lppm.repository.ProposalRepository;
import com.skripsi.lppm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProposalDecisionService {

    private final ProposalDecisionRepository proposalDecisionRepository;

    private final ProposalRepository proposalRepository;

    private final UserRepository userRepository;

    private final TemplateEngine templateEngine;

    public ResponseEntity<?> makeDecision(ProposalDecisionRequest request) {
        try {
            Optional<Proposal> optionalProposal = proposalRepository.findById(request.getProposalId());
            if (optionalProposal.isEmpty()) {
                return ResponseEntity.status(404).body("Proposal not found with id: " + request.getProposalId());
            }
            var decidedByUser = userRepository.findById(request.getDecidedByUserId());
            if (decidedByUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found with ID: " + request.getDecidedByUserId());
            }

            Proposal proposal = optionalProposal.get();

            ProposalDecision decision = proposal.getProposalDecision();
            if (decision == null) {
                decision = new ProposalDecision();
                decision.setProposal(proposal);
            }
            decision.setDecisionStatus(request.getStatus());
            decision.setDecisionNote(request.getNote());
            decision.setDecidedBy(decidedByUser.get());
            decision.setDecisionDate(LocalDateTime.now());

            proposal.setStatus(ProposalStatus.WAITING_DEAN_APPROVAL.name());
            proposal.setProposalDecision(decision);
            ProposalDecision savedDecision = proposalDecisionRepository.save(decision);
            return ResponseEntity.ok(savedDecision);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to make decision: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getDecisionByProposalId(Long proposalId) {
        try {
            ProposalDecision decision = proposalDecisionRepository.findByProposalId(proposalId);
            if (decision == null) {
                return ResponseEntity.status(404).body("No decision found for proposal id: " + proposalId);
            }
            return ResponseEntity.ok(decision);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve decision: " + e.getMessage());
        }
    }

    public ResponseEntity<?> downloadApprovalSheet(Long proposalId) {
        try {
            var proposalOpt = proposalRepository.findById(proposalId);
            if (proposalOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Proposal not found");
            }
            var proposal = proposalOpt.get();
            var proposalConvert = convertProposalToPfdFormat(proposal);

            byte[] pdfBytes = generateApprovalSheetPdf(proposalConvert);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"approval_sheet.pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    private ProposalToPdf convertProposalToPfdFormat(Proposal proposal) {
        List<ProposalToPdf.Peneliti> penelitiList = new ArrayList<>();
        ProposalToPdf.Peneliti ketua = new ProposalToPdf.Peneliti();
        List<ProposalToPdf.Mahasiswa> mahasiswasList = new ArrayList<>();

        var ketuaPeneliti = proposal.getKetuaPeneliti();
        ketua.setNama(ketuaPeneliti.getDosen().getName());
        ketua.setNik(ketuaPeneliti.getDosen().getNik());
        ketua.setProdi(ketuaPeneliti.getDosen().getFaculty().getFacultyName());
        ketua.setJabatan(ketuaPeneliti.getDosen().getFunctionalPosition());

        for (ProposalMember proposalMember : proposal.getProposalMember()) {
            User user = proposalMember.getUser();
            if (user != null) {
                Dosen dosen = user.getDosen();
                if (dosen != null) {
                    ProposalToPdf.Peneliti peneliti = ProposalToPdf.Peneliti.builder()
                            .nama(dosen.getName())
                            .nik(dosen.getNik())
                            .prodi(dosen.getFaculty().getFacultyName())
                            .jabatan(dosen.getFunctionalPosition())
                            .build();

                    penelitiList.add(peneliti);
                } else {
                    Students student = user.getStudent();
                    if (student != null) {
                        mahasiswasList.add(ProposalToPdf.Mahasiswa.builder()
                                .nama(student.getName())
                                .nim(student.getNim())
                                .prodi(student.getFaculty().getFacultyName())
                                .build());
                    }
                }
            }
        }

        return ProposalToPdf.builder()
                .judul(proposal.getJudul())
                .waktuPelaksanaan(proposal.getWaktuPelaksanaan())
                .sumberDana(proposal.getSumberDana())
                .danaYangDiUsulkan(proposal.getDanaYangDiUsulkan())
                .fileUrl(proposal.getFileUrl())
                .luaranPenelitian(proposal.getLuaranPenelitian())
                .namaMitra(proposal.getNamaMitra())
                .alamatMitra(proposal.getAlamatMitra())
                .picMitra(proposal.getPicMitra())
                .ketuaPeneliti(ketua)
                .status(proposal.getStatus())
                .anggotaDosen(penelitiList)
                .anggotaMahasiswa(mahasiswasList)
                .build();
    }

    private byte[] generateApprovalSheetPdf(ProposalToPdf proposal) throws Exception {
        Context context = getContext(proposal);
        String htmlContent = templateEngine.process("approval_sheet1", context);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    private Context getContext(ProposalToPdf proposal) {
        Context context = new Context();
        String judul = proposal.getJudul();
        String namaKetua = proposal.getKetuaPeneliti() != null ? proposal.getKetuaPeneliti().getNama() : "-";
        context.setVariable("judul", judul);
        context.setVariable("namaKetua", namaKetua);
        context.setVariable("nikKetua", proposal.getKetuaPeneliti() != null ? proposal.getKetuaPeneliti().getNik() : "-");
        context.setVariable("jabatanKetua", proposal.getKetuaPeneliti() != null ? proposal.getKetuaPeneliti().getJabatan() : "-");
        context.setVariable("fakultasKetua", proposal.getKetuaPeneliti() != null ? proposal.getKetuaPeneliti().getProdi() : "-");
        context.setVariable("anggotaDosen", proposal.getAnggotaDosen());
        context.setVariable("anggotaMahasiswa", proposal.getAnggotaMahasiswa());
        context.setVariable("namaMitra", proposal.getNamaMitra() != null ? proposal.getNamaMitra() : "-");
        context.setVariable("alamatMitra", proposal.getAlamatMitra() != null ? proposal.getAlamatMitra() : "-");
        context.setVariable("picMitra", proposal.getPicMitra() != null ? proposal.getPicMitra() : "-");
        context.setVariable("waktuPelaksanaan", proposal.getWaktuPelaksanaan() != null ? proposal.getWaktuPelaksanaan() : "-");
        context.setVariable("sumberDana", proposal.getSumberDana() != null ? proposal.getSumberDana() : "-");
        context.setVariable("danaYangDiUsulkan", proposal.getDanaYangDiUsulkan() != null ? proposal.getDanaYangDiUsulkan() : "-");
        context.setVariable("luaranPenelitian", proposal.getLuaranPenelitian() != null ? proposal.getLuaranPenelitian() : "-");

        try {
            String logoPath = new ClassPathResource("static/image/img.png").getURL().toString();
            context.setVariable("logoPath", logoPath);
        } catch (IOException e) {
            context.setVariable("logoPath", "");
        }
        return context;
    }
}

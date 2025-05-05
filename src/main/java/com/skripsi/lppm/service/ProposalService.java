package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.ProposalDTO;
import com.skripsi.lppm.helper.NotificationHelper;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.ProposalStatus;
import com.skripsi.lppm.model.enums.RoleInProposal;
import com.skripsi.lppm.model.enums.StatusApproval;
import com.skripsi.lppm.model.enums.StatusPenelitian;
import com.skripsi.lppm.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final UserRepository userRepository;
    private final ProposalRepository proposalRepository;
    private final NotificationRepository notificationRepository;
    private final DosenRepository dosenRepository;
    private final StudentRepository studentRepository;
    private final FinalReportRepository finalReportRepository;
    private final ProposalMemberRepository proposalMemberRepository;
    private final NotificationHelper notificationHelper;
    private final ProposalReviewerRepository proposalReviewerRepository;

    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok().body(proposalRepository.findAll());
    }
    public String uploadFile(MultipartFile multipartFile){
        String fileUrl = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                String basePath = System.getProperty("user.dir");
                String folderPath = basePath + File.separator + "uploads" + File.separator + "proposals";
                File folder = new File(folderPath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
                File file = new File(folder, fileName);
                multipartFile.transferTo(file);

                fileUrl = file.getAbsolutePath();
            } catch (IOException e) {
                throw new RuntimeException("Gagal menyimpan file proposal", e);
            }
        }
        return fileUrl;
    }

    @Transactional
    public Proposal updateProposalStatus(Long proposalId, StatusPenelitian status, String reason) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        proposal.setStatus(status.toString());
        Proposal updatedProposal = proposalRepository.save(proposal);

        List<ProposalMember> proposalMembers = proposalMemberRepository.findByProposalId(proposalId);

        notificationHelper.sendNotification(proposal.getKetuaPeneliti(),
                "Proposal Anda \"" + proposal.getJudul() + "\" telah " + (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK") +
                        (reason != null ? ". Alasan: " + reason : ""), "Proposal", proposalId);



        for (ProposalMember proposalMember : proposalMembers) {
            if (proposalMember.getUser().getDosen() != null) {
                notificationHelper.sendNotification(proposalMember.getUser().getDosen().getUser(),
                        "Proposal yang Anda ikuti \"" + proposal.getJudul() + "\" telah " +
                                (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK"),"Proposal", proposalId);
            }
            else{
                notificationHelper.sendNotification(proposalMember.getUser().getStudent().getUser(),
                        "Proposal yang Anda ikuti \"" + proposal.getJudul() + "\" telah " +
                                (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK"),"Proposal", proposalId);
            }
        }
        return updatedProposal;
    }

    public void approvedMembers(Long proposalId, Long userId) {
        ProposalMember member = proposalMemberRepository.findByProposalIdAndUserId(proposalId, userId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));

        member.setStatus(StatusApproval.APPROVED);
        proposalMemberRepository.save(member);

        List<ProposalMember> semuaAnggota = proposalMemberRepository.findByProposalId(proposalId);
        boolean semuaSetuju = semuaAnggota.stream()
                .allMatch(a -> a.getStatus() == StatusApproval.APPROVED);

        if (semuaSetuju) {
            Proposal proposal = member.getProposal();

            notificationHelper.sendNotification(proposal.getKetuaPeneliti(),
                    "Semua anggota telah menyetujui. Proposal akan dikirim ke Ketua Penelitian Fakultas.",
                    "Proposal", proposal.getId());

            List<User> ketuaPenelitianFakultasList = userRepository.findByRoles_Name("KETUA_PENELITIAN_FAKULTAS");
            for (User ketua : ketuaPenelitianFakultasList) {
                notificationHelper.sendNotification(ketua,
                        "Proposal baru dari " + proposal.getKetuaPeneliti().getUsername() + " berjudul: " + proposal.getJudul() + " siap ditinjau.",
                        "Proposal", proposal.getId());
            }

            proposal.setStatus(ProposalStatus.WAITING_FACULTY_HEAD.toString());
            proposalRepository.save(proposal);
        }
    }

    public void rejectedMembers(Long proposalId, Long userId) {
        var userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            throw new RuntimeException("Anggota tidak ditemukan");
        }
        var user = userOpt.get();
        String name = "";
        if(Objects.nonNull(user.getDosen())){
            name = user.getDosen().getName();
        }else{
            name = user.getStudent().getName();
        }
        ProposalMember member = proposalMemberRepository.findByProposalIdAndUserId(proposalId, userId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));

        member.setStatus(StatusApproval.REJECTED);
        proposalMemberRepository.save(member);
        Proposal proposal = member.getProposal();

        notificationHelper.sendNotification(proposal.getKetuaPeneliti(),
                name + " menolak bergabung. Proposal pending. Silakan pilih anggota baru.",
                proposal.getJudul(), proposal.getId());
    }

    public List<Proposal> getAllProposals(Long userId) {
        return proposalRepository.findByKetuaPenelitiId(userId);
    }

    public Boolean deleteProposal(Long id){
        try {
            finalReportRepository.deleteByProposalId(id);
            proposalRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object submitProposalWithoutFile(ProposalDTO proposalDTO) {
        try {
            var ketua = dosenRepository.findById(proposalDTO.getKetuaPeneliti());
            User ketuaPeneliti = userRepository.findById(ketua.get().getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            List<Dosen> dosenList = dosenRepository.findByIdIn(proposalDTO.getAnggotaDosen());
            List<Students> studentList = studentRepository.findAllById(proposalDTO.getAnggotaMahasiswa());

            Proposal proposal = new Proposal();
            proposal.setJudul(proposalDTO.getJudul());
            proposal.setWaktuPelaksanaan(proposalDTO.getWaktuPelaksanaan());
            proposal.setSumberDana(proposalDTO.getSumberDana());
            proposal.setDanaYangDiUsulkan(proposalDTO.getDanaYangDiUsulkan());
            proposal.setLuaranPenelitian(proposalDTO.getLuaranPenelitian());
            proposal.setNamaMitra(proposalDTO.getNamaMitra());
            proposal.setAlamatMitra(proposalDTO.getAlamatMitra());
            proposal.setPicMitra(proposalDTO.getPicMitra());
            proposal.setStatus(ProposalStatus.WAITING_MEMBER_APPROVAL.toString());
            proposal.setKetuaPeneliti(ketuaPeneliti);
            proposal.setFileUrl(proposalDTO.getFileUrl());
            proposal.setCreatedBy(ketuaPeneliti);

            Proposal savedProposal = proposalRepository.save(proposal);

            notificationHelper.sendNotification(ketuaPeneliti,
                    "Proposal baru telah dibuat dengan judul: " + proposal.getJudul(),
                    "Proposal", savedProposal.getId());

            List<ProposalMember> members = new ArrayList<>();

            for (Dosen dosen : dosenList) {
                if (dosen.getUser() != null) {
                    ProposalMember member = new ProposalMember();
                    member.setProposal(savedProposal);
                    member.setUser(dosen.getUser());
                    member.setIsMahasiswa(false);
                    member.setRoleInProposal(RoleInProposal.ANGGOTA_DOSEN);
                    member.setStatus(StatusApproval.PENDING);
                    members.add(member);

                    notificationHelper.sendNotification(dosen.getUser(),
                            "Anda ditambahkan sebagai anggota dosen dalam proposal: " + savedProposal.getJudul(),
                            "Proposal", savedProposal.getId());
                }
            }

            for (Students student : studentList) {
                if (student.getUser() != null) {
                    ProposalMember member = new ProposalMember();
                    member.setProposal(savedProposal);
                    member.setUser(student.getUser());
                    member.setIsMahasiswa(true);
                    member.setRoleInProposal(RoleInProposal.ANGGOTA_MAHASISWA);
                    member.setStatus(StatusApproval.PENDING);
                    members.add(member);

                    notificationHelper.sendNotification(student.getUser(),
                            "Anda ditambahkan sebagai anggota mahasiswa dalam proposal: " + savedProposal.getJudul(),
                            "Proposal", savedProposal.getId());
                }
            }

            proposalMemberRepository.saveAll(members);
            return savedProposal;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<?> detail(Long id){
        var detail = proposalRepository.findById(id);
        return ResponseEntity.ok().body(detail);
    }

    public Object updateProposalWithoutFile(ProposalDTO proposalDTO) {
        var ketua = dosenRepository.findById(proposalDTO.getKetuaPeneliti());
        User ketuaPeneliti = userRepository.findById(ketua.get().getUser().getId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Proposal proposal = proposalRepository.findById(proposalDTO.getId())
                .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));

        proposal.setJudul(proposalDTO.getJudul());
        proposal.setWaktuPelaksanaan(proposalDTO.getWaktuPelaksanaan());
        proposal.setSumberDana(proposalDTO.getSumberDana());
        proposal.setDanaYangDiUsulkan(proposalDTO.getDanaYangDiUsulkan());
        proposal.setLuaranPenelitian(proposalDTO.getLuaranPenelitian());
        proposal.setNamaMitra(proposalDTO.getNamaMitra());
        proposal.setAlamatMitra(proposalDTO.getAlamatMitra());
        proposal.setPicMitra(proposalDTO.getPicMitra());
        proposal.setKetuaPeneliti(ketuaPeneliti);
        proposal.setCreatedBy(ketuaPeneliti);
        proposal.setStatus(StatusPenelitian.DRAFT.toString());

        List<Dosen> dosenList = dosenRepository.findAllById(proposalDTO.getAnggotaDosen());
        List<Students> studentList = studentRepository.findAllById(proposalDTO.getAnggotaMahasiswa());

        Proposal updatedProposal = proposalRepository.save(proposal);
        proposalMemberRepository.deleteAllByProposalId(proposal.getId());

        List<ProposalMember> members = new ArrayList<>();

        for (Dosen dosen : dosenList) {
            if (dosen.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(updatedProposal);
                member.setUser(dosen.getUser());
                member.setIsMahasiswa(false);
                member.setRoleInProposal(RoleInProposal.ANGGOTA_DOSEN);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                notificationHelper.sendNotification(dosen.getUser(),
                        "Anda ditambahkan sebagai anggota dosen dalam proposal (update): " + updatedProposal.getJudul(),
                        "Proposal", updatedProposal.getId());
            }
        }

        for (Students student : studentList) {
            if (student.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(updatedProposal);
                member.setUser(student.getUser());
                member.setIsMahasiswa(true);
                member.setRoleInProposal(RoleInProposal.ANGGOTA_MAHASISWA);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                notificationHelper.sendNotification(student.getUser(),
                        "Anda ditambahkan sebagai anggota mahasiswa dalam proposal (update): " + updatedProposal.getJudul(),
                        "Proposal", updatedProposal.getId());
            }
        }

        proposalMemberRepository.saveAll(members);
        return updatedProposal;
    }

    public ResponseEntity<?> addAsReviewer(Long proposalId, Long userId){
        try {
            var proposalOpt = proposalRepository.findById(proposalId);
            if (proposalOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proposal tidak ditemukan");
            }
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User tidak ditemukan");
            }

            var proposal = proposalOpt.get();
            var user = userOpt.get();
            ProposalReviewer proposalReview = new ProposalReviewer();
            proposalReview.setReviewer(user);
            proposalReview.setProposal(proposal);
            proposalReview.setStatus(StatusApproval.PENDING);
            return ResponseEntity.ok(proposalReviewerRepository.save(proposalReview));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.ReactorNettyTcpStompClient;
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
    private final DosenRepository dosenRepository;
    private final StudentRepository studentRepository;
    private final FinalReportRepository finalReportRepository;
    private final ProposalMemberRepository proposalMemberRepository;
    private final NotificationHelper notificationHelper;
    private final ProposalReviewerRepository proposalReviewerRepository;

    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok().body(
                proposalRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
        );
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



        String message = "Proposal yang Anda ikuti \"" + proposal.getJudul() + "\" telah " +
                (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK");

        for (ProposalMember proposalMember : proposalMembers) {
            if (proposalMember == null || proposalMember.getUser() == null) {
                continue; // Lewati jika null
            }

            User user = proposalMember.getUser();
            Dosen dosen = user.getDosen();
            Students student = user.getStudent();

            User targetUser = null;
            if (dosen != null && dosen.getUser() != null) {
                targetUser = dosen.getUser();
            } else if (student != null && student.getUser() != null) {
                targetUser = student.getUser();
            }

            if (targetUser != null) {
                notificationHelper.sendNotification(targetUser, message, "Proposal", proposalId);
            }
        }

        return updatedProposal;
    }

    public void approvedMembers(Long proposalId, Long userId) {
        ProposalMember member = proposalMemberRepository.findByProposalIdAndUserId(proposalId, userId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));

        member.setStatus(StatusApproval.APPROVED);
        proposalMemberRepository.save(member);

        List<ProposalMember> allMembers = proposalMemberRepository.findByProposalId(proposalId);
        boolean allApproved = allMembers.stream()
                .allMatch(a -> a.getStatus() == StatusApproval.APPROVED);

        if (allApproved) {
            Proposal proposal = member.getProposal();

            notificationHelper.sendNotification(proposal.getKetuaPeneliti(),
                    "Semua anggota telah menyetujui. Proposal akan dikirim ke Ketua Penelitian Fakultas.",
                    "Proposal", proposal.getId());

            Long facultyId = proposal.getKetuaPeneliti().getDosen().getFaculty().getId();

            List<User> facultyResearchCoordinators = userRepository.findByRoleAndFaculty("KETUA_PENELITIAN_FAKULTAS", facultyId);

            for (User facultyResearchCoordinator : facultyResearchCoordinators) {
                notificationHelper.sendNotification(facultyResearchCoordinator,
                        "Proposal baru dari " + proposal.getKetuaPeneliti().getUsername() + " berjudul: " + proposal.getJudul() + " siap ditinjau.",
                        "Proposal", proposal.getId());
                proposal.setStatus(ProposalStatus.WAITING_FACULTY_HEAD.toString());
                proposalRepository.save(proposal);
            }

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

    @Transactional
    public Boolean deleteProposal(Long id){
        try {
            finalReportRepository.deleteByProposalId(id);

            // Hapus semua member yang terkait
            proposalMemberRepository.deleteByProposalId(id);

            // Hapus semua reviewer (jika perlu)
            proposalReviewerRepository.deleteByProposalId(id);

            // Hapus proposal
            proposalRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Debug lebih mudah
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
        try {
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
            proposal.setStatus(ProposalStatus.WAITING_MEMBER_APPROVAL.toString());

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
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<?> updateStatus(Long proposalId, String status){
        try{
            var proposalOpt = proposalRepository.findById(proposalId);
            if(proposalOpt.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proposal not found");
            }
            var proposal = proposalOpt.get();
            proposal.setStatus(status);
            return ResponseEntity.status(HttpStatus.OK).body(proposalRepository.save(proposal));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }
}
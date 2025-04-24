package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.ProposalDTO;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.model.enums.StatusApproval;
import com.skripsi.lppm.model.enums.StatusPenelitian;
import com.skripsi.lppm.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public Proposal submitProposalWithFile(ProposalDTO proposalDTO, MultipartFile multipartFile, Boolean bool) {
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

        User ketuaPeneliti = userRepository.findById(proposalDTO.getKetuaPeneliti())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Proposal proposal = new Proposal();
        proposal.setJudul(proposalDTO.getJudul());
        proposal.setWaktuPelaksanaan(proposalDTO.getWaktuPelaksanaan());
        proposal.setSumberDana(proposalDTO.getSumberDana());
        proposal.setDanaYangDiUsulkan(proposalDTO.getDanaYangDiUsulkan());
        proposal.setLuaranPenelitian(proposalDTO.getLuaranPenelitian());
        proposal.setNamaMitra(proposalDTO.getNamaMitra());
        proposal.setAlamatMitra(proposalDTO.getAlamatMitra());
        proposal.setPicMitra(proposalDTO.getPicMitra());
        proposal.setStatus(StatusPenelitian.DRAFT.toString());
        proposal.setKetuaPeneliti(ketuaPeneliti);
        proposal.setFileUrl(fileUrl);
        proposal.setCreatedBy(ketuaPeneliti);

        Proposal savedProposal = proposalRepository.save(proposal);

        sendNotification(ketuaPeneliti,
                "Proposal baru telah dibuat dengan judul: " + proposal.getJudul(),
                "Proposal", savedProposal.getId());

        List<ProposalMember> members = new ArrayList<>();

        // Dosen
        List<Dosen> dosenList = dosenRepository.findAllById(proposalDTO.getAnggotaDosen());
        for (Dosen dosen : dosenList) {
            if (dosen.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(savedProposal);
                member.setUser(dosen.getUser());
                member.setIsMahasiswa(false);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(dosen.getUser(),
                        "Anda ditambahkan sebagai anggota dosen dalam proposal: " + savedProposal.getJudul(),
                        "Proposal", savedProposal.getId());
            }
        }

        List<Students> studentList = studentRepository.findAllById(proposalDTO.getAnggotaMahasiswa());
        for (Students student : studentList) {
            if (student.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(savedProposal);
                member.setUser(student.getUser());
                member.setIsMahasiswa(true);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(student.getUser(),
                        "Anda ditambahkan sebagai anggota mahasiswa dalam proposal: " + savedProposal.getJudul(),
                        "Proposal", savedProposal.getId());
            }
        }

        proposalMemberRepository.saveAll(members);
        return savedProposal;
    }


    @Transactional
    public Proposal updateProposalStatus(Long proposalId, StatusPenelitian status, String reason) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        proposal.setStatus(status.toString());
        Proposal updatedProposal = proposalRepository.save(proposal);

        sendNotification(proposal.getKetuaPeneliti(),
                "Proposal Anda \"" + proposal.getJudul() + "\" telah " + (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK") +
                        (reason != null ? ". Alasan: " + reason : ""), "Proposal", proposalId);

        for (Dosen dosen : proposal.getAnggotaDosen()) {
            if (dosen.getUser() != null) {
                sendNotification(dosen.getUser(),
                        "Proposal yang Anda ikuti \"" + proposal.getJudul() + "\" telah " +
                                (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK"),"Proposal", proposalId);
            }
        }

        for (Students student : proposal.getAnggotaMahasiswa()) {
            if (student.getUser() != null) {
                sendNotification(student.getUser(),
                        "Proposal yang Anda ikuti \"" + proposal.getJudul() + "\" telah " +
                                (status == StatusPenelitian.ACCEPTED ? "DITERIMA" : "DITOLAK"), "Proposal", proposalId);
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

            // Notifikasi ke Ketua Peneliti
            sendNotification(proposal.getKetuaPeneliti(),
                    "Semua anggota telah menyetujui. Proposal akan dikirim ke Ketua Penelitian Fakultas.",
                    "Proposal", proposal.getId());

            // Notifikasi ke semua Ketua Penelitian Fakultas
            List<User> ketuaPenelitianFakultasList = userRepository.findByRoles_Name("KETUA_PENELITIAN_FAKULTAS");
            for (User ketua : ketuaPenelitianFakultasList) {
                sendNotification(ketua,
                        "Proposal baru dari " + proposal.getKetuaPeneliti().getUsername() + " berjudul: " + proposal.getJudul() + " siap ditinjau.",
                        "Proposal", proposal.getId());
            }

            // Update status proposal
            proposal.setStatus(StatusPenelitian.SUBMITTED.toString());
            proposalRepository.save(proposal);
        }
    }

    public void rejectedMembers(Long proposalId, Long userId) {
        ProposalMember member = proposalMemberRepository.findByProposalIdAndUserId(proposalId, userId)
                .orElseThrow(() -> new RuntimeException("Anggota tidak ditemukan"));

        member.setStatus(StatusApproval.REJECTED);
        proposalMemberRepository.save(member);

        Proposal proposal = member.getProposal();

        sendNotification(proposal.getKetuaPeneliti(),
                "Salah satu anggota menolak bergabung. Proposal pending. Silakan pilih anggota baru.",
                "Proposal", proposal.getId());
    }

    private void sendNotification(User user, String message, String proposal, Long proposalId) {
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .isRead(false)
                .relatedModel(proposal)
                .relatedId(proposalId)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
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
        String fileUrl = "";

        User ketuaPeneliti = userRepository.findById(proposalDTO.getKetuaPeneliti())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Proposal proposal = new Proposal();
        proposal.setJudul(proposalDTO.getJudul());
        proposal.setWaktuPelaksanaan(proposalDTO.getWaktuPelaksanaan());
        proposal.setSumberDana(proposalDTO.getSumberDana());
        proposal.setDanaYangDiUsulkan(proposalDTO.getDanaYangDiUsulkan());
        proposal.setLuaranPenelitian(proposalDTO.getLuaranPenelitian());
        proposal.setNamaMitra(proposalDTO.getNamaMitra());
        proposal.setAlamatMitra(proposalDTO.getAlamatMitra());
        proposal.setPicMitra(proposalDTO.getPicMitra());
        proposal.setStatus(StatusPenelitian.DRAFT.toString());
        proposal.setKetuaPeneliti(ketuaPeneliti);
        proposal.setFileUrl(fileUrl);
        proposal.setCreatedBy(ketuaPeneliti);

        Proposal savedProposal = proposalRepository.save(proposal);

        sendNotification(ketuaPeneliti,
                "Proposal baru telah dibuat dengan judul: " + proposal.getJudul(),
                "Proposal", savedProposal.getId());

        List<ProposalMember> members = new ArrayList<>();

        // Dosen
        List<Dosen> dosenList = dosenRepository.findAllById(proposalDTO.getAnggotaDosen());
        for (Dosen dosen : dosenList) {
            if (dosen.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(savedProposal);
                member.setUser(dosen.getUser());
                member.setIsMahasiswa(false);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(dosen.getUser(),
                        "Anda ditambahkan sebagai anggota dosen dalam proposal: " + savedProposal.getJudul(),
                        "Proposal", savedProposal.getId());
            }
        }

        List<Students> studentList = studentRepository.findAllById(proposalDTO.getAnggotaMahasiswa());
        for (Students student : studentList) {
            if (student.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(savedProposal);
                member.setUser(student.getUser());
                member.setIsMahasiswa(true);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(student.getUser(),
                        "Anda ditambahkan sebagai anggota mahasiswa dalam proposal: " + savedProposal.getJudul(),
                        "Proposal", savedProposal.getId());
            }
        }

        proposalMemberRepository.saveAll(members);
        return savedProposal;
    }


    public Object updateProposalWithoutFile(Long proposalId, ProposalDTO proposalDTO) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal tidak ditemukan"));

        User ketuaPeneliti = userRepository.findById(proposalDTO.getKetuaPeneliti())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

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
        proposal.setStatus(StatusPenelitian.DRAFT.toString()); // bisa disesuaikan status jika perlu

        Proposal updatedProposal = proposalRepository.save(proposal);

        // Hapus semua anggota lama
        proposalMemberRepository.deleteAllByProposalId(proposalId);

        List<ProposalMember> members = new ArrayList<>();

        // Tambahkan ulang anggota dosen
        List<Dosen> dosenList = dosenRepository.findAllById(proposalDTO.getAnggotaDosen());
        for (Dosen dosen : dosenList) {
            if (dosen.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(updatedProposal);
                member.setUser(dosen.getUser());
                member.setIsMahasiswa(false);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(dosen.getUser(),
                        "Anda ditambahkan sebagai anggota dosen dalam proposal (update): " + updatedProposal.getJudul(),
                        "Proposal", updatedProposal.getId());
            }
        }

        // Tambahkan ulang anggota mahasiswa
        List<Students> studentList = studentRepository.findAllById(proposalDTO.getAnggotaMahasiswa());
        for (Students student : studentList) {
            if (student.getUser() != null) {
                ProposalMember member = new ProposalMember();
                member.setProposal(updatedProposal);
                member.setUser(student.getUser());
                member.setIsMahasiswa(true);
                member.setStatus(StatusApproval.PENDING);
                members.add(member);

                sendNotification(student.getUser(),
                        "Anda ditambahkan sebagai anggota mahasiswa dalam proposal (update): " + updatedProposal.getJudul(),
                        "Proposal", updatedProposal.getId());
            }
        }

        proposalMemberRepository.saveAll(members);
        return updatedProposal;
    }

}
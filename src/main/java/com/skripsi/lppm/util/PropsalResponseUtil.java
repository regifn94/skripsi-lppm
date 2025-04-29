//package com.skripsi.lppm.util;
//
//import com.skripsi.lppm.dto.ProposalResponse;
//import com.skripsi.lppm.model.Proposal;
//import com.skripsi.lppm.model.User;
//
//public class PropsalResponseUtil {
//    private ProposalResponse convertToProposalResponse(Proposal proposal) {
//        ProposalResponse response = new ProposalResponse();
//        response.setId(proposal.getId());
//        response.setJudul(proposal.getJudul());
//        response.setWaktuPelaksanaan(proposal.getWaktuPelaksanaan());
//        response.setSumberDana(proposal.getSumberDana());
//        response.setDanaYangDiUsulkan(proposal.getDanaYangDiUsulkan());
//        response.setFileUrl(proposal.getFileUrl());
//        response.setLuaranPenelitian(proposal.getLuaranPenelitian());
//        response.setNamaMitra(proposal.getNamaMitra());
//        response.setAlamatMitra(proposal.getAlamatMitra());
//        response.setPicMitra(proposal.getPicMitra());
//        response.setStatus(proposal.getStatus().name()); // Assuming StatusApproval is an enum
//        response.setKetuaPeneliti(convertToKetuaPenelitiResponse(proposal.getKetuaPeneliti()));
//        response.setAnggotaDosen(proposal.getAnggotaDosen().stream().map(this::convertToAnggotaDosenResponse).collect(Collectors.toList()));
//        response.setAnggotaMahasiswa(proposal.getAnggotaMahasiswa().stream().map(this::convertToAnggotaMahasiswaResponse).collect(Collectors.toList()));
//        response.setProposalMember(proposal.getProposalMembers().stream().map(this::convertToProposalMemberResponse).collect(Collectors.toList()));
//        response.setFileBase64(proposal.getFileBase64());
//        response.setCreatedBy(convertToUserResponse(proposal.getCreatedBy()));
//
//        return response;
//    }
//
//    private ProposalResponse.KetuaPenelitiResponse convertToKetuaPenelitiResponse(User ketuaPeneliti) {
//        ProposalResponse.KetuaPenelitiResponse response = new KetuaPenelitiResponse();
//        response.setId(ketuaPeneliti.getId());
//        response.setUsername(ketuaPeneliti.getUsername());
//        response.setEmail(ketuaPeneliti.getEmail());
//        response.setUserType(ketuaPeneliti.getUserType());
//        response.setRoles(ketuaPeneliti.getRoles().stream()
//                .map(role -> new RoleResponse(role.getId(), role.getName()))
//                .collect(Collectors.toList()));
//        response.setDosen(convertToDosenResponse(ketuaPeneliti.getDosen()));
//        response.setStudent(convertToMahasiswaResponse(ketuaPeneliti.getMahasiswa()));
//        response.setProposals(ketuaPeneliti.getProposals().stream().map(Proposal::getJudul).collect(Collectors.toList()));
//        return response;
//    }
//
//    private AnggotaDosenResponse convertToAnggotaDosenResponse(Dosen anggotaDosen) {
//        AnggotaDosenResponse response = new AnggotaDosenResponse();
//        response.setId(anggotaDosen.getId());
//        response.setName(anggotaDosen.getName());
//        response.setNidn(anggotaDosen.getNidn());
//        response.setNik(anggotaDosen.getNik());
//        response.setFunctionalPosition(anggotaDosen.getFunctionalPosition());
//        response.setFaculty(new FacultyResponse(anggotaDosen.getFaculty().getId(), anggotaDosen.getFaculty().getFacultyName()));
//        response.setUser(anggotaDosen.getUser().getUsername());
//        return response;
//    }
//
//    private AnggotaMahasiswaResponse convertToAnggotaMahasiswaResponse(Mahasiswa anggotaMahasiswa) {
//        AnggotaMahasiswaResponse response = new AnggotaMahasiswaResponse();
//        response.setId(anggotaMahasiswa.getId());
//        response.setName(anggotaMahasiswa.getName());
//        response.setNim(anggotaMahasiswa.getNim());
//        response.setFaculty(new FacultyResponse(anggotaMahasiswa.getFaculty().getId(), anggotaMahasiswa.getFaculty().getFacultyName()));
//        response.setUser(anggotaMahasiswa.getUser().getUsername());
//        return response;
//    }
//
//    private ProposalMemberResponse convertToProposalMemberResponse(ProposalMember proposalMember) {
//        ProposalMemberResponse response = new ProposalMemberResponse();
//        response.setId(proposalMember.getId());
//        response.setProposal(proposalMember.getProposal().getJudul());
//        response.setUser(convertToUserResponse(proposalMember.getUser()));
//        response.setStatus(proposalMember.getStatus().name());
//        response.setIsMahasiswa(proposalMember.getIsMahasiswa());
//        return response;
//    }
//
//    private UserResponse convertToUserResponse(User user) {
//        UserResponse response = new UserResponse();
//        response.setId(user.getId());
//        response.setUsername(user.getUsername());
//        response.setEmail(user.getEmail());
//        response.setUserType(user.getUserType());
//        response.setRoles(user.getRoles().stream()
//                .map(role -> new RoleResponse(role.getId(), role.getName()))
//                .collect(Collectors.toList()));
//        return response;
//    }
//
//    private DosenResponse convertToDosenResponse(Dosen dosen) {
//        DosenResponse response = new DosenResponse();
//        response.setId(dosen.getId());
//        response.setName(dosen.getName());
//        response.setNidn(dosen.getNidn());
//        response.setNik(dosen.getNik());
//        response.setFunctionalPosition(dosen.getFunctionalPosition());
//        response.setFaculty(new FacultyResponse(dosen.getFaculty().getId(), dosen.getFaculty().getFacultyName()));
//        response.setUser(dosen.getUser().getUsername());
//        return response;
//    }
//
//    private MahasiswaResponse convertToMahasiswaResponse(Mahasiswa mahasiswa) {
//        MahasiswaResponse response = new MahasiswaResponse();
//        response.setId(mahasiswa.getId());
//        response.setName(mahasiswa.getName());
//        response.setNim(mahasiswa.getNim());
//        response.setFaculty(new FacultyResponse(mahasiswa.getFaculty().getId(), mahasiswa.getFaculty().getFacultyName()));
//        response.setUser(mahasiswa.getUser().getUsername());
//        return response;
//    }
//}

package com.skripsi.lppm.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProposalResponse {
    private Long id;
    private String judul;
    private String waktuPelaksanaan;
    private String sumberDana;
    private String danaYangDiUsulkan;
    private String fileUrl;
    private String luaranPenelitian;
    private String namaMitra;
    private String alamatMitra;
    private String picMitra;
    private String status;
    private KetuaPenelitiResponse ketuaPeneliti;
    private List<AnggotaDosenResponse> anggotaDosen;
    private List<AnggotaMahasiswaResponse> anggotaMahasiswa;
    private List<ProposalMemberResponse> proposalMember;
    private String fileBase64;
    private UserResponse createdBy;

    @Data
    private static class KetuaPenelitiResponse{
        private Long id;
        private String username;
        private String email;
        private String userType;
        private List<RoleResponse> roles;
        private DosenResponse dosen;
        private MahasiswaResponse student;
        private List<String> proposals;
    }

    @Data
    private static class AnggotaDosenResponse{
        private Long id;
        private String name;
        private String nidn;
        private String nik;
        private String functionalPosition;
        private FacultyResponse faculty;
        private String user;
    }

    @Data
    private static class AnggotaMahasiswaResponse{
        private Long id;
        private String name;
        private String nim;
        private FacultyResponse faculty;
        private String user;
    }

    @Data
    private static class ProposalMemberResponse{
        private Long id;
        private String proposal;
        private UserResponse user;
        private String status;
        private Boolean isMahasiswa;
    }

    @Data
    private static class UserResponse{
        private Long id;
        private String username;
        private String email;
        private String userType;
        private List<RoleResponse> roles;
    }

    @Data
    private static class RoleResponse {
        private Long id;
        private String name;
    }

    @Data
    private static class DosenResponse {
        private Long id;
        private String name;
        private String nidn;
        private String nik;
        private String functionalPosition;
        private FacultyResponse faculty;
        private String user;
    }

    @Data
    private static class FacultyResponse {
        private Long id;
        private String facultyName;
    }

    @Data
    private static class MahasiswaResponse {
        private Long id;
        private String name;
        private String nim;
        private FacultyResponse faculty;
        private String user;
    }
}

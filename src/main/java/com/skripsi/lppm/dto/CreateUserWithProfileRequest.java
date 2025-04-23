package com.skripsi.lppm.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateUserWithProfileRequest {
    private String username;
    private String email;
    private List<String> roles;

    private DosenRequest dosen;
    private StudentRequest student;

    @Data
    public static class DosenRequest {
        private String name;
        private String nidn;
        private String nik;
        private String functionalPosition;
        private Long facultyId;
    }

    @Data
    public static class StudentRequest {
        private String name;
        private String nim;
        private Long facultyId;
        private Long programStudyId;
    }
}
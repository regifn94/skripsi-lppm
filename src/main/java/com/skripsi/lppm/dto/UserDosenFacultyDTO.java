package com.skripsi.lppm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDosenFacultyDTO {
    private Long id;
    private String username;
    private String email;
    private String userType;
    private Set<RoleDTO> roles;
    private DosenDTO dosen;
    private StudentDTO student;
    private FacultyDTO faculty;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DosenDTO {
        private Long id;
        private String name;
        private String nidn;
        private String nik;
        private String functionalPosition;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentDTO {
        private Long id;
        private String name;
        private String nim;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacultyDTO {
        private Long id;
        private String facultyName;
    }
}

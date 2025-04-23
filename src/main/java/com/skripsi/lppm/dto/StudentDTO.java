package com.skripsi.lppm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO {
    private Long id;
    private String name;
    private String nim;
    private Long facultyId;
    private Long programStudyId;
    private Long userId;
}
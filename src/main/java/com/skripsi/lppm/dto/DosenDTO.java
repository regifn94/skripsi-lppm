package com.skripsi.lppm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DosenDTO {
    private Long id;
    private String name;
    private String nidn;
    private String nik;
    private String functionalPosition;
    private Long facultyId;
    private Long userId;
}
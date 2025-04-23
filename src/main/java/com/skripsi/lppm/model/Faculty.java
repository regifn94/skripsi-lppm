package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "faculty")
@Entity
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String facultyName;

    @JsonManagedReference
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramStudy> programStudy;

    @JsonBackReference
    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    private List<Dosen> dosenList;
}

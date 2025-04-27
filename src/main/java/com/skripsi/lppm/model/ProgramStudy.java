package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "program_study")
public class ProgramStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prodiName;


//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "faculty_id", nullable = false)
//    private Faculty faculty;
}

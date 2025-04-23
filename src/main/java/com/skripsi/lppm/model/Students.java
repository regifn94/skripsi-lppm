package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * table mahasiswa
 * */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Students {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nim;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "program_study_id", nullable = false)
    private ProgramStudy programStudy;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-student")
    private User user;

//    @ManyToOne
//    @JsonBackReference
//    public Proposal proposal;
}

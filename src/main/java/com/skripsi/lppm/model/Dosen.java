package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Dosen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nidn;
    private String nik;
    private String functionalPosition;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-dosen")
    private User user;

//    @ManyToOne
//    @JsonBackReference
//    private Proposal proposal;
}
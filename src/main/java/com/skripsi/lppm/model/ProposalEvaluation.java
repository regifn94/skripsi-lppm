package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    @JsonBackReference
    private Proposal proposal;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonManagedReference
    private User reviewer;

    private String danaDisetujui;

    private Integer nilaiKualitasDanKebaruan;         // Bobot 25
    private Integer nilaiRoadmap;                     // Bobot 15
    private Integer nilaiTinjauanPustaka;             // Bobot 10
    private Integer nilaiKemutakhiranSumber;          // Bobot 5
    private Integer nilaiMetodologi;                  // Bobot 20
    private Integer nilaiTargetLuaran;                // Bobot 10
    private Integer nilaiKompetensiDanTugas;          // Bobot 10
    private Integer nilaiPenulisan;                   // Bobot 5
    private Double totalNilai;

    @Column(columnDefinition = "TEXT")
    private String komentar;

    private String tanggalEvaluasi;
}

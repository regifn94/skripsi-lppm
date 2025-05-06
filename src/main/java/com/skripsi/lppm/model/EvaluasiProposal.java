package com.skripsi.lppm.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class EvaluasiProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Proposal proposal;

    @ManyToOne
    private User reviewer;

    private String komentar;

    private String nilai;

    private String rekomendasi;

    private LocalDateTime tanggalEvaluasi;
}

package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ProposalReviewByFacultyHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    @JsonIgnore
    private Proposal proposal;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    @JsonIgnore
    private User reviewedBy;

    private String status;

    private String notes;

    private LocalDateTime reviewedAt;
}

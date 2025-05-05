package com.skripsi.lppm.model;

import com.skripsi.lppm.model.enums.StatusApproval;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table
@Entity
@Data
public class ProposalReviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Proposal proposal;

    @ManyToOne
    private User reviewer;

    @Enumerated(EnumType.STRING)
    private StatusApproval status = StatusApproval.PENDING;

    private String reason;

    private LocalDateTime assignedAt;
}

package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.skripsi.lppm.model.enums.DecisionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "proposal_decisions")
public class ProposalDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DecisionStatus decisionStatus = DecisionStatus.PENDING;

    private String decisionNote;

    private LocalDateTime decisionDate;

    @OneToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    @JsonBackReference
    private Proposal proposal;

    @ManyToOne
    @JoinColumn(name = "decided_by_user_id", nullable = false)
    private User decidedBy;
}

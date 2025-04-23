package com.skripsi.lppm.model;

import com.skripsi.lppm.model.enums.StatusApproval;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table
public class ProposalMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Proposal proposal;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @Enumerated(EnumType.STRING)
    private StatusApproval status;

    private Boolean isMahasiswa;
}

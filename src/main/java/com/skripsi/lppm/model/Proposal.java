package com.skripsi.lppm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String fileUrl;

    @ManyToOne
    private User createdBy;

    @OneToMany(mappedBy = "proposal")
    private List<TeamMember> teamMembers;
}

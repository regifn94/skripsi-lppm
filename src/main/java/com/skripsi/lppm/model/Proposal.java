package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.skripsi.lppm.model.enums.StatusPenelitian;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@ToString(exclude = "ketuaPeneliti")
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

    private String judul;
    private String waktuPelaksanaan;
    private String sumberDana;
    private String danaYangDiUsulkan;

    private String fileUrl;

    private String luaranPenelitian;

    private String namaMitra;
    private String alamatMitra;
    private String picMitra;

    private String status;

    @ManyToOne
    @JoinColumn(name = "ketua_peneliti_id", nullable = true)
    private User ketuaPeneliti;

    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProposalMember> proposalMember = new ArrayList<>();

    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProposalReviewer> proposalReviewer = new ArrayList<>();

    @OneToOne(mappedBy = "proposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private ProposalReviewByFacultyHead reviewByFacultyHead;

    private Boolean approvedByDean = false;

    @Transient
    private String fileBase64;

    @ManyToOne
    private User createdBy;
}

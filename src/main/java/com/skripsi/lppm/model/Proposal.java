package com.skripsi.lppm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skripsi.lppm.model.enums.StatusPenelitian;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "ketua_peneliti_id", nullable = false)
    private User ketuaPeneliti;

    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProposalMember> proposalMember = new ArrayList<>();

    @Transient
    private String fileBase64;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;
}

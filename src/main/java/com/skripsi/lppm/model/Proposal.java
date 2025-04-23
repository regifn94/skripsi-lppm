package com.skripsi.lppm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToOne(cascade = CascadeType.ALL)
    private User ketuaPeneliti;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Dosen> anggotaDosen= new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Students> anggotaMahasiswa = new ArrayList<>();

    @Transient
    private String fileBase64;

    @ManyToOne
    private User createdBy;
}

package com.skripsi.lppm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProposalDTO {
    private Long id;
    private String judul;
    private String waktuPelaksanaan;
    private String sumberDana;
    private String danaYangDiUsulkan;
    private String luaranPenelitian;
    private String namaMitra;
    private String alamatMitra;
    private String picMitra;
    private String status;
    private Long ketuaPeneliti;  // ID dari User untuk ketua peneliti
    private List<Long> anggotaDosen;  // List ID dari Dosen
    private List<Long> anggotaMahasiswa;  // List ID dari Mahasiswa
    private Long createdBy;  // ID dari User untuk createdBy
}
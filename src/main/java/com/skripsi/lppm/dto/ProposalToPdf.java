package com.skripsi.lppm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProposalToPdf {
    private String judul;
    private String waktuPelaksanaan;
    private String sumberDana;
    private String danaYangDiUsulkan;

    private String fileUrl;

    private String luaranPenelitian;

    private String namaMitra;
    private String alamatMitra;
    private String picMitra;

    private Peneliti ketuaPeneliti;

    private String status;
    private List<Peneliti> anggotaDosen;
    private List<Mahasiswa> anggotaMahasiswa;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Peneliti {
        private String nama;
        private String nik;
        private String jabatan;
        private String prodi;
        // getter dan setter
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Mahasiswa {
        private String nama;
        private String nim;
        private String prodi;
    }
}

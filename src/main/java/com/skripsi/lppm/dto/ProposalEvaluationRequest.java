package com.skripsi.lppm.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProposalEvaluationRequest {

    private Long proposalId;
    private Long reviewerId;

    private String waktuPelaksanaan;
    private String sumberDana;
    private String danaDiusulkan;
    private String danaDisetujui;
    private String luaranPenelitian;

    private Integer nilaiKualitasDanKebaruan;
    private Integer nilaiRoadmap;
    private Integer nilaiTinjauanPustaka;
    private Integer nilaiKemutakhiranSumber;
    private Integer nilaiMetodologi;
    private Integer nilaiTargetLuaran;
    private Integer nilaiKompetensiDanTugas;
    private Integer nilaiPenulisan;

    private String komentar;

    private String tanggalEvaluasi;
}
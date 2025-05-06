package com.skripsi.lppm.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class WordService {

    public void generateLembarPengesahan(String filePath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Judul
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runTitle = title.createRun();
        runTitle.setText("LEMBAR PENGESAHAN PROPOSAL PENELITIAN");
        runTitle.setBold(true);
        runTitle.setFontSize(14);

        addEmptyLine(document);

        // Isi
        addLabel(document, "Judul Proposal Penelitian");
        addLabel(document, "Identitas Peneliti");
        addLabel(document, "Nama Peneliti (Ketua)");
        addLabel(document, "NIK/NIDN");
        addLabel(document, "Jabatan Fungsional/Golongan");
        addLabel(document, "Fakultas/Program Studi");

        addLabel(document, "Anggota Peneliti Dosen");
        addLabel(document, "Nama Peneliti");
        addLabel(document, "NIK/NIDN");
        addLabel(document, "Jabatan Fungsional/Golongan");
        addLabel(document, "Fakultas/Program Studi");

        addLabel(document, "Anggota Peneliti Mahasiswa");
        addLabel(document, "Nama Peneliti");
        addLabel(document, "NIM");
        addLabel(document, "Fakultas/Program Studi");

        addLabel(document, "Mitra (Jika ada)");
        addLabel(document, "Nama Mitra");
        addLabel(document, "Alamat");
        addLabel(document, "Penanggung Jawab");

        addLabel(document, "Waktu Pelaksanaan");
        addLabel(document, "Sumber Dana");
        addLabel(document, "Dana yang diusulkan Rp.");
        addLabel(document, "Luaran Penelitian");

        addEmptyLine(document);
        addParagraph(document, "Airmadidi, Tanggal-Bulan-Tahun");

        addEmptyLine(document);

        // Tanda tangan
        addSignatureBlock(document, "Ketua Peneliti", "Ketua Penelitian Fakultas");
        addSignatureBlock(document, "Dekan", "Kepala LPPM Universitas Klabat");

        // Simpan file
        FileOutputStream out = new FileOutputStream(filePath);
        document.write(out);
        out.close();
        document.close();
    }

    private void addLabel(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(text);
    }

    private void addEmptyLine(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.createRun().addBreak();
    }

    private void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(text);
    }

    private void addSignatureBlock(XWPFDocument doc, String left, String right) {
        XWPFTable table = doc.createTable(1, 2);
        table.setWidth("100%");
        table.getRow(0).getCell(0).setText(left);
        table.getRow(0).getCell(1).setText(right);

        // Tambah space untuk tanda tangan
        for (int i = 0; i < 3; i++) addEmptyLine(doc);

        XWPFTable table2 = doc.createTable(1, 2);
        table2.setWidth("100%");
        table2.getRow(0).getCell(0).setText("________________");
        table2.getRow(0).getCell(1).setText("__________________");
    }
}


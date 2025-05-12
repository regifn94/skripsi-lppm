package com.skripsi.lppm.helper;

import com.skripsi.lppm.model.Proposal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileTemplate {
    public String APPROVAL_SHEET() throws IOException {
        // Opsi 1: Baca gambar dan konversi ke base64
        byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/static/image/img.png"));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String imageSrc = "data:image/png;base64," + base64Image;
        Document doc = Jsoup.parse("<!DOCTYPE html>\n" +
                "<html lang=\"id\" xmlns:th=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Lembar Pengesahan Proposal Penelitian</title>\n" +
                "    <style>\n" +
                "        @page {\n" +
                "            size: A4;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Times New Roman', serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            width: 210mm;\n" +
                "            height: 297mm;\n" +
                "            background-color: #fdfdfd;\n" +
                "        }\n" +
                "        .a4-container {\n" +
                "            width: 185mm;\n" +
                "            margin: 12mm auto;\n" +
                "            background-color: #fff;\n" +
                "            padding: 10mm;\n" +
                "            box-shadow: 0 0 5mm rgba(0,0,0,0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            text-align: center;\n" +
                "            border-bottom: 2px solid #000;\n" +
                "            padding-bottom: 5px;\n" +
                "        }\n" +
                "        .university-name {\n" +
                "            font-weight: bold;\n" +
                "            font-size: 16pt;\n" +
                "        }\n" +
                "        .university-address,\n" +
                "        .university-contact,\n" +
                "        .university-website {\n" +
                "            font-size: 10pt;\n" +
                "        }\n" +
                "        .document-title {\n" +
                "            text-align: center;\n" +
                "            font-size: 14pt;\n" +
                "            font-weight: bold;\n" +
                "            text-decoration: underline;\n" +
                "            margin: 15px 0;\n" +
                "        }\n" +
                "        .form-table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "        }\n" +
                "        .form-table td {\n" +
                "            border: 1px solid #000;\n" +
                "            padding: 8px;\n" +
                "            vertical-align: top;\n" +
                "            font-size: 11pt;\n" +
                "        }\n" +
                "        .form-table .label-cell {\n" +
                "            font-weight: bold;\n" +
                "            width: 35%;\n" +
                "            background-color: #f0f0f0;\n" +
                "        }\n" +
                "        .signature-section {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            margin-top: 100px;\n" +
                "        }\n" +
                "\n" +
                "        .column {\n" +
                "            width: 45%;\n" +
                "        }\n" +
                "\n" +
                "        .text-right {\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "\n" +
                "        .signatures {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            margin-top: 60px;\n" +
                "        }\n" +
                "\n" +
                "        .signature-block {\n" +
                "            width: 45%;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .signature-space {\n" +
                "            margin-top: 60px;\n" +
                "            border-top: 1px solid black;\n" +
                "            width: 100%;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .bold {\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "\n" +
                "        .center {\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        .top-margin {\n" +
                "            margin-top: 40px;\n" +
                "        }\n" +
                "        .date-place {\n" +
                "            text-align: right;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 11pt;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"a4-container\">\n" +
                "    <div class=\"header\">\n" +
                "        <header>\n" +
                "             <img src=\"" + imageSrc + "\" alt=\"Logo Universitas Klabat\" style=\"width: 600px; height: auto;\">\n"  +
                "        </header>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"document-title\">LEMBAR PENGESAHAN PROPOSAL PENELITIAN</div>\n" +
                "\n" +
                "    <table class=\"form-table\">\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Judul Proposal Penelitian</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td colspan=\"2\" class=\"section-title\">Identitas Peneliti (Ketua)</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Nama Lengkap</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">NIK/NIDN</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Jabatan Fungsional/Golongan</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Fakultas / Program Studi</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "\n" +
                "        <tr>\n" +
                "            <td colspan=\"2\" class=\"section-title\">Anggota Peneliti Dosen</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Nama</td>\n" +
                "            <td th:text=\"${nama}\"></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">NIK/NIDN</td>\n" +
                "            <td th:text=\"${nik}\"></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Jabatan Fungsional/Golongan</td>\n" +
                "            <td th:text=\"${jabatan}\"></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Fakultas / Program Studi</td>\n" +
                "            <td th:text=\"${prodi}\"></td>\n" +
                "        </tr>\n" +
                "\n" +
                "        <tr>\n" +
                "            <td colspan=\"2\" class=\"section-title\">Anggota Peneliti Mahasiswa</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Nama</td>\n" +
                "            <td th:text=\"${nama}\"></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">NIM</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Fakultas / Program Studi</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "\n" +
                "        <tr>\n" +
                "            <td colspan=\"2\" class=\"section-title\">Mitra (Jika Ada)</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Nama Mitra</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Alamat</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Penanggung Jawab</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Waktu Pelaksanaan</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Sumber Dana</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Dana yang Diusulkan (Rp)</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td class=\"label-cell\">Luaran Penelitian</td>\n" +
                "            <td></td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <div class=\"date-place\">Airmadidi, ......................</div>\n" +
                "\n" +
                "    <div class=\"signature-section\">\n" +
                "        <div class=\"column\">\n" +
                "            <p><strong>Ketua Peneliti</strong></p>\n" +
                "            <div class=\"top-margin\"></div>\n" +
                "            <div class=\"signature-space\"></div>\n" +
                "            <p>Dekan</p>\n" +
                "            <div class=\"top-margin\"></div>\n" +
                "            <div class=\"signature-space\"></div>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"column\">\n" +
                "            <p>Menyetujui,<br><strong>Ketua Penelitian Fakultas</strong></p>\n" +
                "            <div class=\"top-margin\"></div>\n" +
                "            <div class=\"signature-space\"></div>\n" +
                "            <p>Mengetahui,<br><strong>Kepala LPPM Universitas Klabat</strong></p>\n" +
                "            <div class=\"top-margin\"></div>\n" +
                "            <div class=\"signature-space\"></div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>");
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return doc.html();
    }
}

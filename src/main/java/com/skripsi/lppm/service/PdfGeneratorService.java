package com.skripsi.lppm.service;

import com.skripsi.lppm.helper.FileTemplate;
import com.skripsi.lppm.model.Proposal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public byte[] generateProposalPdf(Proposal data) throws Exception {
        Context context = new Context();
        context.setVariable("judul", data.getJudul());
        context.setVariable("namaKetua", "-");
        context.setVariable("nikKetua", "-");
        context.setVariable("jabatanKetua", "-");
        context.setVariable("fakultasKetua", "-");

        String logoPath = new File("src/main/resources/static/image/img.png").toURI().toString();
        context.setVariable("logoPath", logoPath);

        // Lanjutkan setVariable untuk semua field

        String htmlContent = templateEngine.process("approval_sheet", context);

        FileTemplate fileTemplate = new FileTemplate();
        fileTemplate.APPROVAL_SHEET();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(fileTemplate.APPROVAL_SHEET());
        renderer.layout();


        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}
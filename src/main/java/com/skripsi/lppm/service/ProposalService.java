package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Proposal;
import com.skripsi.lppm.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public Proposal submitProposal(Proposal proposal) {
        return proposalRepository.save(proposal);
    }

    public Proposal submitProposalWithFile(Proposal proposal, MultipartFile file) {
        try {
            // Rename file sesuai judul proposal (replace spasi dengan _ dan lowercase)
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String sanitizedTitle = proposal.getJudul().toLowerCase().replaceAll("\\s+", "_");
            String newFileName = sanitizedTitle + extension;

            // Simpan ke folder lokal (misal: uploads/)
            String uploadDir = "uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            Path filePath = Paths.get(uploadDir + newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Simpan URL/nama file ke entitas proposal
            proposal.setFileUrl(filePath.toString());

            return proposalRepository.save(proposal);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }
}
package com.skripsi.lppm.service;

import com.skripsi.lppm.model.ProgramStudy;
import com.skripsi.lppm.repository.ProgramStudyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramStudyService {
    private final ProgramStudyRepository programStudyRepository;

    public ProgramStudy createProgramStudy(ProgramStudy programStudy) {
        return programStudyRepository.save(programStudy);
    }

    public List<ProgramStudy> getAllProgramStudies() {
        return programStudyRepository.findAll();
    }

    public ProgramStudy getProgramStudyById(Long id) {
        return programStudyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Program Study not found with id: " + id));
    }

    public ProgramStudy updateProgramStudy(Long id, ProgramStudy updatedProgramStudy) {
        ProgramStudy existing = getProgramStudyById(id);
        existing.setProdiName(updatedProgramStudy.getProdiName());
//        existing.setFaculty(updatedProgramStudy.getFaculty());
        return programStudyRepository.save(existing);
    }

    public void deleteProgramStudy(Long id) {
        programStudyRepository.deleteById(id);
    }
}

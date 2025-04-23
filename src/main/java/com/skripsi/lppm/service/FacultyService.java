package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Faculty;
import com.skripsi.lppm.repository.FacultyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Faculty not found with id: " + id));
    }

    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {
        Faculty existing = getFacultyById(id);
        existing.setFacultyName(updatedFaculty.getFacultyName());
        existing.setProgramStudy(updatedFaculty.getProgramStudy());
        return facultyRepository.save(existing);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }
}

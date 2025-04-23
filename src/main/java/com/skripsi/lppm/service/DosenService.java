package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Dosen;
import com.skripsi.lppm.model.Faculty;
import com.skripsi.lppm.repository.DosenRepository;
import com.skripsi.lppm.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DosenService {

    private final DosenRepository dosenRepository;
    private final FacultyRepository facultyRepository;

    public Dosen createDosen(Dosen dosen) {
        Optional<Faculty> faculty = facultyRepository.findById(dosen.getFaculty().getId());
        faculty.ifPresent(dosen::setFaculty);
        return dosenRepository.save(dosen);
    }

    public List<Dosen> getAllDosens() {
        return dosenRepository.findAll();
    }

    public Dosen getDosenById(Long id) {
        return dosenRepository.findById(id).orElse(null);
    }
}

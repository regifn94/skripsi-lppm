package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Students;
import com.skripsi.lppm.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public ResponseEntity<?> findAll(){
        var userList = studentRepository.findAll();
        return ResponseEntity.ok().body(userList);
    }

    public ResponseEntity<?> createStudent(Students students){
        var saveStudent = studentRepository.save(students);
        return ResponseEntity.ok().body(saveStudent);
    }

    public ResponseEntity<?> deleteStudent(Long id){
        studentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

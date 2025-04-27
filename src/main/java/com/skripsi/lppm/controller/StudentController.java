package com.skripsi.lppm.controller;

import com.skripsi.lppm.model.Students;
import com.skripsi.lppm.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<?> getListStudent(){
        return studentService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Students students){
        return studentService.createStudent(students);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") Long id){
        return studentService.deleteStudent(id);
    }
}

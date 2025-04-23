package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.*;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FacultyRepository facultyRepository;
    private final DosenRepository dosenRepository;
    private final StudentRepository studentRepository;
    private final ProgramStudyRepository programStudyRepository;

    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());

        Set<Role> roleSet = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleSet.add(role);
        }
        user.setRoles(roleSet);
        return userRepository.save(user);
    }

    public ResponseEntity<?> createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserType(request.getUserType());

        Set<Role> roleSet = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleSet.add(role);
        }
        user.setRoles(roleSet);
        var saveUser = userRepository.save(user);
        return ResponseEntity.ok(saveUser);
    }

    public ResponseEntity<?> updateUser(UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserType(request.getUserType());

        Set<Role> updatedRoles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            updatedRoles.add(role);
        }
        user.setRoles(updatedRoles);
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
    public User login(LoginRequest request) {
        return userRepository.findByUsername(request.username)
                .filter(user -> user.getPassword().equals(request.password))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    public ResponseEntity<?> listDataUser(){
        var listUser = userRepository.findAll();
        return ResponseEntity.ok(listUser);
    }

    public ResponseEntity<?> listDataUser(String usernameKeyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> userPage;
        if (usernameKeyword == null || usernameKeyword.isBlank()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByUsernameContainingIgnoreCase(usernameKeyword, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> detailData(Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> ResponseEntity.ok().body(value)).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public ResponseEntity<?> createUserWithProfile(CreateUserWithProfileRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        Set<Role> roleSet = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleSet.add(role);
        }
        user.setRoles(roleSet);

        User savedUser = userRepository.save(user);

        if (request.getRoles().contains("DOSEN") && request.getDosen() != null) {
            CreateUserWithProfileRequest.DosenRequest dosenReq = request.getDosen();
            Faculty faculty = facultyRepository.findById(dosenReq.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));

            Dosen dosen = new Dosen();
            dosen.setName(dosenReq.getName());
            dosen.setNidn(dosenReq.getNidn());
            dosen.setNik(dosenReq.getNik());
            dosen.setFunctionalPosition(dosenReq.getFunctionalPosition());
            dosen.setFaculty(faculty);
            dosen.setUser(savedUser);

            dosenRepository.save(dosen);
        }

        if (request.getRoles().contains("ROLE_STUDENT") && request.getStudent() != null) {
            CreateUserWithProfileRequest.StudentRequest studentReq = request.getStudent();

            Faculty faculty = facultyRepository.findById(studentReq.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));

            ProgramStudy program = programStudyRepository.findById(studentReq.getProgramStudyId())
                    .orElseThrow(() -> new RuntimeException("Program Study not found"));

            Students student = new Students();
            student.setName(studentReq.getName());
            student.setNim(studentReq.getNim());
            student.setFaculty(faculty);
            student.setProgramStudy(program);
            student.setUser(savedUser);

            studentRepository.save(student);
        }

        return ResponseEntity.ok("User created successfully");
    }
}
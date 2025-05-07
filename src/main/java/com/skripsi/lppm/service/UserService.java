package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.*;
import com.skripsi.lppm.model.*;
import com.skripsi.lppm.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FacultyRepository facultyRepository;
    private final DosenRepository dosenRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
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

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public ResponseEntity<?> createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserType(request.getUserType());
        user.setPassword("12345");

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
    public User login(LoginRequest request) {
        var dataUser = userRepository.findByUsername(request.username);
        if(dataUser.isPresent()){
            var user = dataUser.get();
            if(user.getPassword().equals(request.password)){
                return user;
            }
        }
        return null;
    }

    public ResponseEntity<?> findAllUser() {
        List<User> users = userRepository.findAll();

        var userList = users.stream().map(user -> {
            Set<UserDosenFacultyDTO.RoleDTO> roleDTOSet = user.getRoles().stream()
                    .map(role -> new UserDosenFacultyDTO.RoleDTO(role.getId(), role.getName()))
                    .collect(Collectors.toSet());

            UserDosenFacultyDTO.DosenDTO dosenDTO = null;
            UserDosenFacultyDTO.StudentDTO studentDTO = null;
            UserDosenFacultyDTO.FacultyDTO facultyDTO = null;

            if (user.getDosen() != null) {
                dosenDTO = new UserDosenFacultyDTO.DosenDTO(
                        user.getDosen().getId(),
                        user.getDosen().getName(),
                        user.getDosen().getNidn(),
                        user.getDosen().getNik(),
                        user.getDosen().getFunctionalPosition()
                );
                if (user.getDosen().getFaculty() != null) {
                    facultyDTO = new UserDosenFacultyDTO.FacultyDTO(
                            user.getDosen().getFaculty().getId(),
                            user.getDosen().getFaculty().getFacultyName()
                    );
                }
            }

            if (user.getStudent() != null) {
                studentDTO = new UserDosenFacultyDTO.StudentDTO(
                        user.getStudent().getId(),
                        user.getStudent().getName(),
                        user.getStudent().getNim()
                );
                if (user.getStudent().getFaculty() != null) {
                    facultyDTO = new UserDosenFacultyDTO.FacultyDTO(
                            user.getStudent().getFaculty().getId(),
                            user.getStudent().getFaculty().getFacultyName()
                    );
                }
            }

            return new UserDosenFacultyDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getUserType(),
                    roleDTOSet,
                    dosenDTO,
                    studentDTO,
                    facultyDTO
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(userList);
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

    @Transactional
    public void deleteUser(Long id) {
        notificationRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public ResponseEntity<?> createUserWithProfile(CreateUserWithProfileRequest request) {
        var user = new User();
        user.setPassword("12345");
        user.setUserType(request.getUserType());
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

        if (request.getUserType().contains("DOSEN_STAFF") && request.getDosen() != null) {
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

        if (request.getUserType().contains("STUDENT") && request.getStudent() != null) {
            CreateUserWithProfileRequest.StudentRequest studentReq = request.getStudent();

            Faculty faculty = facultyRepository.findById(studentReq.getFacultyId())
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));

//            ProgramStudy program = programStudyRepository.findById(studentReq.getProgramStudyId())
//                    .orElseThrow(() -> new RuntimeException("Program Study not found"));

            Students student = new Students();
            student.setName(studentReq.getName());
            student.setNim(studentReq.getNim());
            student.setFaculty(faculty);
//            student.setProgramStudy(program);
            student.setUser(savedUser);

            studentRepository.save(student);
        }

        return ResponseEntity.ok("User created successfully");
    }

    public ResponseEntity<?> updateUserWithProfile(CreateUserWithProfileRequest request) {
        try {
            if (request.getId() == null) {
                throw new RuntimeException("User ID is required for update");
            }

            User user = userRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getId()));

            user.setUserType(request.getUserType());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());

            // Update Roles
            Set<Role> roleSet = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roleSet.add(role);
            }
            user.setRoles(roleSet);

            User savedUser = userRepository.save(user);

            // Update Dosen Profile if exists
            if (request.getUserType().contains("DOSEN_STAFF") && request.getDosen() != null) {
                CreateUserWithProfileRequest.DosenRequest dosenReq = request.getDosen();
                var userFaculty = dosenRepository.findByUserId(request.getId());
                Faculty faculty = facultyRepository.findById(userFaculty.get().getFaculty().getId())
                        .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + dosenReq.getFacultyId()));

                Dosen dosen = dosenRepository.findByUserId(savedUser.getId())
                        .orElse(new Dosen()); // kalau belum ada Dosen, buat baru

                dosen.setName(dosenReq.getName());
                dosen.setNidn(dosenReq.getNidn());
                dosen.setNik(dosenReq.getNik());
                dosen.setFunctionalPosition(dosenReq.getFunctionalPosition());
                dosen.setFaculty(faculty);
                dosen.setUser(savedUser);

                dosenRepository.save(dosen);
            }

            // Update Student Profile if exists
            if (request.getUserType().contains("STUDENT") && request.getStudent() != null) {
                CreateUserWithProfileRequest.StudentRequest studentReq = request.getStudent();

                Faculty faculty = facultyRepository.findById(studentReq.getFacultyId())
                        .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + studentReq.getFacultyId()));

                ProgramStudy program = programStudyRepository.findById(studentReq.getProgramStudyId())
                        .orElseThrow(() -> new RuntimeException("Program Study not found with ID: " + studentReq.getProgramStudyId()));

                Students student = studentRepository.findByUserId(savedUser.getId())
                        .orElse(new Students()); // kalau belum ada Student, buat baru

                student.setName(studentReq.getName());
                student.setNim(studentReq.getNim());
                student.setFaculty(faculty);
//            student.setProgramStudy(program);
                student.setUser(savedUser);

                studentRepository.save(student);
            }

            return ResponseEntity.ok("User updated successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error : " + e.getMessage());
        }
    }

    public ResponseEntity<?> getUserWithRoleReviewer(Long dekanId){
        try {
            var dosen = dosenRepository.findById(dekanId);
            if (dosen.isPresent()) {
                var faculty = dosen.get().getFaculty().getId();
                var userReviewers = userRepository.findByRoleAndFaculty("REVIEWER", faculty);
                return ResponseEntity.status(HttpStatus.OK).body(userReviewers);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error " + e.getMessage());
        }
    }
}
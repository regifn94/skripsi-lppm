package com.skripsi.lppm.controller;

import com.skripsi.lppm.dto.CreateUserRequest;
import com.skripsi.lppm.dto.CreateUserWithProfileRequest;
import com.skripsi.lppm.dto.UpdateUserRequest;
import com.skripsi.lppm.dto.UserDosenFacultyDTO;
import com.skripsi.lppm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> listUsers(){
        return userService.findAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @PostMapping
//    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest){
//        return userService.createUser(createUserRequest);
//    }
    @PostMapping
    public ResponseEntity<?> createWithProfile(@RequestBody CreateUserWithProfileRequest request) {
        return userService.createUserWithProfile(request);
    }



    @PutMapping
    public ResponseEntity<?> updateWithProfile(@RequestBody CreateUserWithProfileRequest request) {
        return userService.updateUserWithProfile(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviewer/{id}")
    public ResponseEntity<?> getUserWithRoleReviewer(@PathVariable Long id){
        return userService.getUserWithRoleReviewer(id);
    }
}

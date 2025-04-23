package com.skripsi.lppm.controller;

import com.skripsi.lppm.dto.CreateUserRequest;
import com.skripsi.lppm.dto.CreateUserWithProfileRequest;
import com.skripsi.lppm.dto.UpdateUserRequest;
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
        return userService.listDataUser();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest){
        return userService.createUser(createUserRequest);
    }
    @PostMapping("/create-with-profile")
    public ResponseEntity<?> createWithProfile(@RequestBody CreateUserWithProfileRequest request) {
        return userService.createUserWithProfile(request);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    public ResponseEntity<?> listUsers(
//            @RequestParam(required = false) String username,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return userService.listDataUser(username, page, size);
//    }
}

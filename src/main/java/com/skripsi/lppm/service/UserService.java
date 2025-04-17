package com.skripsi.lppm.service;

import com.skripsi.lppm.dto.LoginRequest;
import com.skripsi.lppm.dto.RegisterRequest;
import com.skripsi.lppm.model.Role;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.repository.RoleRepository;
import com.skripsi.lppm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username);
        user.setPassword(request.password); // You should hash this in production!

        Set<Role> roleSet = new HashSet<>();
        for (String roleName : request.roles) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleSet.add(role);
        }
        user.setRoles(roleSet);
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        return userRepository.findByUsername(request.username)
                .filter(user -> user.getPassword().equals(request.password))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}

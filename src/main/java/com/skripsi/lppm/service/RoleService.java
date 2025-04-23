package com.skripsi.lppm.service;

import com.skripsi.lppm.model.Role;
import com.skripsi.lppm.model.User;
import com.skripsi.lppm.repository.RoleRepository;
import com.skripsi.lppm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<Role> getListRole(){
        return roleRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> deleteRole(Long id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }

        Role role = optionalRole.get();

        // Hapus relasi dari semua user yang memiliki role ini
        List<User> usersWithRole = userRepository.findAllByRolesContaining(role);
        for (User user : usersWithRole) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }

        roleRepository.delete(role);

        return ResponseEntity.ok("Role deleted successfully");
    }

}

package com.skripsi.lppm.component;

import com.skripsi.lppm.model.Role;
import com.skripsi.lppm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> roles = List.of(
                "ADMIN",
                "KETUA_LPPM",
                "KETUA_PENELITIAN_FAKULTAS",
                "DEKAN",
                "DOSEN",
                "REVIEWER",
                "KANTOR_KEUANGAN",
                "KANTOR_AKADEMIK"
        );

        for (String roleName : roles) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                System.out.println("Seeding role: " + roleName);
                return roleRepository.save(new Role(roleName));
            });
        }
    }
}

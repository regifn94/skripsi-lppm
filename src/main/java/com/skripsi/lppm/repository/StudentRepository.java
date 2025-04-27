package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Students, Long> {
    Optional<Students> findByUserId(Long id);
}

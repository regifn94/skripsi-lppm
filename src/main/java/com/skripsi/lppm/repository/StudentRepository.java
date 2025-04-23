package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Students, Long> {
}

package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Dosen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DosenRepository extends JpaRepository<Dosen, Long> {
    List<Dosen> findByIdIn(List<Long> ids);

    Optional<Dosen> findByUserId(Long userId);
}

package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("SELECT p FROM Proposal p WHERE p.ketuaPeneliti.id = :userId")
    List<Proposal> findByKetuaPenelitiId(@Param("userId") Long userId);
}
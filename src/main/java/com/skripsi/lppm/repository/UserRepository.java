package com.skripsi.lppm.repository;

import com.skripsi.lppm.model.Role;
import com.skripsi.lppm.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    List<User> findAllByRolesContaining(Role role);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoles_Name(@Param("roleName") String roleName);

    List<User> findAllByRoleName(String ketuaPenelitianFakultas);

    List<User> findAllByUserIdIn(List<Long> userId);
}
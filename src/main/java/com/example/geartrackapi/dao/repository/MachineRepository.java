package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<Machine, UUID> {
    Optional<Machine> findByIdAndHiddenFalse(UUID id);
    List<Machine> findByUserIdAndHiddenFalse(UUID userId);
    Page<Machine> findByUserIdAndHiddenFalse(UUID userId, Pageable pageable);
}
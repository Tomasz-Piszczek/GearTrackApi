package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Tool;
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
public interface ToolRepository extends JpaRepository<Tool, UUID> {
    Optional<Tool> findByIdAndOrganizationIdAndHiddenFalse(UUID id, UUID organizationId);
    List<Tool> findByOrganizationIdAndHiddenFalse(UUID organizationId);
    Page<Tool> findByOrganizationIdAndHiddenFalse(UUID organizationId, Pageable pageable);
}
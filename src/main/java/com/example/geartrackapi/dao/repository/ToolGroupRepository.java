package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.ToolGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ToolGroupRepository extends JpaRepository<ToolGroup, UUID> {
    List<ToolGroup> findByOrganizationIdAndHiddenFalse(UUID organizationId);
    Optional<ToolGroup> findByIdAndOrganizationIdAndHiddenFalse(UUID id, UUID organizationId);
}

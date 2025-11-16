package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToolRepository extends JpaRepository<Tool, UUID> {
    List<Tool> findByUserId(UUID userId);
}
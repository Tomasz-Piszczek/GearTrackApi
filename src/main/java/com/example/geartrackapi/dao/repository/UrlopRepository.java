package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlopRepository extends JpaRepository<Urlop, UUID> {

    List<Urlop> findByEmployeeIdAndOrganizationIdAndHiddenFalse(UUID employeeId, UUID organizationId);

    List<Urlop> findByOrganizationIdAndHiddenFalse(UUID organizationId);

    Optional<Urlop> findByIdAndHiddenFalse(UUID id);

    List<Urlop> findByOrganizationIdAndStatusAndHiddenFalse(UUID organizationId, UrlopStatus status);
}

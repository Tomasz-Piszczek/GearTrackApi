package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.EmployeeUrlopDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeUrlopDaysRepository extends JpaRepository<EmployeeUrlopDays, UUID> {

    List<EmployeeUrlopDays> findByEmployeeIdAndOrganizationIdAndHiddenFalse(UUID employeeId, UUID organizationId);

    Optional<EmployeeUrlopDays> findByEmployeeIdAndYearAndOrganizationIdAndHiddenFalse(
            UUID employeeId, Integer year, UUID organizationId);

    List<EmployeeUrlopDays> findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
            UUID employeeId, List<Integer> years, UUID organizationId);
}

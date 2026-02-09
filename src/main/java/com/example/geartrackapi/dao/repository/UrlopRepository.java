package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlopRepository extends JpaRepository<Urlop, UUID> {

    List<Urlop> findByEmployeeIdAndOrganizationIdAndHiddenFalse(UUID employeeId, UUID organizationId);

    List<Urlop> findByOrganizationIdAndHiddenFalse(UUID organizationId);

    Optional<Urlop> findByIdAndHiddenFalse(UUID id);

    List<Urlop> findByOrganizationIdAndStatusAndHiddenFalse(UUID organizationId, UrlopStatus status);

    @Query("SELECT u FROM Urlop u WHERE CONCAT(u.employee.firstName, ' ', u.employee.lastName) IN :names " +
           "AND u.status = 'ACCEPTED' AND u.hidden = false " +
           "AND u.fromDate <= :endDate AND u.toDate >= :startDate " +
           "AND u.organizationId = :orgId")
    List<Urlop> findAcceptedUrlopyByEmployeeNamesAndDateRange(
        @Param("names") List<String> employeeNames,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("orgId") UUID organizationId);
}

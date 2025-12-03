package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Employee;
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
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByIdAndHiddenFalse(UUID id);
    List<Employee> findByUserIdAndHiddenFalse(UUID userId);
    Page<Employee> findByUserIdAndHiddenFalse(UUID userId, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE e.userId = :userId AND e.hidden = false AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Employee> findByUserIdAndNameContaining(@Param("userId") UUID userId, @Param("search") String search, Pageable pageable);
}
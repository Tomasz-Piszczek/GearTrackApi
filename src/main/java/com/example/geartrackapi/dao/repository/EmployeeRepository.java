package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByUserId(UUID userId);
    Page<Employee> findByUserId(UUID userId, Pageable pageable);
    
    List<Employee> findByUserIdAndFirstNameContainingIgnoreCase(UUID userId, String firstName);
    Page<Employee> findByUserIdAndFirstNameContainingIgnoreCase(UUID userId, String firstName, Pageable pageable);
    
    List<Employee> findByUserIdAndLastNameContainingIgnoreCase(UUID userId, String lastName);
    Page<Employee> findByUserIdAndLastNameContainingIgnoreCase(UUID userId, String lastName, Pageable pageable);
}
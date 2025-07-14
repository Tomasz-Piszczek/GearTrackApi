package com.example.geartrackapi.dao.repository;

import com.example.geartrackapi.dao.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByUserId(UUID userId);
    List<Employee> findByUserIdAndFirstNameContainingIgnoreCase(UUID userId, String firstName);
    List<Employee> findByUserIdAndLastNameContainingIgnoreCase(UUID userId, String lastName);
}
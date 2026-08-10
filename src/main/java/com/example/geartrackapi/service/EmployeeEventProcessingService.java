package com.example.geartrackapi.service;

import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.infrastructure.events.model.EmployeeChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventProcessingService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public void processEmployeeChangeEvent(EmployeeChangeEvent event) {
        log.info("Processing employee change event: eventId={}, eventType={}, timestamp={}", 
                event.getEventId(), event.getEventType(), event.getTimestamp());

        if (event.getEmployees() == null || event.getEmployees().isEmpty()) {
            log.warn("No employee data in event {}", event.getEventId());
            return;
        }

        processEmployeeData(event.getEmployees());
    }

    private void processEmployeeData(List<EmployeeChangeEvent.EmployeeData> employees) {
        for (EmployeeChangeEvent.EmployeeData employeeData : employees) {
            updateOrCreateEmployee(employeeData);
        }
        
    }
        //todo FIX hardcoded org ID
    private void updateOrCreateEmployee(EmployeeChangeEvent.EmployeeData employeeData) {
        UUID organizationId = UUID.fromString("e0318ab7-3ca7-4787-8165-f14dc5fe465d");
        
        Optional<Employee> existingEmployee = employeeRepository.findByBiEmployeeIdAndOrganizationId(
                employeeData.getId(), organizationId);

        if (existingEmployee.isPresent()) {
            updateExistingEmployee(existingEmployee.get(), employeeData);
        } else {
            createNewEmployee(employeeData, organizationId);
        }
    }

    private void updateExistingEmployee(Employee employee, EmployeeChangeEvent.EmployeeData employeeData) {
        boolean needsUpdate = false;
        
        String[] nameParts = parseEmployeeName(employeeData.getCode());
        if (!nameParts[0].equals(employee.getFirstName())) {
            employee.setFirstName(nameParts[0]);
            needsUpdate = true;
        }
        
        if (!nameParts[1].equals(employee.getLastName())) {
            employee.setLastName(nameParts[1]);
            needsUpdate = true;
        }

        if (needsUpdate) {
            employeeRepository.save(employee);
            log.info("Updated employee with BI ID: {}", employeeData.getId());
        }
    }

    private void createNewEmployee(EmployeeChangeEvent.EmployeeData employeeData, UUID organizationId) {
        String[] nameParts = parseEmployeeName(employeeData.getCode());

        Employee newEmployee = Employee.builder()
                .biEmployeeId(employeeData.getId())
                .firstName(nameParts[0])
                .lastName(nameParts[1])
                .organizationId(organizationId)
                .hidden(false)
                .build();
        
        employeeRepository.save(newEmployee);
        log.info("Created new employee with BI ID: {}", employeeData.getId());
    }

    private String[] parseEmployeeName(String employeeCode) {
        if (employeeCode == null || employeeCode.trim().isEmpty()) {
            return new String[]{"Unknown", "Employee"};
        }
        
        String[] parts = employeeCode.trim().split("\\s+");
        if (parts.length >= 2) {
            return new String[]{parts[0], parts[1]};
        } else {
            return new String[]{parts[0], ""};
        }
    }

}
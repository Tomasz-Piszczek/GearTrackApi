package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.dao.model.EmployeeTool;
import com.example.geartrackapi.dao.model.Tool;
import com.example.geartrackapi.dao.repository.EmployeeToolRepository;
import com.example.geartrackapi.dao.repository.ToolRepository;
import com.example.geartrackapi.mapper.EmployeeToolMapper;
import com.example.geartrackapi.mapper.ToolMapper;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolCrudService {
    
    private final ToolRepository toolRepository;
    private final EmployeeToolRepository employeeToolRepository;
    private final ToolMapper toolMapper;
    private final EmployeeToolMapper employeeToolMapper;
    
    public List<ToolDto> findAllTools() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return toolRepository.findByOrganizationIdAndHiddenFalse(organizationId)
                .stream()
                .map(tool -> {
                    return toolMapper.toDto(tool, employeeToolRepository.getAvailableQuantityByOrganizationIdAndToolId(organizationId, tool.getId()));
                })
                .collect(Collectors.toList());
    }
    
    public ToolDto createTool(ToolDto toolDto) {
        Tool tool = toolMapper.toEntity(toolDto);
        return toolMapper.toDto(toolRepository.save(tool));
    }
    
    public ToolDto updateTool(ToolDto toolDto) {
        Tool existing = toolRepository.findByIdAndOrganizationIdAndHiddenFalse(toolDto.getUuid(), SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + toolDto.getUuid()));
        Tool updated = toolMapper.updateEntity(existing, toolDto);
        return toolMapper.toDto(toolRepository.save(updated));
    }
    
    public void deleteTool(UUID id) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        Tool tool = toolRepository.findByIdAndOrganizationIdAndHiddenFalse(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + id));

        tool.setHidden(true);
        toolRepository.save(tool);

        List<EmployeeTool> assignments = employeeToolRepository.findByOrganizationIdAndToolId(organizationId, id);
        assignments.forEach(assignment -> assignment.setHidden(true));
        employeeToolRepository.saveAll(assignments);

        log.info("[deleteTool] Soft deleted tool {} and {} assignments", id, assignments.size());
    }
    
    public AssignToolDto assignToolToEmployee(UUID toolId, UUID employeeId, AssignToolDto assignDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        Tool tool = toolRepository.findByIdAndOrganizationIdAndHiddenFalse(toolId, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + toolId));
        
        Integer totalAssigned = employeeToolRepository.getTotalAssignedQuantityByOrganizationIdAndToolId(organizationId, toolId);
        int availableQuantity = Math.max(0, tool.getQuantity() - totalAssigned);
        
        if (assignDto.getQuantity() > availableQuantity) {
            throw new IllegalArgumentException(
                String.format("Insufficient quantity. Available: %d, Requested: %d", 
                    availableQuantity, assignDto.getQuantity()));
        }

        EmployeeTool employeeTool = employeeToolMapper.toEntity(toolId, employeeId, assignDto);
        return employeeToolMapper.toAssignToolDto(employeeToolRepository.save(employeeTool));
    }
    
    public void unassignToolFromEmployee(UUID toolId, UUID employeeId, Integer quantity) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        List<EmployeeTool> matchingAssignments = employeeToolRepository.findByOrganizationIdAndEmployeeId(organizationId, employeeId)
                .stream()
                .filter(et -> et.getToolId().equals(toolId) && !et.getHidden())
                .collect(Collectors.toList());

        if (matchingAssignments.isEmpty()) {
            throw new EntityNotFoundException("No tool assignment found for tool " + toolId + " and employee " + employeeId);
        }

        EmployeeTool assignment = matchingAssignments.get(0);

        if (quantity >= assignment.getQuantity()) {
            assignment.setHidden(true);
            employeeToolRepository.save(assignment);
            log.info("[unassignToolFromEmployee] Soft deleted assignment {} (removed {} of {} items)",
                    assignment.getId(), quantity, assignment.getQuantity());
        } else {
            assignment.setQuantity(assignment.getQuantity() - quantity);
            employeeToolRepository.save(assignment);
            log.info("[unassignToolFromEmployee] Reduced quantity for assignment {} from {} to {}",
                    assignment.getId(), assignment.getQuantity() + quantity, assignment.getQuantity());
        }
    }
    
    public List<AssignToolDto> getToolsAssignedToEmployee(UUID employeeId) {
        return employeeToolRepository.findByOrganizationIdAndEmployeeId(SecurityUtils.getCurrentOrganizationId(), employeeId)
                .stream()
                .map(employeeToolMapper::toAssignToolDto)
                .collect(Collectors.toList());
    }
    
    public List<AssignToolDto> getEmployeesAssignedToTool(UUID toolId) {
        return employeeToolRepository.findByOrganizationIdAndToolId(SecurityUtils.getCurrentOrganizationId(), toolId)
                .stream()
                .map(employeeToolMapper::toAssignToolDto)
                .collect(Collectors.toList());
    }

    public AssignToolDto markToolAsUsed(UUID employeeToolId) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        EmployeeTool employeeTool = employeeToolRepository.findById(employeeToolId)
                .orElseThrow(() -> new EntityNotFoundException("EmployeeTool not found with UUID: " + employeeToolId));

        if (!employeeTool.getOrganizationId().equals(organizationId)) {
            throw new IllegalArgumentException("EmployeeTool does not belong to current organization");
        }

        employeeTool.setUsedAt(LocalDate.now());
        return employeeToolMapper.toAssignToolDto(employeeToolRepository.save(employeeTool));
    }


}
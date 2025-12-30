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
        Tool tool = toolRepository.findByIdAndOrganizationIdAndHiddenFalse(id, SecurityUtils.getCurrentOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + id));
        tool.setHidden(true);
        toolRepository.save(tool);
    }
    
    public AssignToolDto assignToolToEmployee(AssignToolDto assignDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        Tool tool = toolRepository.findByIdAndOrganizationIdAndHiddenFalse(assignDto.getToolId(), organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + assignDto.getToolId()));
        
        Integer totalAssigned = employeeToolRepository.getTotalAssignedQuantityByOrganizationIdAndToolId(organizationId, assignDto.getToolId());
        int availableQuantity = Math.max(0, tool.getQuantity() - totalAssigned);
        
        if (assignDto.getQuantity() > availableQuantity) {
            throw new IllegalArgumentException(
                String.format("Insufficient quantity. Available: %d, Requested: %d", 
                    availableQuantity, assignDto.getQuantity()));
        }

        EmployeeTool employeeTool = employeeToolMapper.toEntity(assignDto);
        return employeeToolMapper.toAssignToolDto(employeeToolRepository.save(employeeTool));
    }
    
    public void unassignToolFromEmployee(AssignToolDto assignDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<EmployeeTool> matchingAssignments = employeeToolRepository.findByOrganizationIdAndEmployeeId(organizationId, assignDto.getEmployeeId())
                .stream()
                .filter(et -> et.getToolId().equals(assignDto.getToolId()) && 
                             et.getCondition().equals(assignDto.getCondition()))
                .collect(Collectors.toList());
        

        
        EmployeeTool assignmentToModify = matchingAssignments.stream()
                .filter(et -> et.getQuantity() >= assignDto.getQuantity())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Insufficient quantity to remove. Requested: " + assignDto.getQuantity()));
        
        if (assignmentToModify.getQuantity().equals(assignDto.getQuantity())) {
            employeeToolRepository.delete(assignmentToModify);
        } else {
            assignmentToModify.setQuantity(assignmentToModify.getQuantity() - assignDto.getQuantity());
            employeeToolRepository.save(assignmentToModify);
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
    
    
}
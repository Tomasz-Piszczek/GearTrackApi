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
        log.debug("[findAllTools] Getting all tools for authenticated user");
        UUID userId = SecurityUtils.authenticatedUserId();
        return toolRepository.findByUserId(userId)
                .stream()
                .map(tool -> {
                    return toolMapper.toDto(tool, employeeToolRepository.getAvailableQuantityByUserIdAndToolId(userId, tool.getId()));
                })
                .collect(Collectors.toList());
    }
    
    public ToolDto createTool(ToolDto toolDto) {
        log.debug("[createTool] Creating tool with name: {}", toolDto.getName());
        Tool tool = toolMapper.toEntity(toolDto);
        tool.setUserId(SecurityUtils.authenticatedUserId());
        return toolMapper.toDto(toolRepository.save(tool));
    }
    
    public ToolDto updateTool(ToolDto toolDto) {
        log.debug("[updateTool] Updating tool with UUID: {}", toolDto.getUuid());
        Tool tool = toolRepository.findById(toolDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + toolDto.getUuid()));
        toolMapper.updateEntity(tool, toolDto);
        return toolMapper.toDto(toolRepository.save(tool));
    }
    
    public void deleteTool(UUID id) {
        log.debug("[deleteTool] Deleting tool with UUID: {}", id);
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + id));
        toolRepository.delete(tool);
    }
    
    public AssignToolDto assignToolToEmployee(AssignToolDto assignDto) {
        log.debug("[assignToolToEmployee] Assigning tool UUID: {} to employee UUID: {}", assignDto.getToolId(), assignDto.getEmployeeId());

        UUID userId = SecurityUtils.authenticatedUserId();
        Tool tool = toolRepository.findById(assignDto.getToolId())
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + assignDto.getToolId()));
        
        Integer totalAssigned = employeeToolRepository.getTotalAssignedQuantityByUserIdAndToolId(userId, assignDto.getToolId());
        int availableQuantity = Math.max(0, tool.getQuantity() - totalAssigned);
        
        if (assignDto.getQuantity() > availableQuantity) {
            throw new IllegalArgumentException(
                String.format("Insufficient quantity. Available: %d, Requested: %d", 
                    availableQuantity, assignDto.getQuantity()));
        }

        EmployeeTool employeeTool = employeeToolMapper.toEntity(assignDto);
        employeeTool.setAssignedAt(assignDto.getAssignedAt() != null ? assignDto.getAssignedAt() : LocalDate.now());
        employeeTool.setUserId(SecurityUtils.authenticatedUserId());
        return employeeToolMapper.toAssignToolDto(employeeToolRepository.save(employeeTool));
    }
    
    public void unassignToolFromEmployee(AssignToolDto assignDto) {
        log.debug("[unassignToolFromEmployee] Unassigning tool UUID: {} from employee UUID: {}", assignDto.getToolId(), assignDto.getEmployeeId());
        List<EmployeeTool> employeeTools = employeeToolRepository.findByUserIdAndEmployeeId(SecurityUtils.authenticatedUserId(), assignDto.getEmployeeId())
                .stream()
                .filter(et -> et.getToolId().equals(assignDto.getToolId()))
                .collect(Collectors.toList());
        if (employeeTools.isEmpty()) {
            throw new EntityNotFoundException("No tool assignment found");
        }
        employeeToolRepository.deleteAll(employeeTools);
    }
    
    public List<AssignToolDto> getToolsAssignedToEmployee(UUID employeeId) {
        log.debug("[getToolsAssignedToEmployee] Getting tools assigned to employee UUID: {}", employeeId);
        return employeeToolRepository.findByUserIdAndEmployeeId(SecurityUtils.authenticatedUserId(), employeeId)
                .stream()
                .map(employeeToolMapper::toAssignToolDto)
                .collect(Collectors.toList());
    }
    
    
}
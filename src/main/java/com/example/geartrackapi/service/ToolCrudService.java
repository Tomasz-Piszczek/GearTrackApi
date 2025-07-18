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
                .map(toolMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public ToolDto createTool(ToolDto toolDto) {
        log.debug("[createTool] Creating tool with name: {}", toolDto.getName());
        UUID userId = SecurityUtils.authenticatedUserId();
        Tool tool = toolMapper.toEntity(toolDto);
        tool.setUserId(userId);
        Tool savedTool = toolRepository.save(tool);
        return toolMapper.toDto(savedTool);
    }
    
    public ToolDto updateTool(ToolDto toolDto) {
        log.debug("[updateTool] Updating tool with UUID: {}", toolDto.getUuid());
        Tool tool = toolRepository.findById(toolDto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Tool not found with UUID: " + toolDto.getUuid()));

        toolMapper.updateEntity(tool, toolDto);
        Tool savedTool = toolRepository.save(tool);
        return toolMapper.toDto(savedTool);
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

        EmployeeTool employeeTool = employeeToolMapper.toEntity(assignDto);
        employeeTool.setAssignedAt(assignDto.getAssignedAt() != null ? assignDto.getAssignedAt() : LocalDate.now());
        employeeTool.setUserId(userId);
        
        EmployeeTool savedEmployeeTool = employeeToolRepository.save(employeeTool);
        return employeeToolMapper.toAssignToolDto(savedEmployeeTool);
    }
    
    public void unassignToolFromEmployee(AssignToolDto assignDto) {
        log.debug("[unassignToolFromEmployee] Unassigning tool UUID: {} from employee UUID: {}", assignDto.getToolId(), assignDto.getEmployeeId());
        UUID userId = SecurityUtils.authenticatedUserId();
        List<EmployeeTool> employeeTools = employeeToolRepository.findByUserIdAndEmployeeId(userId, assignDto.getEmployeeId())
                .stream()
                .filter(et -> et.getToolId().equals(assignDto.getToolId()))
                .collect(Collectors.toList());

        employeeToolRepository.deleteAll(employeeTools);
    }
    
    public List<AssignToolDto> getToolsAssignedToEmployee(UUID employeeId) {
        log.debug("[getToolsAssignedToEmployee] Getting tools assigned to employee UUID: {}", employeeId);
        UUID userId = SecurityUtils.authenticatedUserId();
        List<EmployeeTool> employeeTools = employeeToolRepository.findByUserIdAndEmployeeId(userId, employeeId);
        
        return employeeTools.stream()
                .map(employeeToolMapper::toAssignToolDto)
                .collect(Collectors.toList());
    }
}
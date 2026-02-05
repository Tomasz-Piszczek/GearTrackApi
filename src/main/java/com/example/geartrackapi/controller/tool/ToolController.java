package com.example.geartrackapi.controller.tool;

import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.service.ToolCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ToolController {
    
    private final ToolCrudService toolCrudService;
    
    @GetMapping
    public List<ToolDto> findAllTools() {
        log.info("[findAllTools] Getting all tools");
        return toolCrudService.findAllTools();
    }
    
    @PostMapping
    public ToolDto createTool(@RequestBody ToolDto toolDto) {
        log.info("[createTool] Creating tool with name: {}", toolDto.getName());
        return toolCrudService.createTool(toolDto);
    }
    
    @PutMapping
    public ToolDto updateTool(@RequestBody ToolDto toolDto) {
        log.info("[updateTool] Updating tool with UUID: {}", toolDto.getUuid());
        return toolCrudService.updateTool(toolDto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteTool(@PathVariable UUID id) {
        log.info("[deleteTool] Deleting tool with UUID: {}", id);
        toolCrudService.deleteTool(id);
    }
    
    @PostMapping("/assign/{toolId}/{employeeId}")
    public AssignToolDto assignToolToEmployee(@PathVariable UUID toolId, @PathVariable UUID employeeId, @RequestBody AssignToolDto assignDto) {
        log.info("[assignToolToEmployee] Assigning tool UUID: {} to employee UUID: {}", toolId, employeeId);
        return toolCrudService.assignToolToEmployee(toolId, employeeId, assignDto);
    }
    
    @DeleteMapping("/unassign/{toolId}/{employeeId}")
    public void unassignToolFromEmployee(@PathVariable UUID toolId, @PathVariable UUID employeeId, @RequestParam Integer quantity) {
        log.info("[unassignToolFromEmployee] Unassigning {} units of tool UUID: {} from employee UUID: {}", quantity, toolId, employeeId);
        toolCrudService.unassignToolFromEmployee(toolId, employeeId, quantity);
    }
    
    @GetMapping("/{toolId}/employees")
    public List<AssignToolDto> getEmployeesAssignedToTool(@PathVariable UUID toolId) {
        log.info("[getEmployeesAssignedToTool] Getting employees assigned to tool UUID: {}", toolId);
        return toolCrudService.getEmployeesAssignedToTool(toolId);
    }

    @PostMapping("/mark-used/{employeeToolId}")
    public AssignToolDto markToolAsUsed(@PathVariable UUID employeeToolId) {
        log.info("[markToolAsUsed] Marking tool as used for EmployeeTool UUID: {}", employeeToolId);
        return toolCrudService.markToolAsUsed(employeeToolId);
    }

}
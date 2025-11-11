package com.example.geartrackapi.controller.tool;

import com.example.geartrackapi.controller.tool.dto.AssignToolDto;
import com.example.geartrackapi.controller.tool.dto.ToolDto;
import com.example.geartrackapi.controller.tool.dto.ToolQuantityDto;
import com.example.geartrackapi.service.ToolCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
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
    
    @PostMapping("/assign")
    public AssignToolDto assignToolToEmployee(@RequestBody AssignToolDto assignDto) {
        log.info("[assignToolToEmployee] Assigning tool UUID: {} to employee UUID: {}", assignDto.getToolId(), assignDto.getEmployeeId());
        return toolCrudService.assignToolToEmployee(assignDto);
    }
    
    @DeleteMapping("/unassign")
    public void unassignToolFromEmployee(@RequestBody AssignToolDto assignDto) {
        log.info("[unassignToolFromEmployee] Unassigning tool UUID: {} from employee UUID: {}", assignDto.getToolId(), assignDto.getEmployeeId());
        toolCrudService.unassignToolFromEmployee(assignDto);
    }
    
    @GetMapping("/{toolId}/available-quantity")
    public ToolQuantityDto getAvailableQuantity(@PathVariable UUID toolId) {
        log.info("[getAvailableQuantity] Getting available quantity for tool UUID: {}", toolId);
        return toolCrudService.getToolQuantity(toolId);
    }
}
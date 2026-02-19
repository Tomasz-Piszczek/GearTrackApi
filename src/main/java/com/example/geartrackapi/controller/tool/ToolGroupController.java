package com.example.geartrackapi.controller.tool;

import com.example.geartrackapi.controller.tool.dto.ToolGroupDto;
import com.example.geartrackapi.service.ToolGroupCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tool-groups")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class ToolGroupController {

    private final ToolGroupCrudService toolGroupCrudService;

    @GetMapping
    public List<ToolGroupDto> findAllToolGroups() {
        log.info("[findAllToolGroups] Getting all tool groups");
        return toolGroupCrudService.findAllToolGroups();
    }

    @PostMapping
    public ToolGroupDto createToolGroup(@RequestBody ToolGroupDto toolGroupDto) {
        log.info("[createToolGroup] Creating tool group with name: {}", toolGroupDto.getName());
        return toolGroupCrudService.createToolGroup(toolGroupDto);
    }

    @PutMapping
    public ToolGroupDto updateToolGroup(@RequestBody ToolGroupDto toolGroupDto) {
        log.info("[updateToolGroup] Updating tool group with UUID: {}", toolGroupDto.getUuid());
        return toolGroupCrudService.updateToolGroup(toolGroupDto);
    }

    @DeleteMapping("/{id}")
    public void deleteToolGroup(@PathVariable UUID id) {
        log.info("[deleteToolGroup] Deleting tool group with UUID: {}", id);
        toolGroupCrudService.deleteToolGroup(id);
    }
}

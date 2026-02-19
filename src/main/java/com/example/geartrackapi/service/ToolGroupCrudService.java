package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.tool.dto.ToolGroupDto;
import com.example.geartrackapi.dao.model.Tool;
import com.example.geartrackapi.dao.model.ToolGroup;
import com.example.geartrackapi.dao.repository.ToolGroupRepository;
import com.example.geartrackapi.dao.repository.ToolRepository;
import com.example.geartrackapi.mapper.ToolGroupMapper;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolGroupCrudService {

    private final ToolGroupRepository toolGroupRepository;
    private final ToolRepository toolRepository;
    private final ToolGroupMapper toolGroupMapper;

    @Transactional(readOnly = true)
    public List<ToolGroupDto> findAllToolGroups() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return toolGroupRepository.findByOrganizationIdAndHiddenFalse(organizationId)
                .stream()
                .map(toolGroupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ToolGroupDto createToolGroup(ToolGroupDto toolGroupDto) {
        ToolGroup toolGroup = toolGroupMapper.toEntity(toolGroupDto);
        return toolGroupMapper.toDto(toolGroupRepository.save(toolGroup));
    }

    @Transactional
    public ToolGroupDto updateToolGroup(ToolGroupDto toolGroupDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        ToolGroup existing = toolGroupRepository.findByIdAndOrganizationIdAndHiddenFalse(
                toolGroupDto.getUuid(), organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Grupa narzędzi nie znaleziona: " + toolGroupDto.getUuid()));
        toolGroupMapper.updateEntity(existing, toolGroupDto);
        return toolGroupMapper.toDto(toolGroupRepository.save(existing));
    }

    @Transactional
    public void deleteToolGroup(UUID id) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        ToolGroup toolGroup = toolGroupRepository.findByIdAndOrganizationIdAndHiddenFalse(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Grupa narzędzi nie znaleziona: " + id));

        List<Tool> toolsInGroup = toolRepository.findByOrganizationIdAndHiddenFalse(organizationId)
                .stream()
                .filter(tool -> tool.getToolGroup() != null && tool.getToolGroup().getId().equals(id))
                .collect(Collectors.toList());

        toolsInGroup.forEach(tool -> tool.setToolGroup(null));
        toolRepository.saveAll(toolsInGroup);

        toolGroup.setHidden(true);
        toolGroupRepository.save(toolGroup);
        log.info("[deleteToolGroup] Soft deleted tool group {} and unassigned {} tools", id, toolsInGroup.size());
    }

    @Transactional(readOnly = true)
    public ToolGroup getToolGroupById(UUID id) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return toolGroupRepository.findByIdAndOrganizationIdAndHiddenFalse(id, organizationId).orElse(null);
    }
}

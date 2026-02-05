package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.badanieszkolenie.dto.BadanieSzkolenieDto;
import com.example.geartrackapi.dao.model.BadanieSzkolenie;
import com.example.geartrackapi.dao.model.BadanieSzkolenieStatus;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.Role;
import com.example.geartrackapi.dao.repository.BadanieSzkolenieRepository;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.mapper.BadanieSzkolenieMapper;
import com.example.geartrackapi.security.SecurityUser;
import com.example.geartrackapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadanieSzkolenieService {

    private final BadanieSzkolenieRepository badanieSzkolenieRepository;
    private final EmployeeRepository employeeRepository;
    private final BadanieSzkolenieMapper badanieSzkolenieMapper;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Transactional(readOnly = true)
    public List<BadanieSzkolenieDto> getBadaniaSzkoleniaByEmployeeId(UUID employeeId) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<BadanieSzkolenie> badania = badanieSzkolenieRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId);
        return badania.stream()
                .map(badanieSzkolenieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BadanieSzkolenieDto> getAllBadaniaSzkolenia() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<BadanieSzkolenie> badania = badanieSzkolenieRepository.findByOrganizationIdAndHiddenFalse(organizationId);
        return badania.stream()
                .map(badanieSzkolenieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BadanieSzkolenieDto createBadanieSzkolenie(UUID employeeId, BadanieSzkolenieDto dto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Employee employee = employeeRepository.findByIdAndHiddenFalse(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        if (!employee.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Employee does not belong to your organization");
        }

        BadanieSzkolenie badanie = badanieSzkolenieMapper.toEntity(dto, employee);
        BadanieSzkolenie saved = badanieSzkolenieRepository.save(badanie);
        BadanieSzkolenieDto savedDto = badanieSzkolenieMapper.toDto(saved);

        emitEvent("CREATE", savedDto);

        return savedDto;
    }

    @Transactional
    public BadanieSzkolenieDto updateBadanieSzkolenie(UUID id, BadanieSzkolenieDto dto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        BadanieSzkolenie existing = badanieSzkolenieRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("BadanieSzkolenie not found with ID: " + id));

        if (!existing.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("BadanieSzkolenie does not belong to your organization");
        }

        if (dto.getStatus() != null && dto.getStatus() != existing.getStatus()) {
            if (!isCurrentUserAdmin()) {
                throw new AccessDeniedException("Only admins can change badanie szkolenie status");
            }
        }

        Employee employee = employeeRepository.findByIdAndHiddenFalse(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        if (!employee.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Employee does not belong to your organization");
        }

        BadanieSzkolenie updated = badanieSzkolenieMapper.updateEntity(existing, dto, employee);
        BadanieSzkolenie saved = badanieSzkolenieRepository.save(updated);
        BadanieSzkolenieDto savedDto = badanieSzkolenieMapper.toDto(saved);

        emitEvent("UPDATE", savedDto);

        return savedDto;
    }

    @Transactional
    public void deleteBadanieSzkolenie(UUID id) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        BadanieSzkolenie badanie = badanieSzkolenieRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("BadanieSzkolenie not found with ID: " + id));

        if (!badanie.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("BadanieSzkolenie does not belong to your organization");
        }

        badanie.setHidden(true);
        badanieSzkolenieRepository.save(badanie);

        BadanieSzkolenieDto deletedDto = badanieSzkolenieMapper.toDto(badanie);
        emitEvent("DELETE", deletedDto);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        return badanieSzkolenieRepository.findDistinctCategoriesByOrganizationId(organizationId);
    }

    public SseEmitter subscribe() {
        log.info("[subscribe] Creating new SSE emitter for badania szkolenia updates");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("[subscribe] SSE emitter completed");
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("[subscribe] SSE emitter timed out");
            emitters.remove(emitter);
        });
        emitter.onError((e) -> {
            log.error("[subscribe] SSE emitter error", e);
            emitters.remove(emitter);
        });

        return emitter;
    }

    private void emitEvent(String eventType, BadanieSzkolenieDto dto) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(dto));
            } catch (IOException e) {
                log.error("Error sending SSE event", e);
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getRole() == Role.ADMIN;
        }
        return false;
    }
}

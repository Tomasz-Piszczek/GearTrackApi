package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.urlop.dto.UrlopDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.Role;
import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopStatus;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.UrlopRepository;
import com.example.geartrackapi.mapper.UrlopMapper;
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
public class UrlopService {

    private final UrlopRepository urlopRepository;
    private final EmployeeRepository employeeRepository;
    private final UrlopMapper urlopMapper;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Transactional(readOnly = true)
    public List<UrlopDto> getUrlopByEmployeeId(UUID employeeId) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<Urlop> urlopy = urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId);
        return urlopy.stream()
                .map(urlopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UrlopDto> getAllUrlopy() {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();
        List<Urlop> urlopy = urlopRepository.findByOrganizationIdAndHiddenFalse(organizationId);
        return urlopy.stream()
                .map(urlopMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UrlopDto createUrlop(UUID employeeId, UrlopDto urlopDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Employee employee = employeeRepository.findByIdAndHiddenFalse(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        if (!employee.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Employee does not belong to your organization");
        }

        Urlop urlop = urlopMapper.toEntity(urlopDto, employee);
        Urlop saved = urlopRepository.save(urlop);
        UrlopDto savedDto = urlopMapper.toDto(saved);

        emitEvent("CREATE", savedDto);

        return savedDto;
    }

    @Transactional
    public UrlopDto updateUrlop(UUID id, UrlopDto urlopDto) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Urlop existing = urlopRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Urlop not found with ID: " + id));

        if (!existing.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Urlop does not belong to your organization");
        }

        if (urlopDto.getStatus() != null && urlopDto.getStatus() != existing.getStatus()) {
            if (!isCurrentUserAdmin()) {
                throw new AccessDeniedException("Only admins can change urlop status");
            }
        }

        Employee employee = employeeRepository.findByIdAndHiddenFalse(urlopDto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + urlopDto.getEmployeeId()));

        if (!employee.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Employee does not belong to your organization");
        }

        Urlop updated = urlopMapper.updateEntity(existing, urlopDto, employee);
        Urlop saved = urlopRepository.save(updated);
        UrlopDto savedDto = urlopMapper.toDto(saved);

        emitEvent("UPDATE", savedDto);

        return savedDto;
    }

    @Transactional
    public void deleteUrlop(UUID id) {
        UUID organizationId = SecurityUtils.getCurrentOrganizationId();

        Urlop urlop = urlopRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Urlop not found with ID: " + id));

        if (!urlop.getOrganizationId().equals(organizationId)) {
            throw new AccessDeniedException("Urlop does not belong to your organization");
        }

        urlop.setHidden(true);
        urlopRepository.save(urlop);

        UrlopDto deletedDto = urlopMapper.toDto(urlop);
        emitEvent("DELETE", deletedDto);
    }

    public SseEmitter subscribe() {
        log.info("[subscribe] Creating new SSE emitter for urlopy updates");

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

    private void emitEvent(String eventType, UrlopDto urlopDto) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(urlopDto));
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

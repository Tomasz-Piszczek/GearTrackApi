package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.urlop.dto.UrlopDto;
import com.example.geartrackapi.dao.model.Employee;
import com.example.geartrackapi.dao.model.Role;
import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopCategory;
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
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlopService {

    private final UrlopRepository urlopRepository;
    private final EmployeeRepository employeeRepository;
    private final UrlopMapper urlopMapper;
    private final SseEmitterService sseEmitterService;
    private final PolishWorkingDaysService polishWorkingDaysService;
    private final EmployeeUrlopDaysService employeeUrlopDaysService;

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

        validateUrlopNaZadanie(employeeId, urlopDto, organizationId, null);
        validateRemainingVacationDays(employeeId, urlopDto, organizationId, null);

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

        validateUrlopNaZadanie(urlopDto.getEmployeeId(), urlopDto, organizationId, existing);
        validateRemainingVacationDays(urlopDto.getEmployeeId(), urlopDto, organizationId, existing);

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

    private void emitEvent(String eventType, UrlopDto urlopDto) {
        sseEmitterService.emitEvent(
            SecurityUtils.getCurrentOrganizationId(),
            "URLOP." + eventType,
            urlopDto
        );
    }

    private void validateUrlopNaZadanie(UUID employeeId, UrlopDto urlopDto, UUID organizationId, Urlop existingUrlop) {
        if (urlopDto.getCategory() != UrlopCategory.URLOP_NA_ŻĄDANIE) {
            return;
        }

        LocalDate fromDate = urlopDto.getFromDate();
        LocalDate toDate = urlopDto.getToDate();
        int year = fromDate.getYear();

        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        List<Urlop> urlopyNaZadanie = urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId)
                .stream()
                .filter(u -> u.getStatus() == UrlopStatus.ACCEPTED)
                .filter(u -> u.getCategory() == UrlopCategory.URLOP_NA_ŻĄDANIE)
                .filter(u -> {
                    if (existingUrlop != null && u.getId().equals(existingUrlop.getId())) {
                        return false;
                    }
                    LocalDate uFrom = u.getFromDate();
                    LocalDate uTo = u.getToDate();
                    return !(uTo.isBefore(yearStart) || uFrom.isAfter(yearEnd));
                })
                .collect(Collectors.toList());

        int currentUsedDays = urlopyNaZadanie.stream()
                .mapToInt(u -> {
                    LocalDate from = u.getFromDate().isBefore(yearStart) ? yearStart : u.getFromDate();
                    LocalDate to = u.getToDate().isAfter(yearEnd) ? yearEnd : u.getToDate();
                    return polishWorkingDaysService.countWorkingDays(from, to);
                })
                .sum();

        int newUrlopDays = polishWorkingDaysService.countWorkingDays(fromDate, toDate);
        int totalDays = currentUsedDays + newUrlopDays;

        if (totalDays > 4) {
            throw new IllegalStateException("Pracownik ma więcej niż 4 dni urlopu na żądanie w tym roku");
        }
    }

    private void validateRemainingVacationDays(UUID employeeId, UrlopDto urlopDto, UUID organizationId, Urlop existingUrlop) {
        if (!urlopDto.getCategory().countsTowardsVacationDays()) {
            return;
        }

        LocalDate fromDate = urlopDto.getFromDate();
        LocalDate toDate = urlopDto.getToDate();

        int requestedDays = polishWorkingDaysService.countWorkingDays(fromDate, toDate);

        try {
            var vacationSummary = employeeUrlopDaysService.getVacationSummary(employeeId);

            if (!vacationSummary.getIsConfigured()) {
                return;
            }

            int remainingDays = vacationSummary.getRemainingDays();

            if (existingUrlop != null && existingUrlop.getCategory().countsTowardsVacationDays()
                    && existingUrlop.getStatus() == UrlopStatus.ACCEPTED) {
                int existingDays = polishWorkingDaysService.countWorkingDays(
                        existingUrlop.getFromDate(),
                        existingUrlop.getToDate()
                );
                remainingDays += existingDays;
            }

            if (requestedDays > remainingDays) {
                throw new IllegalStateException(String.format(
                        "Pracownik nie ma wystarczającej liczby dni urlopu. Dostępne: %d dni, wymagane: %d dni",
                        remainingDays,
                        requestedDays
                ));
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw e;
            }
            log.warn("Could not validate remaining vacation days for employee {}: {}", employeeId, e.getMessage());
        }
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return securityUser.getRole() == Role.ADMIN;
        }
        return false;
    }
}

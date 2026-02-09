package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.employee.dto.VacationSummaryDto;
import com.example.geartrackapi.controller.urlop.dto.UrlopDto;
import com.example.geartrackapi.dao.model.*;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.UrlopRepository;
import com.example.geartrackapi.mapper.UrlopMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlopServiceTest {

    @Mock
    private UrlopRepository urlopRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UrlopMapper urlopMapper;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private PolishWorkingDaysService polishWorkingDaysService;

    @Mock
    private EmployeeUrlopDaysService employeeUrlopDaysService;

    @InjectMocks
    private UrlopService urlopService;

    private UUID employeeId;
    private UUID organizationId;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        employee = Employee.builder()
                .id(employeeId)
                .firstName("Jan")
                .lastName("Kowalski")
                .organizationId(organizationId)
                .build();
    }

    @Test
    @DisplayName("Should allow creating URLOP_NA_ŻĄDANIE when employee has used 0 days")
    void shouldAllowNaZadanieWhenZeroDaysUsed() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10)))
                .thenReturn(1);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should allow creating URLOP_NA_ŻĄDANIE when employee has used 3 days")
    void shouldAllowNaZadanieWhenThreeDaysUsed() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 5, 20),
                LocalDate.of(2024, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop existingUrlop1 = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop existingUrlop2 = createUrlop(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(existingUrlop1, existingUrlop2));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 10)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)))
                .thenReturn(2);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20)))
                .thenReturn(1);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should reject URLOP_NA_ŻĄDANIE when employee already has 4 days")
    void shouldRejectNaZadanieWhenFourDaysUsed() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop existingUrlop1 = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop existingUrlop2 = createUrlop(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(existingUrlop1, existingUrlop2));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11)))
                .thenReturn(2);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)))
                .thenReturn(2);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20)))
                .thenReturn(1);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlopService.createUrlop(employeeId, urlopDto));

        assertEquals("Pracownik ma więcej niż 4 dni urlopu na żądanie w tym roku", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject URLOP_NA_ŻĄDANIE when request would exceed 4 days")
    void shouldRejectNaZadanieWhenWouldExceedLimit() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop existingUrlop1 = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(existingUrlop1));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11)))
                .thenReturn(2);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22)))
                .thenReturn(3);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlopService.createUrlop(employeeId, urlopDto));

        assertEquals("Pracownik ma więcej niż 4 dni urlopu na żądanie w tym roku", exception.getMessage());
    }

    @Test
    @DisplayName("Should not count PENDING URLOP_NA_ŻĄDANIE towards limit")
    void shouldNotCountPendingNaZadanieTowardsLimit() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop pendingUrlop = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 13),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(pendingUrlop));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20)))
                .thenReturn(1);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should not count REJECTED URLOP_NA_ŻĄDANIE towards limit")
    void shouldNotCountRejectedNaZadanieTowardsLimit() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop rejectedUrlop = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 13),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.REJECTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(rejectedUrlop));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20)))
                .thenReturn(1);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should not validate URLOP_WYPOCZYNKOWY against NA_ŻĄDANIE limit")
    void shouldNotValidateWypoczynkowyAgainstLimit() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 30),
                UrlopCategory.URLOP_WYPOCZYNKOWY
        );

        Urlop existingNaZadanie = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 13),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(existingNaZadanie));

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 30),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
        verify(polishWorkingDaysService, never()).countWorkingDays(any(), any());
    }

    @Test
    @DisplayName("Should exclude current urlop when updating to URLOP_NA_ŻĄDANIE")
    void shouldExcludeCurrentUrlopWhenUpdating() {
        UUID urlopId = UUID.randomUUID();
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );
        urlopDto.setId(urlopId);
        urlopDto.setEmployeeId(employeeId);

        Urlop existingUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        existingUrlop.setId(urlopId);

        Urlop otherNaZadanie = createUrlop(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 13),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByIdAndHiddenFalse(urlopId))
                .thenReturn(Optional.of(existingUrlop));
        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(existingUrlop, otherNaZadanie));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 13)))
                .thenReturn(4);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20)))
                .thenReturn(1);

        Urlop updatedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        when(urlopMapper.updateEntity(any(), any(), any())).thenReturn(updatedUrlop);
        when(urlopRepository.save(any())).thenReturn(updatedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlopService.updateUrlop(urlopId, urlopDto));

        assertEquals("Pracownik ma więcej niż 4 dni urlopu na żądanie w tym roku", exception.getMessage());
    }

    @Test
    @DisplayName("Should count NA_ŻĄDANIE from different year separately")
    void shouldCountNaZadanieFromDifferentYearSeparately() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE
        );

        Urlop previousYearUrlop = createUrlop(LocalDate.of(2023, 12, 15), LocalDate.of(2023, 12, 18),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(previousYearUrlop));
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 10)))
                .thenReturn(1);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should reject URLOP_WYPOCZYNKOWY when employee has no remaining days")
    void shouldRejectWypoczynkowyWhenNoRemainingDays() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_WYPOCZYNKOWY
        );

        VacationSummaryDto vacationSummary = VacationSummaryDto.builder()
                .isConfigured(true)
                .remainingDays(0)
                .build();

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(employeeUrlopDaysService.getVacationSummary(employeeId))
                .thenReturn(vacationSummary);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22)))
                .thenReturn(3);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlopService.createUrlop(employeeId, urlopDto));

        assertTrue(exception.getMessage().contains("Pracownik nie ma wystarczającej liczby dni urlopu"));
        assertTrue(exception.getMessage().contains("Dostępne: 0 dni"));
    }

    @Test
    @DisplayName("Should reject URLOP_WYPOCZYNKOWY when employee has insufficient remaining days")
    void shouldRejectWypoczynkowyWhenInsufficientDays() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 29),
                UrlopCategory.URLOP_WYPOCZYNKOWY
        );

        VacationSummaryDto vacationSummary = VacationSummaryDto.builder()
                .isConfigured(true)
                .remainingDays(5)
                .build();

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(employeeUrlopDaysService.getVacationSummary(employeeId))
                .thenReturn(vacationSummary);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 29)))
                .thenReturn(8);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlopService.createUrlop(employeeId, urlopDto));

        assertTrue(exception.getMessage().contains("Pracownik nie ma wystarczającej liczby dni urlopu"));
        assertTrue(exception.getMessage().contains("Dostępne: 5 dni, wymagane: 8 dni"));
    }

    @Test
    @DisplayName("Should allow URLOP_WYPOCZYNKOWY when employee has sufficient remaining days")
    void shouldAllowWypoczynkowyWhenSufficientDays() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_WYPOCZYNKOWY
        );

        VacationSummaryDto vacationSummary = VacationSummaryDto.builder()
                .isConfigured(true)
                .remainingDays(10)
                .build();

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(employeeUrlopDaysService.getVacationSummary(employeeId))
                .thenReturn(vacationSummary);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22)))
                .thenReturn(3);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    @Test
    @DisplayName("Should not validate URLOP_MACIERZYNSKI against remaining vacation days")
    void shouldNotValidateMacierzynskiAgainstRemainingDays() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 12, 31),
                UrlopCategory.URLOP_MACIERZYNSKI
        );

        VacationSummaryDto vacationSummary = VacationSummaryDto.builder()
                .isConfigured(true)
                .remainingDays(0)
                .build();

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(employeeUrlopDaysService.getVacationSummary(employeeId))
                .thenReturn(vacationSummary);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 12, 31),
                UrlopCategory.URLOP_MACIERZYNSKI, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
        verify(polishWorkingDaysService, never()).countWorkingDays(any(), any());
    }

    @Test
    @DisplayName("Should allow creation when vacation days not configured")
    void shouldAllowWhenVacationDaysNotConfigured() {
        UrlopDto urlopDto = createUrlopDto(
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_WYPOCZYNKOWY
        );

        VacationSummaryDto vacationSummary = VacationSummaryDto.builder()
                .isConfigured(false)
                .remainingDays(null)
                .build();

        when(employeeRepository.findByIdAndHiddenFalse(employeeId))
                .thenReturn(Optional.of(employee));
        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of());
        when(employeeUrlopDaysService.getVacationSummary(employeeId))
                .thenReturn(vacationSummary);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22)))
                .thenReturn(3);

        Urlop savedUrlop = createUrlop(LocalDate.of(2024, 8, 20), LocalDate.of(2024, 8, 22),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.PENDING);
        when(urlopMapper.toEntity(any(), any())).thenReturn(savedUrlop);
        when(urlopRepository.save(any())).thenReturn(savedUrlop);
        when(urlopMapper.toDto(any())).thenReturn(urlopDto);

        assertDoesNotThrow(() -> urlopService.createUrlop(employeeId, urlopDto));
    }

    private UrlopDto createUrlopDto(LocalDate fromDate, LocalDate toDate, UrlopCategory category) {
        return UrlopDto.builder()
                .employeeId(employeeId)
                .fromDate(fromDate)
                .toDate(toDate)
                .category(category)
                .status(UrlopStatus.PENDING)
                .build();
    }

    private Urlop createUrlop(LocalDate fromDate, LocalDate toDate, UrlopCategory category, UrlopStatus status) {
        return Urlop.builder()
                .id(UUID.randomUUID())
                .fromDate(fromDate)
                .toDate(toDate)
                .category(category)
                .status(status)
                .employee(employee)
                .organizationId(organizationId)
                .build();
    }
}

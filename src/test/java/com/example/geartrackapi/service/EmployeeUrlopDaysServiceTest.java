package com.example.geartrackapi.service;

import com.example.geartrackapi.controller.employee.dto.VacationSummaryDto;
import com.example.geartrackapi.dao.model.*;
import com.example.geartrackapi.dao.repository.EmployeeRepository;
import com.example.geartrackapi.dao.repository.EmployeeUrlopDaysRepository;
import com.example.geartrackapi.dao.repository.UrlopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeUrlopDaysServiceTest {

    @Mock
    private EmployeeUrlopDaysRepository employeeUrlopDaysRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UrlopRepository urlopRepository;

    @Mock
    private PolishWorkingDaysService polishWorkingDaysService;

    @InjectMocks
    private EmployeeUrlopDaysService employeeUrlopDaysService;

    private UUID employeeId;
    private UUID organizationId;
    private int currentYear;
    private int previousYear;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        currentYear = 2024;
        previousYear = 2023;
    }

    @Test
    @DisplayName("Should calculate remaining days with only URLOP_WYPOCZYNKOWY")
    void shouldCalculateRemainingDaysWithOnlyWypoczynkowy() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlop1 = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlop1));

        when(polishWorkingDaysService.countWorkingDays(any(), any())).thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should calculate remaining days with WYPOCZYNKOWY and NA_ŻĄDANIE")
    void shouldCalculateRemainingDaysWithWypoczynkowyAndNaZadanie() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlopWypoczynkowy = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop urlopNaZadanie1 = createUrlop(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop urlopNaZadanie2 = createUrlop(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlopWypoczynkowy, urlopNaZadanie1, urlopNaZadanie2));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20)))
                .thenReturn(1);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(19, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should not count URLOP_MACIERZYNSKI towards remaining vacation days")
    void shouldNotCountMacierzynskiTowardsVacationDays() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlopWypoczynkowy = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop urlopMacierzynski = createUrlop(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 6, 30),
                UrlopCategory.URLOP_MACIERZYNSKI, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlopWypoczynkowy, urlopMacierzynski));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should not count URLOP_BEZPLATNY towards remaining vacation days")
    void shouldNotCountBezplatnyTowardsVacationDays() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlopWypoczynkowy = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop urlopBezplatny = createUrlop(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 31),
                UrlopCategory.URLOP_BEZPLATNY, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlopWypoczynkowy, urlopBezplatny));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should carry over unused days from previous year")
    void shouldCarryOverUnusedDaysFromPreviousYear() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop previousYearUrlop = createUrlop(LocalDate.of(2023, 6, 1), LocalDate.of(2023, 6, 10),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop currentYearUrlop = createUrlop(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(previousYearUrlop, currentYearUrlop));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 6, 1), LocalDate.of(2023, 6, 10)))
                .thenReturn(8);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        int carryOver = 26 - 8;
        int remaining = 26 + carryOver - 5;
        assertEquals(39, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should handle scenario: 4 days NA_ŻĄDANIE + 22 days WYPOCZYNKOWY in previous year, wants 2 more days")
    void shouldHandleUserScenario() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop prevYearWypoczynkowy = createUrlop(LocalDate.of(2023, 1, 10), LocalDate.of(2023, 2, 10),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop prevYearNaZadanie1 = createUrlop(LocalDate.of(2023, 3, 15), LocalDate.of(2023, 3, 15),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop prevYearNaZadanie2 = createUrlop(LocalDate.of(2023, 5, 20), LocalDate.of(2023, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop prevYearNaZadanie3 = createUrlop(LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop prevYearNaZadanie4 = createUrlop(LocalDate.of(2023, 9, 5), LocalDate.of(2023, 9, 5),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        Urlop currentYearWypoczynkowy = createUrlop(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 11),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(prevYearWypoczynkowy, prevYearNaZadanie1, prevYearNaZadanie2,
                        prevYearNaZadanie3, prevYearNaZadanie4, currentYearWypoczynkowy));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 1, 10), LocalDate.of(2023, 2, 10)))
                .thenReturn(22);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 3, 15), LocalDate.of(2023, 3, 15)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 5, 20), LocalDate.of(2023, 5, 20)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 10)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2023, 9, 5), LocalDate.of(2023, 9, 5)))
                .thenReturn(1);
        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 11)))
                .thenReturn(2);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        int previousYearUsed = 22 + 4;
        int carryOver = 26 - previousYearUsed;
        int currentYearUsed = 2;
        int remaining = 26 + carryOver - currentYearUsed;
        assertEquals(24, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should not count PENDING urlopy towards used days")
    void shouldNotCountPendingUrlopy() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop acceptedUrlop = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop pendingUrlop = createUrlop(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.PENDING);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(acceptedUrlop, pendingUrlop));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should not count REJECTED urlopy towards used days")
    void shouldNotCountRejectedUrlopy() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop acceptedUrlop = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop rejectedUrlop = createUrlop(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.REJECTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(acceptedUrlop, rejectedUrlop));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should return not configured when current year days are missing")
    void shouldReturnNotConfiguredWhenCurrentYearMissing() {
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(previousYearDays));

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertFalse(result.getIsConfigured());
        assertNull(result.getRemainingDays());
        assertTrue(result.getMissingYears().contains(currentYear));
    }

    @Test
    @DisplayName("Should return not configured when previous year days are missing")
    void shouldReturnNotConfiguredWhenPreviousYearMissing() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays));

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertFalse(result.getIsConfigured());
        assertNull(result.getRemainingDays());
        assertTrue(result.getMissingYears().contains(previousYear));
    }

    @Test
    @DisplayName("Should count NA_ŻĄDANIE urlopy in year")
    void shouldCountNaZadanieUrlopyInYear() {
        Urlop naZadanie1 = createUrlop(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop naZadanie2 = createUrlop(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop naZadanie3 = createUrlop(LocalDate.of(2024, 8, 15), LocalDate.of(2024, 8, 15),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(naZadanie1, naZadanie2, naZadanie3));

        int count = employeeUrlopDaysService.countUrlopNaZadanieInYear(employeeId, 2024, organizationId);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should not count NA_ŻĄDANIE from different year")
    void shouldNotCountNaZadanieFromDifferentYear() {
        Urlop naZadanie2023 = createUrlop(LocalDate.of(2023, 3, 10), LocalDate.of(2023, 3, 10),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);
        Urlop naZadanie2024 = createUrlop(LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                UrlopCategory.URLOP_NA_ŻĄDANIE, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(naZadanie2023, naZadanie2024));

        int count = employeeUrlopDaysService.countUrlopNaZadanieInYear(employeeId, 2024, organizationId);

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Should handle employee with 20 days allowance (< 10 years experience)")
    void shouldHandleEmployeeWith20DaysAllowance() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 20);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 20);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlop = createUrlop(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 5),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlop));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 5)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(15, result.getRemainingDays());
    }

    @Test
    @DisplayName("Should not count URLOP_OJCOWSKI towards remaining vacation days")
    void shouldNotCountOjcowskiTowardsVacationDays() {
        EmployeeUrlopDays currentYearDays = createEmployeeUrlopDays(currentYear, 26);
        EmployeeUrlopDays previousYearDays = createEmployeeUrlopDays(previousYear, 26);

        when(employeeUrlopDaysRepository.findByEmployeeIdAndYearInAndOrganizationIdAndHiddenFalse(
                employeeId, List.of(currentYear, previousYear), organizationId))
                .thenReturn(List.of(currentYearDays, previousYearDays));

        Urlop urlopWypoczynkowy = createUrlop(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19),
                UrlopCategory.URLOP_WYPOCZYNKOWY, UrlopStatus.ACCEPTED);
        Urlop urlopOjcowski = createUrlop(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 14),
                UrlopCategory.URLOP_OJCOWSKI, UrlopStatus.ACCEPTED);

        when(urlopRepository.findByEmployeeIdAndOrganizationIdAndHiddenFalse(employeeId, organizationId))
                .thenReturn(List.of(urlopWypoczynkowy, urlopOjcowski));

        when(polishWorkingDaysService.countWorkingDays(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19)))
                .thenReturn(5);

        VacationSummaryDto result = employeeUrlopDaysService.getVacationSummary(employeeId);

        assertTrue(result.getIsConfigured());
        assertEquals(21, result.getRemainingDays());
    }

    private EmployeeUrlopDays createEmployeeUrlopDays(int year, int days) {
        return EmployeeUrlopDays.builder()
                .year(year)
                .days(days)
                .build();
    }

    private Urlop createUrlop(LocalDate fromDate, LocalDate toDate, UrlopCategory category, UrlopStatus status) {
        return Urlop.builder()
                .id(UUID.randomUUID())
                .fromDate(fromDate)
                .toDate(toDate)
                .category(category)
                .status(status)
                .employee(Employee.builder().id(employeeId).build())
                .organizationId(organizationId)
                .build();
    }
}

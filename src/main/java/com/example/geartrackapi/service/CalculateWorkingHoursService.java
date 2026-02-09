package com.example.geartrackapi.service;

import com.example.geartrackapi.client.BiAnalyticsClient;
import com.example.geartrackapi.controller.payroll.dto.DailyBreakdownDto;
import com.example.geartrackapi.controller.payroll.dto.DailyHoursDto;
import com.example.geartrackapi.controller.payroll.dto.EmployeeHoursDto;
import com.example.geartrackapi.controller.payroll.dto.UrlopBreakdownDto;
import com.example.geartrackapi.dao.model.Urlop;
import com.example.geartrackapi.dao.model.UrlopCategory;
import com.example.geartrackapi.dao.repository.UrlopRepository;
import com.example.geartrackapi.exception.WorkingHoursConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculateWorkingHoursService {

    private final BiAnalyticsClient biAnalyticsClient;
    private final UrlopRepository urlopRepository;
    private final PolishWorkingDaysService polishWorkingDaysService;
    private static final BigDecimal DEFAULT_URLOP_HOURS = new BigDecimal("8");

    public Map<String, WorkingHoursData> calculateWorkingHours(
            List<String> employeeNames,
            Integer year,
            Integer month,
            String jwtToken,
            UUID organizationId) {

        List<EmployeeHoursDto> biHours = biAnalyticsClient.getEmployeeHours(employeeNames, year, month, jwtToken);

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Urlop> allUrlopy = urlopRepository.findAcceptedUrlopyByEmployeeNamesAndDateRange(
                employeeNames, startOfMonth, endOfMonth, organizationId);

        Map<String, List<Urlop>> urlopyByEmployee = allUrlopy.stream()
                .collect(Collectors.groupingBy(u -> u.getEmployee().getFirstName() + " " + u.getEmployee().getLastName()));

        Map<String, EmployeeHoursDto> biHoursMap = biHours.stream()
                .collect(Collectors.toMap(EmployeeHoursDto::getEmployeeName, hours -> hours, (a, b) -> a));

        Map<String, List<LocalDate>> allConflicts = new HashMap<>();
        Map<String, WorkingHoursData> result = new HashMap<>();

        for (String employeeName : employeeNames) {
            EmployeeHoursDto hours = biHoursMap.get(employeeName);
            List<Urlop> employeeUrlopy = urlopyByEmployee.getOrDefault(employeeName, Collections.emptyList());

            Set<LocalDate> workDates = hours.getDailyHours().stream()
                    .map(DailyHoursDto::getDate)
                    .collect(Collectors.toSet());

            List<DailyBreakdownDto> dailyBreakdown = new ArrayList<>();
            for (DailyHoursDto biDay : hours.getDailyHours()) {
                BigDecimal actualHours = biDay.getHours();
                BigDecimal roundedHours = roundToNearestHour(actualHours);

                dailyBreakdown.add(DailyBreakdownDto.builder()
                        .date(biDay.getDate())
                        .actualHours(actualHours)
                        .roundedHours(roundedHours)
                        .startTime(biDay.getStartTime())
                        .endTime(biDay.getEndTime())
                        .build());
            }

            List<UrlopBreakdownDto> urlopBreakdown = calculateUrlopBreakdown(employeeUrlopy, startOfMonth, endOfMonth);

            Set<LocalDate> urlopDates = new HashSet<>();
            for (Urlop urlop : employeeUrlopy) {
                LocalDate start = urlop.getFromDate().isBefore(startOfMonth) ? startOfMonth : urlop.getFromDate();
                LocalDate end = urlop.getToDate().isAfter(endOfMonth) ? endOfMonth : urlop.getToDate();

                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    if (polishWorkingDaysService.isWorkingDay(date)) {
                        urlopDates.add(date);
                    }
                }
            }

            List<LocalDate> conflicts = workDates.stream()
                    .filter(urlopDates::contains)
                    .sorted()
                    .collect(Collectors.toList());

            if (!conflicts.isEmpty()) {
                allConflicts.put(employeeName, conflicts);
            }

            BigDecimal totalWorkHours = dailyBreakdown.stream()
                    .map(DailyBreakdownDto::getRoundedHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalUrlopHours = urlopBreakdown.stream()
                    .map(UrlopBreakdownDto::getTotalHours)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalHours = totalWorkHours.add(totalUrlopHours);

            result.put(employeeName, WorkingHoursData.builder()
                    .totalHours(totalHours)
                    .dailyBreakdown(dailyBreakdown)
                    .urlopBreakdown(urlopBreakdown)
                    .build());
        }

        if (!allConflicts.isEmpty()) {
            throw new WorkingHoursConflictException(
                    "Znaleziono konflikty między godzinami pracy a urlopami",
                    allConflicts
            );
        }

        return result;
    }

    public BigDecimal roundToNearestHour(BigDecimal hours) {
        if (hours.stripTrailingZeros().scale() <= 0) {
            return hours;
        }
        return hours.setScale(0, RoundingMode.HALF_UP);
    }

    private List<UrlopBreakdownDto> calculateUrlopBreakdown(List<Urlop> urlopy, LocalDate startOfMonth, LocalDate endOfMonth) {
        Map<UrlopCategory, BigDecimal> categoryHours = new HashMap<>();

        for (Urlop urlop : urlopy) {
            LocalDate start = urlop.getFromDate().isBefore(startOfMonth) ? startOfMonth : urlop.getFromDate();
            LocalDate end = urlop.getToDate().isAfter(endOfMonth) ? endOfMonth : urlop.getToDate();

            BigDecimal hours = BigDecimal.ZERO;
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                if (polishWorkingDaysService.isWorkingDay(date)) {
                    hours = hours.add(DEFAULT_URLOP_HOURS);
                }
            }

            categoryHours.merge(urlop.getCategory(), hours, BigDecimal::add);
        }

        return categoryHours.entrySet().stream()
                .map(entry -> UrlopBreakdownDto.builder()
                        .category(entry.getKey())
                        .totalHours(entry.getValue())
                        .rate(entry.getKey().getRate())
                        .build())
                .collect(Collectors.toList());
    }

    public static class WorkingHoursData {
        private final BigDecimal totalHours;
        private final List<DailyBreakdownDto> dailyBreakdown;
        private final List<UrlopBreakdownDto> urlopBreakdown;

        @lombok.Builder
        public WorkingHoursData(BigDecimal totalHours, List<DailyBreakdownDto> dailyBreakdown, List<UrlopBreakdownDto> urlopBreakdown) {
            this.totalHours = totalHours;
            this.dailyBreakdown = dailyBreakdown;
            this.urlopBreakdown = urlopBreakdown;
        }

        public BigDecimal getTotalHours() {
            return totalHours;
        }

        public List<DailyBreakdownDto> getDailyBreakdown() {
            return dailyBreakdown;
        }

        public List<UrlopBreakdownDto> getUrlopBreakdown() {
            return urlopBreakdown;
        }
    }
}

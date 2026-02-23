package com.example.geartrackapi.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class PolishWorkingDaysService {

    private final Map<Integer, Set<LocalDate>> holidaysCache = new HashMap<>();

    public int countWorkingDays(LocalDate from, LocalDate to) {
        int workingDays = 0;
        LocalDate current = from;

        while (!current.isAfter(to)) {
            if (isWorkingDay(current)) {
                workingDays++;
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    public boolean isWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        Set<LocalDate> holidays = getPolishHolidays(date.getYear());
        return !holidays.contains(date);
    }

    public Set<LocalDate> getPolishHolidays(int year) {
        return holidaysCache.computeIfAbsent(year, this::calculatePolishHolidays);
    }

    private Set<LocalDate> calculatePolishHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();

        holidays.add(LocalDate.of(year, 1, 1));
        holidays.add(LocalDate.of(year, 1, 6));
        holidays.add(LocalDate.of(year, 5, 1));
        holidays.add(LocalDate.of(year, 5, 3));
        holidays.add(LocalDate.of(year, 8, 15));
        holidays.add(LocalDate.of(year, 11, 1));
        holidays.add(LocalDate.of(year, 11, 11));
        holidays.add(LocalDate.of(year, 12, 25));
        holidays.add(LocalDate.of(year, 12, 26));

        LocalDate easter = calculateEasterSunday(year);
        holidays.add(easter);
        holidays.add(easter.plusDays(1));
        holidays.add(easter.plusDays(49));
        holidays.add(easter.plusDays(60));

        return holidays;
    }

    private LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }
}

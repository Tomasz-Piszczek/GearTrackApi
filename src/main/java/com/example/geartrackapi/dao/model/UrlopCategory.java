package com.example.geartrackapi.dao.model;

import java.math.BigDecimal;

public enum UrlopCategory {
    URLOP_WYPOCZYNKOWY(new BigDecimal("1.00")),
    URLOP_MACIERZYNSKI(new BigDecimal("1.00")),
    URLOP_BEZPLATNY(new BigDecimal("0.00"));

    private final BigDecimal rate;

    UrlopCategory(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}

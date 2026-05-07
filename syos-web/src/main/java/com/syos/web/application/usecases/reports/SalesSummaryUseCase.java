package com.syos.web.application.usecases.reports;

import com.syos.web.application.dto.reports.SalesSummaryDTO;
import com.syos.web.infrastructure.persistence.dao.ReportsDao;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Sales Summary Report Use Case
 * Handles business logic for sales summary reporting
 */
public class SalesSummaryUseCase {

    private final ReportsDao reportsDao;

    public SalesSummaryUseCase(ReportsDao reportsDao) {
        this.reportsDao = reportsDao;
    }

    public SalesSummaryDTO execute(String period, String startDateStr, String endDateStr) throws SQLException {
        // Parse dates based on period
        LocalDate[] dates = parsePeriodDates(period, startDateStr, endDateStr);
        LocalDate startDate = dates[0];
        LocalDate endDate = dates[1];

        return reportsDao.getSalesSummary(startDate, endDate, period);
    }

    private LocalDate[] parsePeriodDates(String period, String startDateStr, String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;
        LocalDate today = LocalDate.now();

        switch (period.toLowerCase()) {
            case "today":
                startDate = today;
                endDate = today;
                break;
            case "yesterday":
                startDate = today.minusDays(1);
                endDate = today.minusDays(1);
                break;
            case "week":
                startDate = today.minusDays(6); // Last 7 days including today
                endDate = today;
                break;
            case "month":
                startDate = today.minusDays(29); // Last 30 days including today
                endDate = today;
                break;
            case "year":
                startDate = today.minusDays(364); // Last 365 days including today
                endDate = today;
                break;
            case "custom":
                if (startDateStr != null && endDateStr != null) {
                    startDate = LocalDate.parse(startDateStr);
                    endDate = LocalDate.parse(endDateStr);
                } else {
                    throw new IllegalArgumentException("Start date and end date required for custom period");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        return new LocalDate[]{startDate, endDate};
    }
}

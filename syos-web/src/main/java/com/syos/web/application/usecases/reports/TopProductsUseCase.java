package com.syos.web.application.usecases.reports;

import com.syos.web.application.dto.reports.TopProductDTO;
import com.syos.web.infrastructure.persistence.dao.ReportsDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Top Products Report Use Case
 * Handles business logic for top products reporting
 */
public class TopProductsUseCase {

    private final ReportsDao reportsDao;

    public TopProductsUseCase(ReportsDao reportsDao) {
        this.reportsDao = reportsDao;
    }

    public List<TopProductDTO> execute(String period, int limit, String sortBy) throws SQLException {
        // Parse dates based on period
        LocalDate[] dates = parsePeriodDates(period);
        LocalDate startDate = dates[0];
        LocalDate endDate = dates[1];

        // Validate sortBy parameter
        String validatedSortBy = validateSortBy(sortBy);

        // Validate limit
        int validatedLimit = Math.max(1, Math.min(limit, 100)); // Between 1 and 100

        return reportsDao.getTopProducts(startDate, endDate, validatedSortBy, validatedLimit);
    }

    private LocalDate[] parsePeriodDates(String period) {
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
                startDate = today.minusDays(6);
                endDate = today;
                break;
            case "month":
                startDate = today.minusDays(29);
                endDate = today;
                break;
            case "year":
                startDate = today.minusDays(364);
                endDate = today;
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        return new LocalDate[]{startDate, endDate};
    }

    private String validateSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "quantity"; // default
        }

        String normalized = sortBy.toLowerCase().trim();
        if ("quantity".equals(normalized) || "revenue".equals(normalized)) {
            return normalized;
        }

        return "quantity"; // default fallback
    }
}

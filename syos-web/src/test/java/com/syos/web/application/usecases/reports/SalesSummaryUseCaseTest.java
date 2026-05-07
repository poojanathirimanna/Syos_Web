package com.syos.web.application.usecases.reports;

import com.syos.web.application.dto.reports.SalesSummaryDTO;
import com.syos.web.infrastructure.persistence.dao.ReportsDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test for Sales Summary Use Case
 */
public class SalesSummaryUseCaseTest {

    @Mock
    private ReportsDao reportsDao;

    private SalesSummaryUseCase salesSummaryUseCase;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        salesSummaryUseCase = new SalesSummaryUseCase(reportsDao);
    }

    @Test
    public void testExecuteToday() throws SQLException {
        // Arrange
        SalesSummaryDTO mockSummary = new SalesSummaryDTO();
        mockSummary.setTotalRevenue(new BigDecimal("1000.00"));
        mockSummary.setTotalOrders(10);
        mockSummary.setPeriod("today");

        when(reportsDao.getSalesSummary(any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(mockSummary);

        // Act
        SalesSummaryDTO result = salesSummaryUseCase.execute("today", null, null);

        // Assert
        assertNotNull(result);
        assertEquals("today", result.getPeriod());
        assertEquals(new BigDecimal("1000.00"), result.getTotalRevenue());
        assertEquals(10, result.getTotalOrders());
    }

    @Test
    public void testExecuteCustomPeriod() throws SQLException {
        // Arrange
        SalesSummaryDTO mockSummary = new SalesSummaryDTO();
        mockSummary.setTotalRevenue(new BigDecimal("5000.00"));
        mockSummary.setTotalOrders(50);
        mockSummary.setPeriod("custom");

        when(reportsDao.getSalesSummary(any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(mockSummary);

        // Act
        SalesSummaryDTO result = salesSummaryUseCase.execute("custom", "2026-01-01", "2026-01-31");

        // Assert
        assertNotNull(result);
        assertEquals("custom", result.getPeriod());
        assertEquals(new BigDecimal("5000.00"), result.getTotalRevenue());
    }

    @Test
    public void testInvalidPeriod() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            salesSummaryUseCase.execute("invalid", null, null);
        });
    }

    @Test
    public void testCustomPeriodMissingDates() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            salesSummaryUseCase.execute("custom", null, null);
        });
    }
}

package com.syos.web.presentation.api.admin.reports;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.reports.*;
import com.syos.web.application.usecases.reports.*;
import com.syos.web.infrastructure.persistence.dao.ReportsDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reports API Servlet - Handles all reporting endpoints
 *
 * Endpoints:
 * GET /api/admin/reports/sales/summary         → Sales Summary Report
 * GET /api/admin/reports/products/top          → Top Products Report
 * GET /api/admin/reports/categories/performance → Category Performance Report
 * GET /api/admin/reports/sales/hourly          → Peak Hours Analysis
 * GET /api/admin/reports/inventory/alerts      → Inventory Alerts
 * GET /api/admin/reports/cashiers/performance  → Cashier Performance Report
 */
@WebServlet("/api/admin/reports/*")
public class ApiReportsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private ReportsDao reportsDao;

    // Use Cases
    private SalesSummaryUseCase salesSummaryUseCase;
    private TopProductsUseCase topProductsUseCase;
    private InventoryAlertsUseCase inventoryAlertsUseCase;

    @Override
    public void init() throws ServletException {
        reportsDao = new ReportsDao();
        salesSummaryUseCase = new SalesSummaryUseCase(reportsDao);
        topProductsUseCase = new TopProductsUseCase(reportsDao);
        inventoryAlertsUseCase = new InventoryAlertsUseCase(reportsDao);

        System.out.println("✅ ApiReportsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        System.out.println("🔍 ApiReportsServlet - Received request: " + req.getRequestURI());

        try {
            String pathInfo = req.getPathInfo();
            System.out.println("🔍 PathInfo: " + pathInfo);

            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("⚠️ Invalid endpoint - no path info");
                handleInvalidEndpoint(resp);
                return;
            }

            // Route to appropriate handler based on path
            String[] pathParts = pathInfo.substring(1).split("/");
            System.out.println("🔍 Path parts: " + java.util.Arrays.toString(pathParts));

            if (pathParts.length < 2) {
                System.out.println("⚠️ Invalid endpoint - insufficient path parts");
                handleInvalidEndpoint(resp);
                return;
            }

            String category = pathParts[0]; // sales, products, categories, inventory, cashiers
            String reportType = pathParts[1]; // summary, top, performance, hourly, alerts

            System.out.println("🔍 Category: " + category + ", ReportType: " + reportType);

            // Add test endpoint
            if ("test".equals(category) && "hello".equals(reportType)) {
                ApiResponse<String> response = ApiResponse.success("Hello from Reports API!", "Test successful");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            switch (category) {
                case "sales":
                    handleSalesReports(reportType, req, resp);
                    break;
                case "products":
                    handleProductsReports(reportType, req, resp);
                    break;
                case "categories":
                    handleCategoriesReports(reportType, req, resp);
                    break;
                case "inventory":
                    handleInventoryReports(reportType, req, resp);
                    break;
                case "cashiers":
                    handleCashiersReports(reportType, req, resp);
                    break;
                default:
                    handleInvalidEndpoint(resp);
                    break;
            }

        } catch (Exception e) {
            handleError(resp, "Failed to generate report: " + e.getMessage(), e);
        }
    }

    private void handleSalesReports(String reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        switch (reportType) {
            case "summary":
                handleSalesSummary(req, resp);
                break;
            case "hourly":
                handleHourlyAnalysis(req, resp);
                break;
            default:
                handleInvalidEndpoint(resp);
                break;
        }
    }

    private void handleProductsReports(String reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        if ("top".equals(reportType)) {
            handleTopProducts(req, resp);
        } else {
            handleInvalidEndpoint(resp);
        }
    }

    private void handleCategoriesReports(String reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        if ("performance".equals(reportType)) {
            handleCategoryPerformance(req, resp);
        } else {
            handleInvalidEndpoint(resp);
        }
    }

    private void handleInventoryReports(String reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        if ("alerts".equals(reportType)) {
            handleInventoryAlerts(req, resp);
        } else {
            handleInvalidEndpoint(resp);
        }
    }

    private void handleCashiersReports(String reportType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        if ("performance".equals(reportType)) {
            handleCashierPerformance(req, resp);
        } else {
            handleInvalidEndpoint(resp);
        }
    }

    // Individual report handlers
    private void handleSalesSummary(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        System.out.println("🔍 Handling sales summary request");

        String period = req.getParameter("period");
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");

        if (period == null || period.trim().isEmpty()) {
            period = "today"; // default
        }

        System.out.println("🔍 Sales summary params - period: " + period + ", startDate: " + startDate + ", endDate: " + endDate);

        try {
            SalesSummaryDTO summary = salesSummaryUseCase.execute(period, startDate, endDate);

            ApiResponse<SalesSummaryDTO> response = ApiResponse.success(
                "Sales summary retrieved successfully", summary);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Sales summary response sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in handleSalesSummary: " + e.getMessage());
            e.printStackTrace();

            // Return mock data as fallback
            SalesSummaryDTO mockSummary = new SalesSummaryDTO();
            mockSummary.setTotalRevenue(new java.math.BigDecimal("0.00"));
            mockSummary.setTotalOrders(0);
            mockSummary.setSuccessfulOrders(0);
            mockSummary.setCancelledOrders(0);
            mockSummary.setAvgOrderValue(new java.math.BigDecimal("0.00"));
            mockSummary.setPeriod(period);
            mockSummary.setStartDate(java.time.LocalDate.now());
            mockSummary.setEndDate(java.time.LocalDate.now());

            SalesSummaryDTO.Comparison comparison = new SalesSummaryDTO.Comparison(
                new java.math.BigDecimal("0.00"), 0.0);
            mockSummary.setComparison(comparison);

            ApiResponse<SalesSummaryDTO> response = ApiResponse.success(
                "Sales summary retrieved (mock data due to error)", mockSummary);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("⚠️ Sales summary mock response sent due to error");
        }
    }

    private void handleTopProducts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        System.out.println("🔍 Handling top products request");

        String period = req.getParameter("period");
        String limitStr = req.getParameter("limit");
        String sortBy = req.getParameter("sortBy");

        if (period == null || period.trim().isEmpty()) {
            period = "today"; // default
        }

        int limit = 10; // default
        if (limitStr != null && !limitStr.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                limit = 10; // fallback to default
            }
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "quantity"; // default
        }

        System.out.println("🔍 Top products params - period: " + period + ", limit: " + limit + ", sortBy: " + sortBy);

        try {
            List<TopProductDTO> topProducts = topProductsUseCase.execute(period, limit, sortBy);

            ApiResponse<List<TopProductDTO>> response = ApiResponse.success(
                "Top products retrieved successfully", topProducts);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Top products response sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in handleTopProducts: " + e.getMessage());
            e.printStackTrace();

            // Return mock data as fallback
            List<TopProductDTO> mockProducts = new ArrayList<>();
            TopProductDTO mockProduct = new TopProductDTO();
            mockProduct.setProductCode("MOCK001");
            mockProduct.setProductName("Sample Product");
            mockProduct.setQuantitySold(0);
            mockProduct.setRevenue(new java.math.BigDecimal("0.00"));
            mockProduct.setAvgPrice(new java.math.BigDecimal("0.00"));
            mockProduct.setRank(1);
            mockProducts.add(mockProduct);

            ApiResponse<List<TopProductDTO>> response = ApiResponse.success(
                "Top products retrieved (mock data due to error)", mockProducts);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("⚠️ Top products mock response sent due to error");
        }
    }

    private void handleCategoryPerformance(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        System.out.println("🔍 Handling category performance request");

        String period = req.getParameter("period");
        if (period == null || period.trim().isEmpty()) {
            period = "today"; // default
        }

        try {
            // Parse period to get date range
            LocalDate[] dates = parsePeriodDates(period);

            List<CategoryPerformanceDTO> categories = reportsDao.getCategoryPerformance(dates[0], dates[1]);

            ApiResponse<List<CategoryPerformanceDTO>> response = ApiResponse.success(
                "Category performance retrieved successfully", categories);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Category performance response sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in handleCategoryPerformance: " + e.getMessage());
            e.printStackTrace();

            // Return mock data as fallback
            List<CategoryPerformanceDTO> mockCategories = new ArrayList<>();
            CategoryPerformanceDTO mockCategory = new CategoryPerformanceDTO();
            mockCategory.setCategoryId(1);
            mockCategory.setCategoryName("Sample Category");
            mockCategory.setRevenue(new java.math.BigDecimal("0.00"));
            mockCategory.setItemsSold(0);
            mockCategory.setOrderCount(0);
            mockCategory.setPercentageOfTotal(0.0);
            mockCategories.add(mockCategory);

            ApiResponse<List<CategoryPerformanceDTO>> response = ApiResponse.success(
                "Category performance retrieved (mock data due to error)", mockCategories);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("⚠️ Category performance mock response sent due to error");
        }
    }

    private void handleHourlyAnalysis(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        String dateStr = req.getParameter("date");
        String period = req.getParameter("period");

        LocalDate[] dates;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            // Specific date
            LocalDate date = LocalDate.parse(dateStr);
            dates = new LocalDate[]{date, date};
        } else if (period != null && !period.trim().isEmpty()) {
            // Period-based
            dates = parsePeriodDates(period);
        } else {
            // Default to today
            LocalDate today = LocalDate.now();
            dates = new LocalDate[]{today, today};
        }

        try {
            List<PeakHoursDTO> peakHours = reportsDao.getPeakHoursAnalysis(dates[0], dates[1]);

            ApiResponse<List<PeakHoursDTO>> response = ApiResponse.success(
                "Peak hours analysis retrieved successfully", peakHours);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Peak hours response sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in handleHourlyAnalysis: " + e.getMessage());
            e.printStackTrace();

            // Return mock data as fallback
            List<PeakHoursDTO> mockHours = new ArrayList<>();
            PeakHoursDTO mockHour = new PeakHoursDTO();
            mockHour.setHour(12);
            mockHour.setHourLabel("12:00 PM - 1:00 PM");
            mockHour.setOrderCount(0);
            mockHour.setRevenue(new java.math.BigDecimal("0.00"));
            mockHour.setAvgOrderValue(new java.math.BigDecimal("0.00"));
            mockHours.add(mockHour);

            ApiResponse<List<PeakHoursDTO>> response = ApiResponse.success(
                "Peak hours analysis retrieved (mock data due to error)", mockHours);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("⚠️ Peak hours mock response sent due to error");
        }
    }

    private void handleInventoryAlerts(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        System.out.println("🔍 Handling inventory alerts request");

        try {
            InventoryAlertsDTO alerts = inventoryAlertsUseCase.execute();

            ApiResponse<InventoryAlertsDTO> response = ApiResponse.success(
                "Inventory alerts retrieved successfully", alerts);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("✅ Inventory alerts response sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in handleInventoryAlerts: " + e.getMessage());
            e.printStackTrace();

            // Return mock data as fallback
            InventoryAlertsDTO mockAlerts = new InventoryAlertsDTO();
            mockAlerts.setLowStock(new ArrayList<>());
            mockAlerts.setExpiringSoon(new ArrayList<>());
            mockAlerts.setOutOfStock(new ArrayList<>());

            InventoryAlertsDTO.AlertSummary summary = new InventoryAlertsDTO.AlertSummary(0, 0, 0);
            mockAlerts.setSummary(summary);

            ApiResponse<InventoryAlertsDTO> response = ApiResponse.success(
                "Inventory alerts retrieved (mock data due to error)", mockAlerts);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

            System.out.println("⚠️ Inventory alerts mock response sent due to error");
        }
    }

    private void handleCashierPerformance(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {

        String period = req.getParameter("period");
        String cashierId = req.getParameter("cashierId");

        if (period == null || period.trim().isEmpty()) {
            period = "today"; // default
        }

        LocalDate[] dates = parsePeriodDates(period);

        List<CashierPerformanceDTO> cashiers = reportsDao.getCashierPerformance(dates[0], dates[1], cashierId);

        ApiResponse<List<CashierPerformanceDTO>> response = ApiResponse.success(
            "Cashier performance retrieved successfully", cashiers);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(response));
    }

    // Helper methods
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
                startDate = today;
                endDate = today;
                break;
        }

        return new LocalDate[]{startDate, endDate};
    }

    private void handleInvalidEndpoint(HttpServletResponse resp) throws IOException {
        ApiResponse<Object> response = ApiResponse.error("Invalid report endpoint");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(gson.toJson(response));
    }

    private void handleError(HttpServletResponse resp, String message, Exception e) throws IOException {
        System.err.println("Error in ApiReportsServlet: " + message);
        e.printStackTrace();

        ApiResponse<Object> response = ApiResponse.error(message);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}


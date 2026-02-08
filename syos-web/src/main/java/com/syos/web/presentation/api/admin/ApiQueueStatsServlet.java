package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.concurrency.BillQueueService;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 📊 Queue Stats Servlet - For monitoring bill processing queue
 * GET /api/admin/queue-stats → Get queue statistics
 */
@WebServlet("/api/admin/queue-stats")
public class ApiQueueStatsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();

    @Override
    public void init() {
        System.out.println("✅ ApiQueueStatsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check authentication
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            ApiResponse<Object> error = ApiResponse.error("Unauthorized - Please login");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        try {
            // Get queue stats
            BillQueueService.QueueStats stats = BillQueueService.getInstance().getStats();

            // Build response
            Map<String, Object> statsMap = new HashMap<>();
            statsMap.put("queueSize", stats.queueSize);
            statsMap.put("remainingCapacity", stats.remainingCapacity);
            statsMap.put("totalCapacity", stats.queueSize + stats.remainingCapacity);
            statsMap.put("numWorkers", stats.numWorkers);
            statsMap.put("utilizationPercent", calculateUtilization(stats));
            statsMap.put("status", getQueueStatus(stats));

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                    "Queue statistics retrieved successfully",
                    statsMap
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to get queue stats: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            System.err.println("❌ Error in ApiQueueStatsServlet: " + e.getMessage());
        }
    }

    private double calculateUtilization(BillQueueService.QueueStats stats) {
        int total = stats.queueSize + stats.remainingCapacity;
        if (total == 0) return 0.0;
        return (double) stats.queueSize / total * 100;
    }

    private String getQueueStatus(BillQueueService.QueueStats stats) {
        double utilization = calculateUtilization(stats);
        if (utilization < 25) return "LOW";
        if (utilization < 50) return "MODERATE";
        if (utilization < 75) return "HIGH";
        return "CRITICAL";
    }
}


package com.syos.web.application.usecases.reports;

import com.syos.web.application.dto.reports.InventoryAlertsDTO;
import com.syos.web.infrastructure.persistence.dao.ReportsDao;

import java.sql.SQLException;

/**
 * Inventory Alerts Use Case
 * Handles business logic for inventory alerts and monitoring
 */
public class InventoryAlertsUseCase {

    private final ReportsDao reportsDao;

    public InventoryAlertsUseCase(ReportsDao reportsDao) {
        this.reportsDao = reportsDao;
    }

    public InventoryAlertsDTO execute() throws SQLException {
        return reportsDao.getInventoryAlerts();
    }
}

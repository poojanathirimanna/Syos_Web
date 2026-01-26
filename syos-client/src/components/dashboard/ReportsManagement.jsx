import { useState } from "react";

export default function ReportsManagement() {
    const [selectedPeriod, setSelectedPeriod] = useState("today");
    const [selectedReport, setSelectedReport] = useState("sales");

    // Mock data for demonstration
    const summaryData = {
        totalRevenue: "₱125,450.00",
        totalOrders: 248,
        avgOrderValue: "₱506.25",
        topProduct: "Basmati Rice 1kg",
        lowStock: 5,
        expiringSoon: 3
    };

    return (
        <>
            <style>{`
                .reports-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .reports-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .reports-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .filter-section {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 24px;
                    background: white;
                    padding: 16px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }

                .filter-group {
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                }

                .filter-label {
                    font-size: 13px;
                    font-weight: 600;
                    color: #666;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }

                .filter-select {
                    padding: 10px 16px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                    cursor: pointer;
                    transition: all 0.2s;
                    background: white;
                    min-width: 180px;
                }

                .filter-select:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .summary-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                    gap: 20px;
                    margin-bottom: 32px;
                }

                .summary-card {
                    background: linear-gradient(135deg, #ffffff 0%, #f9fafb 100%);
                    padding: 28px;
                    border-radius: 16px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                    border: 1px solid rgba(0,0,0,0.05);
                    transition: all 0.3s ease;
                    position: relative;
                    overflow: hidden;
                }

                .summary-card::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 4px;
                    background: linear-gradient(90deg, #52B788 0%, #40916C 100%);
                }

                .summary-card:hover {
                    transform: translateY(-4px);
                    box-shadow: 0 8px 20px rgba(82, 183, 136, 0.15);
                }

                .summary-card-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-bottom: 16px;
                }

                .summary-card-icon {
                    width: 48px;
                    height: 48px;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 24px;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .summary-card-label {
                    font-size: 13px;
                    color: #888;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    margin-bottom: 8px;
                }

                .summary-card-value {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1a1a1a;
                    line-height: 1;
                    margin-bottom: 12px;
                }

                .summary-card-trend {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    font-size: 13px;
                    font-weight: 600;
                }

                .trend-positive {
                    color: #16a34a;
                }

                .trend-negative {
                    color: #dc2626;
                }

                .trend-neutral {
                    color: #888;
                }

                .report-cards {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
                    gap: 24px;
                    margin-bottom: 24px;
                }

                .report-card {
                    background: white;
                    border-radius: 16px;
                    padding: 28px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                    border: 1px solid rgba(0,0,0,0.05);
                }

                .report-card-title {
                    font-size: 18px;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 20px;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .chart-placeholder {
                    width: 100%;
                    height: 250px;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: #52B788;
                    font-size: 16px;
                    font-weight: 600;
                    border: 2px dashed #52B788;
                }

                .data-table {
                    width: 100%;
                    margin-top: 20px;
                }

                .data-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 12px 0;
                    border-bottom: 1px solid #f0f0f0;
                }

                .data-row:last-child {
                    border-bottom: none;
                }

                .data-label {
                    font-size: 14px;
                    color: #666;
                    font-weight: 500;
                }

                .data-value {
                    font-size: 14px;
                    font-weight: 700;
                    color: #333;
                }

                .btn-export {
                    padding: 12px 24px;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    border-radius: 10px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 600;
                    transition: all 0.3s;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .btn-export:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .quick-actions {
                    display: flex;
                    gap: 12px;
                    flex-wrap: wrap;
                }

                .action-btn {
                    padding: 10px 20px;
                    background: white;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 600;
                    transition: all 0.2s;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .action-btn:hover {
                    border-color: #52B788;
                    color: #52B788;
                    transform: translateY(-2px);
                }
            `}</style>

            <div className="reports-management">
                <div className="reports-header">
                    <h1 className="reports-title">
                        <span>📈</span>
                        Reports & Analytics
                    </h1>
                    <button className="btn-export">
                        📥 Export Report
                    </button>
                </div>

                <div className="filter-section">
                    <div className="filter-group">
                        <label className="filter-label">Time Period</label>
                        <select 
                            className="filter-select"
                            value={selectedPeriod}
                            onChange={(e) => setSelectedPeriod(e.target.value)}
                        >
                            <option value="today">Today</option>
                            <option value="yesterday">Yesterday</option>
                            <option value="week">This Week</option>
                            <option value="month">This Month</option>
                            <option value="year">This Year</option>
                            <option value="custom">Custom Range</option>
                        </select>
                    </div>

                    <div className="filter-group">
                        <label className="filter-label">Report Type</label>
                        <select 
                            className="filter-select"
                            value={selectedReport}
                            onChange={(e) => setSelectedReport(e.target.value)}
                        >
                            <option value="sales">Sales Report</option>
                            <option value="inventory">Inventory Report</option>
                            <option value="products">Product Performance</option>
                            <option value="cashiers">Cashier Performance</option>
                        </select>
                    </div>

                    <div className="filter-group">
                        <label className="filter-label">Quick Actions</label>
                        <div className="quick-actions">
                            <button className="action-btn">🖨️ Print</button>
                            <button className="action-btn">📧 Email</button>
                        </div>
                    </div>
                </div>

                <div className="summary-grid">
                    <div className="summary-card">
                        <div className="summary-card-icon">💰</div>
                        <div className="summary-card-label">Total Revenue</div>
                        <div className="summary-card-value">{summaryData.totalRevenue}</div>
                        <div className="summary-card-trend trend-positive">
                            ↗ +12.5% from last period
                        </div>
                    </div>

                    <div className="summary-card">
                        <div className="summary-card-icon">🛒</div>
                        <div className="summary-card-label">Total Orders</div>
                        <div className="summary-card-value">{summaryData.totalOrders}</div>
                        <div className="summary-card-trend trend-positive">
                            ↗ +8.3% from last period
                        </div>
                    </div>

                    <div className="summary-card">
                        <div className="summary-card-icon">📊</div>
                        <div className="summary-card-label">Avg Order Value</div>
                        <div className="summary-card-value">{summaryData.avgOrderValue}</div>
                        <div className="summary-card-trend trend-neutral">
                            → No change
                        </div>
                    </div>

                    <div className="summary-card">
                        <div className="summary-card-icon">⭐</div>
                        <div className="summary-card-label">Top Product</div>
                        <div className="summary-card-value" style={{fontSize: '20px'}}>{summaryData.topProduct}</div>
                        <div className="summary-card-trend trend-positive">
                            127 units sold
                        </div>
                    </div>

                    <div className="summary-card">
                        <div className="summary-card-icon">⚠️</div>
                        <div className="summary-card-label">Low Stock Items</div>
                        <div className="summary-card-value">{summaryData.lowStock}</div>
                        <div className="summary-card-trend trend-negative">
                            Requires attention
                        </div>
                    </div>

                    <div className="summary-card">
                        <div className="summary-card-icon">⏰</div>
                        <div className="summary-card-label">Expiring Soon</div>
                        <div className="summary-card-value">{summaryData.expiringSoon}</div>
                        <div className="summary-card-trend trend-negative">
                            Within 30 days
                        </div>
                    </div>
                </div>

                <div className="report-cards">
                    <div className="report-card">
                        <div className="report-card-title">
                            <span>📈</span>
                            Sales Trend
                        </div>
                        <div className="chart-placeholder">
                            Sales Chart (Coming Soon)
                        </div>
                    </div>

                    <div className="report-card">
                        <div className="report-card-title">
                            <span>🏆</span>
                            Top Products
                        </div>
                        <div className="data-table">
                            <div className="data-row">
                                <span className="data-label">1. Basmati Rice 1kg</span>
                                <span className="data-value">127 units</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">2. White Sugar 1kg</span>
                                <span className="data-value">98 units</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">3. Cooking Oil 1L</span>
                                <span className="data-value">85 units</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">4. Salt 500g</span>
                                <span className="data-value">76 units</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">5. Wheat Flour 1kg</span>
                                <span className="data-value">62 units</span>
                            </div>
                        </div>
                    </div>

                    <div className="report-card">
                        <div className="report-card-title">
                            <span>📊</span>
                            Category Performance
                        </div>
                        <div className="chart-placeholder">
                            Category Chart (Coming Soon)
                        </div>
                    </div>

                    <div className="report-card">
                        <div className="report-card-title">
                            <span>⏱️</span>
                            Peak Hours
                        </div>
                        <div className="data-table">
                            <div className="data-row">
                                <span className="data-label">9:00 AM - 11:00 AM</span>
                                <span className="data-value">45 orders</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">1:00 PM - 3:00 PM</span>
                                <span className="data-value">67 orders</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">5:00 PM - 7:00 PM</span>
                                <span className="data-value">89 orders</span>
                            </div>
                            <div className="data-row">
                                <span className="data-label">7:00 PM - 9:00 PM</span>
                                <span className="data-value">47 orders</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

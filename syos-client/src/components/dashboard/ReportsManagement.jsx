import { useState, useEffect } from "react";
import { 
    apiGetSalesReport, 
    apiGetTopProducts, 
    apiGetInventoryAlerts,
    apiGetCategoryPerformance,
    apiGetPeakHours 
} from "../../services/api";

export default function ReportsManagement() {
    const [selectedPeriod, setSelectedPeriod] = useState("today");
    const [selectedReport, setSelectedReport] = useState("sales");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // Report data states
    const [salesData, setSalesData] = useState(null);
    const [topProducts, setTopProducts] = useState([]);
    const [inventoryAlerts, setInventoryAlerts] = useState({ lowStock: [], expiringSoon: [] });
    const [categoryPerformance, setCategoryPerformance] = useState([]);
    const [peakHours, setPeakHours] = useState([]);

    useEffect(() => {
        loadReports();
    }, [selectedPeriod]);

    const loadReports = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const [salesResult, topProductsResult, alertsResult, categoryResult, peakHoursResult] = await Promise.all([
                apiGetSalesReport(selectedPeriod),
                apiGetTopProducts(5, selectedPeriod),
                apiGetInventoryAlerts(),
                apiGetCategoryPerformance(selectedPeriod),
                apiGetPeakHours(selectedPeriod)
            ]);

            if (salesResult.success) {
                setSalesData(salesResult.data);
            }
            if (topProductsResult.success) {
                setTopProducts(topProductsResult.data);
            }
            if (alertsResult.success) {
                setInventoryAlerts(alertsResult.data);
            }
            if (categoryResult.success) {
                setCategoryPerformance(categoryResult.data);
            }
            if (peakHoursResult.success) {
                setPeakHours(peakHoursResult.data);
            }
        } catch (err) {
            console.error("Failed to load reports:", err);
            setError("Failed to load reports. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return `LKR ${(amount || 0).toFixed(2)}`;
    };

    const exportReport = () => {
        // Generate CSV content
        const csvContent = generateCSVReport();
        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `SYOS_Report_${selectedPeriod}_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    };

    const generateCSVReport = () => {
        if (!salesData) return '';
        
        let csv = 'SYOS Business Report\n';
        csv += `Period: ${selectedPeriod}\n`;
        csv += `Generated: ${new Date().toLocaleString()}\n\n`;
        
        csv += 'Sales Summary\n';
        csv += 'Metric,Value\n';
        csv += `Total Revenue,${salesData.totalRevenue}\n`;
        csv += `Total Orders,${salesData.totalOrders}\n`;
        csv += `Average Order Value,${salesData.avgOrderValue.toFixed(2)}\n\n`;
        
        csv += 'Top Products\n';
        csv += 'Product Name,Quantity Sold,Revenue\n';
        topProducts.forEach(p => {
            csv += `${p.productName},${p.quantitySold},${p.revenue}\n`;
        });
        
        return csv;
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

                .loading-container {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 400px;
                }

                .loading-spinner {
                    font-size: 18px;
                    color: #52B788;
                    font-weight: 600;
                }

                .error-message {
                    background: #fee2e2;
                    border: 2px solid #ef4444;
                    color: #dc2626;
                    padding: 16px 20px;
                    border-radius: 12px;
                    margin-bottom: 24px;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .no-data {
                    text-align: center;
                    padding: 40px 20px;
                    color: #9ca3af;
                    font-style: italic;
                }

                .alert-section-title {
                    font-weight: 700;
                    color: #ef4444;
                    font-size: 13px;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    margin-bottom: 12px;
                    padding-bottom: 8px;
                    border-bottom: 2px solid #fee2e2;
                }

                .alert-value {
                    color: #ef4444 !important;
                }

                .btn-export:disabled,
                .action-btn:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                    transform: none !important;
                }

                @media print {
                    .reports-header button,
                    .filter-section,
                    .action-btn {
                        display: none;
                    }
                }
            `}</style>

            <div className="reports-management">
                <div className="reports-header">
                    <h1 className="reports-title">
                        <span>📈</span>
                        Reports & Analytics
                    </h1>
                    <button className="btn-export" onClick={exportReport} disabled={loading}>
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
                            disabled={loading}
                        >
                            <option value="today">Today</option>
                            <option value="yesterday">Yesterday</option>
                            <option value="week">This Week</option>
                            <option value="month">This Month</option>
                            <option value="year">This Year</option>
                        </select>
                    </div>

                    <div className="filter-group">
                        <label className="filter-label">Quick Actions</label>
                        <div className="quick-actions">
                            <button className="action-btn" onClick={() => window.print()}>🖨️ Print</button>
                            <button className="action-btn" onClick={loadReports} disabled={loading}>
                                🔄 Refresh
                            </button>
                        </div>
                    </div>
                </div>

                {error && (
                    <div className="error-message">
                        ⚠️ {error}
                    </div>
                )}

                {loading ? (
                    <div className="loading-container">
                        <div className="loading-spinner">⏳ Loading reports...</div>
                    </div>
                ) : (
                    <>
                        <div className="summary-grid">
                            <div className="summary-card">
                                <div className="summary-card-icon">💰</div>
                                <div className="summary-card-label">Total Revenue</div>
                                <div className="summary-card-value">
                                    {formatCurrency(salesData?.totalRevenue || 0)}
                                </div>
                                <div className="summary-card-trend trend-neutral">
                                    {salesData?.totalOrders || 0} orders this period
                                </div>
                            </div>

                            <div className="summary-card">
                                <div className="summary-card-icon">🛒</div>
                                <div className="summary-card-label">Total Orders</div>
                                <div className="summary-card-value">{salesData?.totalOrders || 0}</div>
                            </div>

                            <div className="summary-card">
                                <div className="summary-card-icon">📊</div>
                                <div className="summary-card-label">Avg Order Value</div>
                                <div className="summary-card-value">
                                    {formatCurrency(salesData?.avgOrderValue || 0)}
                                </div>
                                <div className="summary-card-trend trend-neutral">
                                    Per transaction
                                </div>
                            </div>

                            <div className="summary-card">
                                <div className="summary-card-icon">⭐</div>
                                <div className="summary-card-label">Top Product</div>
                                <div className="summary-card-value" style={{fontSize: '18px'}}>
                                    {topProducts[0]?.productName || 'No data'}
                                </div>
                                <div className="summary-card-trend trend-positive">
                                    {topProducts[0]?.quantitySold || 0} units sold
                                </div>
                            </div>

                            <div className="summary-card">
                                <div className="summary-card-icon">⚠️</div>
                                <div className="summary-card-label">Low Stock Items</div>
                                <div className="summary-card-value">{inventoryAlerts.lowStockCount || 0}</div>
                                <div className={`summary-card-trend ${inventoryAlerts.lowStockCount > 0 ? 'trend-negative' : 'trend-positive'}`}>
                                    {inventoryAlerts.lowStockCount > 0 ? 'Requires attention' : 'All good'}
                                </div>
                            </div>

                            <div className="summary-card">
                                <div className="summary-card-icon">⏰</div>
                                <div className="summary-card-label">Expiring Soon</div>
                                <div className="summary-card-value">{inventoryAlerts.expiringSoonCount || 0}</div>
                                <div className={`summary-card-trend ${inventoryAlerts.expiringSoonCount > 0 ? 'trend-negative' : 'trend-positive'}`}>
                                    {inventoryAlerts.expiringSoonCount > 0 ? 'Within 30 days' : 'All good'}
                                </div>
                            </div>
                        </div>

                        <div className="report-cards">
                            <div className="report-card">
                                <div className="report-card-title">
                                    <span>🏆</span>
                                    Top Products
                                </div>
                                <div className="data-table">
                                    {topProducts.length > 0 ? (
                                        topProducts.map((product, index) => (
                                            <div key={product.productCode} className="data-row">
                                                <span className="data-label">
                                                    {index + 1}. {product.productName}
                                                </span>
                                                <span className="data-value">{product.quantitySold} units</span>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="no-data">No product data for this period</div>
                                    )}
                                </div>
                            </div>

                            <div className="report-card">
                                <div className="report-card-title">
                                    <span>📊</span>
                                    Category Performance
                                </div>
                                <div className="data-table">
                                    {categoryPerformance.length > 0 ? (
                                        categoryPerformance.slice(0, 5).map((cat) => (
                                            <div key={cat.categoryId} className="data-row">
                                                <span className="data-label">{cat.categoryName}</span>
                                                <span className="data-value">{formatCurrency(cat.revenue)}</span>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="no-data">No category data for this period</div>
                                    )}
                                </div>
                            </div>

                            <div className="report-card">
                                <div className="report-card-title">
                                    <span>⏱️</span>
                                    Peak Hours
                                </div>
                                <div className="data-table">
                                    {peakHours.length > 0 ? (
                                        peakHours.slice(0, 5).map((hour) => (
                                            <div key={hour.hour} className="data-row">
                                                <span className="data-label">{hour.hourLabel}</span>
                                                <span className="data-value">{hour.orderCount} orders</span>
                                            </div>
                                        ))
                                    ) : (
                                        <div className="no-data">No sales data for this period</div>
                                    )}
                                </div>
                            </div>

                            <div className="report-card">
                                <div className="report-card-title">
                                    <span>⚠️</span>
                                    Inventory Alerts
                                </div>
                                <div className="data-table">
                                    {inventoryAlerts.lowStock.length > 0 ? (
                                        <>
                                            <div className="alert-section-title">Low Stock</div>
                                            {inventoryAlerts.lowStock.slice(0, 3).map((item) => (
                                                <div key={item.productCode} className="data-row">
                                                    <span className="data-label">{item.productName || item.name}</span>
                                                    <span className="data-value alert-value">
                                                        {item.availableQuantity} left
                                                    </span>
                                                </div>
                                            ))}
                                        </>
                                    ) : (
                                        <div className="no-data">No low stock items</div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </>
    );
}

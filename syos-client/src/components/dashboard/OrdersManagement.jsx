import { useState, useEffect } from "react";
import { apiGetAllBills } from "../../services/api";

export default function OrdersManagement() {
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchQuery, setSearchQuery] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    useEffect(() => {
        loadBills();
    }, []);

    const loadBills = async () => {
        try {
            setLoading(true);
            setError("");
            const response = await apiGetAllBills();
            console.log('📋 Bills API Response:', response);
            if (response.success) {
                const billsData = response.data || [];
                console.log('📋 Bills loaded:', billsData.length);
                console.log('📋 Sample bill data:', billsData.slice(0, 3).map(b => ({ 
                    billNumber: b.billNumber,
                    cashierId: b.cashierId,
                    cashierName: b.cashierName,
                    customerId: b.customerId,
                    customerName: b.customerName,
                    userId: b.userId,
                    status: b.status 
                })));
                setBills(billsData);
            } else {
                setError(response.message || "Failed to load bills");
            }
        } catch (err) {
            setError("Error loading bills: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    // Filter bills
    const filteredBills = bills.filter(bill => {
        const matchesSearch = bill.billNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            bill.cashierName?.toLowerCase().includes(searchQuery.toLowerCase());
        let billStatus = (bill.orderStatus || bill.status || 'completed').toLowerCase();
        // Map PENDING to COMPLETED
        if (billStatus === 'pending') billStatus = 'completed';
        const matchesStatus = statusFilter === "all" || billStatus === statusFilter;
        return matchesSearch && matchesStatus;
    });

    // Pagination
    const totalPages = Math.ceil(filteredBills.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedBills = filteredBills.slice(startIndex, endIndex);

    // Status counts
    const statusCounts = {
        all: bills.length,
        pending: 0, // PENDING is treated as COMPLETED
        processing: bills.filter(b => (b.orderStatus || b.status)?.toLowerCase() === "processing").length,
        completed: bills.filter(b => {
            const status = (b.orderStatus || b.status || 'completed').toLowerCase();
            return status === 'completed' || status === 'pending';
        }).length,
        cancelled: bills.filter(b => (b.orderStatus || b.status)?.toLowerCase() === "cancelled").length,
    };

    // Analytics calculations
    const completedBills = bills.filter(b => {
        const status = (b.orderStatus || b.status || 'completed').toLowerCase();
        return status === 'completed' || status === 'pending';
    });
    const totalRevenue = completedBills.reduce((sum, bill) => sum + Number(bill.totalAmount || 0), 0);
    const averageBillValue = completedBills.length > 0 ? totalRevenue / completedBills.length : 0;
    
    // Today's bills
    const today = new Date().toISOString().split('T')[0];
    const todayBills = bills.filter(bill => {
        const billDate = bill.billDate || bill.createdAt || '';
        return billDate.startsWith(today);
    });
    const todayRevenue = todayBills
        .filter(b => {
            const status = (b.orderStatus || b.status || 'completed').toLowerCase();
            return status === 'completed' || status === 'pending';
        })
        .reduce((sum, bill) => sum + Number(bill.totalAmount || 0), 0);

    const getStatusBadge = (status) => {
        const styles = {
            pending: { bg: "linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)", color: "#92400e", border: "#fbbf24" },
            processing: { bg: "linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%)", color: "#1e40af", border: "#60a5fa" },
            completed: { bg: "linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)", color: "#065f46", border: "#34d399" },
            cancelled: { bg: "linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)", color: "#991b1b", border: "#f87171" },
        };
        return styles[status] || styles.completed;
    };

    return (
        <>
            <style>{`
                .orders-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .orders-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .btn-refresh {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 10px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.3s ease;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .btn-refresh:hover {
                    background: linear-gradient(135deg, #40916C 0%, #2d6a4f 100%);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                    transform: translateY(-2px);
                }

                .btn-refresh:active {
                    transform: translateY(0);
                }

                .btn-refresh:disabled {
                    opacity: 0.6;
                    cursor: not-allowed;
                    transform: none;
                }

                .orders-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .summary-stats {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 16px;
                    margin-bottom: 24px;
                }

                .stat-card {
                    background: linear-gradient(135deg, #ffffff 0%, #f9fafb 100%);
                    padding: 20px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    border: 1px solid rgba(0,0,0,0.05);
                    transition: all 0.3s ease;
                    position: relative;
                    overflow: hidden;
                }

                .stat-card::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 3px;
                    background: linear-gradient(90deg, #52B788 0%, #40916C 100%);
                }

                .stat-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.15);
                }

                .stat-icon {
                    font-size: 32px;
                    margin-bottom: 8px;
                }

                .stat-label {
                    font-size: 12px;
                    color: #888;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    margin-bottom: 8px;
                }

                .stat-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #1a1a1a;
                    margin-bottom: 4px;
                }

                .stat-subtitle {
                    font-size: 13px;
                    color: #666;
                    font-weight: 500;
                }

                .controls-section {
                    background: white;
                    padding: 20px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    margin-bottom: 24px;
                    display: flex;
                    gap: 16px;
                    align-items: center;
                    flex-wrap: wrap;
                }

                .search-box {
                    flex: 1;
                    min-width: 280px;
                    position: relative;
                }

                .search-input {
                    width: 100%;
                    padding: 12px 16px 12px 40px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                    transition: all 0.2s;
                }

                .search-input:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .search-icon {
                    position: absolute;
                    left: 14px;
                    top: 50%;
                    transform: translateY(-50%);
                    font-size: 16px;
                }

                .filter-tabs {
                    display: flex;
                    gap: 8px;
                    flex-wrap: wrap;
                }

                .filter-tab {
                    padding: 10px 20px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 13px;
                    font-weight: 600;
                    transition: all 0.2s;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }

                .filter-tab:hover {
                    border-color: #52B788;
                    color: #52B788;
                }

                .filter-tab.active {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border-color: #52B788;
                }

                .orders-table-container {
                    background: white;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    overflow: hidden;
                }

                .orders-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .orders-table thead {
                    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                }

                .orders-table th {
                    padding: 16px;
                    text-align: left;
                    font-size: 13px;
                    font-weight: 700;
                    color: #555;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    border-bottom: 2px solid #e0e0e0;
                }

                .orders-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f0f0f0;
                    font-size: 14px;
                    color: #333;
                }

                .orders-table tbody tr {
                    transition: all 0.2s;
                }

                .orders-table tbody tr:hover {
                    background: #f9fafb;
                }

                .order-id {
                    font-weight: 700;
                    color: #52B788;
                }

                .status-badge {
                    display: inline-block;
                    padding: 6px 14px;
                    border-radius: 20px;
                    font-size: 12px;
                    font-weight: 700;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    border: 2px solid;
                }

                .action-buttons {
                    display: flex;
                    gap: 8px;
                }

                .btn-action {
                    padding: 8px 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 13px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-action:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                }

                .btn-view {
                    border-color: #3b82f6;
                    color: #3b82f6;
                }

                .btn-view:hover {
                    background: #3b82f6;
                    color: white;
                }

                .pagination {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 20px 24px;
                    background: white;
                    border-top: 2px solid #f0f0f0;
                }

                .pagination-info {
                    font-size: 14px;
                    color: #666;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .items-per-page {
                    padding: 8px 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 6px;
                    font-size: 14px;
                    cursor: pointer;
                    background: white;
                }

                .pagination-controls {
                    display: flex;
                    gap: 8px;
                }

                .page-btn {
                    padding: 8px 14px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 600;
                    transition: all 0.2s;
                    min-width: 40px;
                }

                .page-btn:hover:not(:disabled) {
                    border-color: #52B788;
                    color: #52B788;
                }

                .page-btn.active {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border-color: #52B788;
                }

                .page-btn:disabled {
                    opacity: 0.4;
                    cursor: not-allowed;
                }

                .no-orders {
                    padding: 60px 20px;
                    text-align: center;
                    color: #888;
                    font-size: 16px;
                }
            `}</style>

            <div className="orders-management">
                <div className="orders-header">
                    <h1 className="orders-title">
                        <span>🛒</span>
                        Bills/Orders Management
                    </h1>
                    <button 
                        className="btn-refresh" 
                        onClick={loadBills}
                        disabled={loading}
                    >
                        ↻ Refresh Orders
                    </button>
                </div>

                {error && (
                    <div style={{
                        padding: '16px',
                        marginBottom: '20px',
                        background: 'linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)',
                        color: '#991b1b',
                        borderRadius: '12px',
                        fontWeight: 600
                    }}>
                        ⚠️ {error}
                    </div>
                )}

                {loading ? (
                    <div style={{ textAlign: 'center', padding: '60px', color: '#888' }}>
                        <div style={{ fontSize: '24px', marginBottom: '12px' }}>⏳</div>
                        Loading bills...
                    </div>
                ) : (
                    <>
                        <div className="summary-stats">
                            <div className="stat-card">
                                <div className="stat-icon">💰</div>
                                <div className="stat-label">Total Revenue</div>
                                <div className="stat-value">Rs. {totalRevenue.toFixed(2)}</div>
                                <div className="stat-subtitle">{completedBills.length} completed bills</div>
                            </div>
                            <div className="stat-card">
                                <div className="stat-icon">📈</div>
                                <div className="stat-label">Average Bill Value</div>
                                <div className="stat-value">Rs. {averageBillValue.toFixed(2)}</div>
                                <div className="stat-subtitle">per transaction</div>
                            </div>
                            <div className="stat-card">
                                <div className="stat-icon">📅</div>
                                <div className="stat-label">Today's Bills</div>
                                <div className="stat-value">{todayBills.length}</div>
                                <div className="stat-subtitle">Rs. {todayRevenue.toFixed(2)} revenue</div>
                            </div>
                            <div className="stat-card">
                                <div className="stat-icon">✅</div>
                                <div className="stat-label">Completed</div>
                                <div className="stat-value" style={{color: '#059669'}}>{statusCounts.completed}</div>
                                <div className="stat-subtitle">{statusCounts.all} total bills</div>
                            </div>
                        </div>

                <div className="controls-section">
                    <div className="search-box">
                        <span className="search-icon">🔍</span>
                        <input
                            type="text"
                            className="search-input"
                            placeholder="Search by Bill Number or Cashier Name..."
                            value={searchQuery}
                            onChange={(e) => {
                                setSearchQuery(e.target.value);
                                setCurrentPage(1);
                            }}
                        />
                    </div>

                    <div className="filter-tabs">
                        <button
                            className={`filter-tab ${statusFilter === "all" ? "active" : ""}`}
                            onClick={() => {
                                setStatusFilter("all");
                                setCurrentPage(1);
                            }}
                        >
                            All <span>({statusCounts.all})</span>
                        </button>
                        <button
                            className={`filter-tab ${statusFilter === "completed" ? "active" : ""}`}
                            onClick={() => {
                                setStatusFilter("completed");
                                setCurrentPage(1);
                            }}
                        >
                            Completed <span>({statusCounts.completed})</span>
                        </button>
                        <button
                            className={`filter-tab ${statusFilter === "cancelled" ? "active" : ""}`}
                            onClick={() => {
                                setStatusFilter("cancelled");
                                setCurrentPage(1);
                            }}
                        >
                            Cancelled <span>({statusCounts.cancelled})</span>
                        </button>
                    </div>
                </div>

                <div className="orders-table-container">
                    <table className="orders-table">
                        <thead>
                            <tr>
                                <th>Bill Number</th>
                                <th>Date & Time</th>
                                <th>Cashier</th>
                                <th>Total Amount</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {paginatedBills.length > 0 ? (
                                paginatedBills.map(bill => {
                                    let billStatus = (bill.orderStatus || bill.status || 'completed').toLowerCase();
                                    // Map PENDING to COMPLETED
                                    if (billStatus === 'pending') billStatus = 'completed';
                                    const statusStyle = getStatusBadge(billStatus);
                                    return (
                                        <tr key={bill.billNumber}>
                                            <td className="order-id">{bill.billNumber}</td>
                                            <td>{bill.billDate || bill.createdAt || 'N/A'}</td>
                                            <td>
                                                {bill.channel === 'ONLINE' ? 
                                                    '🌐 Online Order' : 
                                                    (bill.cashierName || 'N/A')
                                                }
                                            </td>
                                            <td style={{fontWeight: 700}}>Rs. {Number(bill.totalAmount || 0).toFixed(2)}</td>
                                            <td>
                                                <span 
                                                    className="status-badge"
                                                    style={{
                                                        background: statusStyle.bg,
                                                        color: statusStyle.color,
                                                        borderColor: statusStyle.border
                                                    }}
                                                >
                                                    {billStatus.toUpperCase()}
                                                </span>
                                            </td>
                                        </tr>
                                    );
                                })
                            ) : (
                                <tr>
                                    <td colSpan="5" className="no-orders">
                                        {bills.length === 0 ? "No bills found" : "No bills match your search"}
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    {filteredBills.length > 0 && (
                        <div className="pagination">
                            <div className="pagination-info">
                                <span>
                                    Showing {startIndex + 1}-{Math.min(endIndex, filteredBills.length)} of {filteredBills.length}
                                </span>
                                <select
                                    className="items-per-page"
                                    value={itemsPerPage}
                                    onChange={(e) => {
                                        setItemsPerPage(Number(e.target.value));
                                        setCurrentPage(1);
                                    }}
                                >
                                    <option value={5}>5 per page</option>
                                    <option value={10}>10 per page</option>
                                    <option value={25}>25 per page</option>
                                    <option value={50}>50 per page</option>
                                </select>
                            </div>

                            <div className="pagination-controls">
                                <button
                                    className="page-btn"
                                    onClick={() => setCurrentPage(1)}
                                    disabled={currentPage === 1}
                                >
                                    ⏮
                                </button>
                                <button
                                    className="page-btn"
                                    onClick={() => setCurrentPage(prev => prev - 1)}
                                    disabled={currentPage === 1}
                                >
                                    ◀
                                </button>

                                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                                    let pageNum;
                                    if (totalPages <= 5) {
                                        pageNum = i + 1;
                                    } else if (currentPage <= 3) {
                                        pageNum = i + 1;
                                    } else if (currentPage >= totalPages - 2) {
                                        pageNum = totalPages - 4 + i;
                                    } else {
                                        pageNum = currentPage - 2 + i;
                                    }

                                    return (
                                        <button
                                            key={pageNum}
                                            className={`page-btn ${currentPage === pageNum ? "active" : ""}`}
                                            onClick={() => setCurrentPage(pageNum)}
                                        >
                                            {pageNum}
                                        </button>
                                    );
                                })}

                                <button
                                    className="page-btn"
                                    onClick={() => setCurrentPage(prev => prev + 1)}
                                    disabled={currentPage === totalPages}
                                >
                                    ▶
                                </button>
                                <button
                                    className="page-btn"
                                    onClick={() => setCurrentPage(totalPages)}
                                    disabled={currentPage === totalPages}
                                >
                                    ⏭
                                </button>
                            </div>
                        </div>
                    )}
                </div>
                    </>
                )}
            </div>
        </>
    );
}

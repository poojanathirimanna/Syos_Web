import { useState } from "react";

export default function OrdersManagement() {
    const [searchQuery, setSearchQuery] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    // Mock orders data
    const mockOrders = [
        { id: "ORD-001", date: "2024-01-15 14:30", customer: "Juan dela Cruz", items: 5, total: "₱1,250.00", status: "completed" },
        { id: "ORD-002", date: "2024-01-15 15:45", customer: "Maria Santos", items: 3, total: "₱850.00", status: "completed" },
        { id: "ORD-003", date: "2024-01-15 16:20", customer: "Pedro Garcia", items: 8, total: "₱2,100.00", status: "processing" },
        { id: "ORD-004", date: "2024-01-15 17:10", customer: "Ana Reyes", items: 2, total: "₱450.00", status: "pending" },
        { id: "ORD-005", date: "2024-01-15 18:05", customer: "Carlos Lopez", items: 6, total: "₱1,680.00", status: "completed" },
        { id: "ORD-006", date: "2024-01-15 19:15", customer: "Elena Cruz", items: 4, total: "₱920.00", status: "processing" },
        { id: "ORD-007", date: "2024-01-15 20:30", customer: "Miguel Ramos", items: 7, total: "₱1,890.00", status: "pending" },
        { id: "ORD-008", date: "2024-01-15 21:00", customer: "Sofia Mendoza", items: 3, total: "₱750.00", status: "cancelled" },
        { id: "ORD-009", date: "2024-01-16 09:15", customer: "Diego Torres", items: 5, total: "₱1,320.00", status: "completed" },
        { id: "ORD-010", date: "2024-01-16 10:40", customer: "Isabella Flores", items: 9, total: "₱2,450.00", status: "processing" },
        { id: "ORD-011", date: "2024-01-16 11:25", customer: "Lucas Morales", items: 2, total: "₱380.00", status: "completed" },
        { id: "ORD-012", date: "2024-01-16 12:50", customer: "Valentina Diaz", items: 6, total: "₱1,540.00", status: "pending" },
    ];

    // Filter orders
    const filteredOrders = mockOrders.filter(order => {
        const matchesSearch = order.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            order.customer.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesStatus = statusFilter === "all" || order.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    // Pagination
    const totalPages = Math.ceil(filteredOrders.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedOrders = filteredOrders.slice(startIndex, endIndex);

    // Status counts
    const statusCounts = {
        all: mockOrders.length,
        pending: mockOrders.filter(o => o.status === "pending").length,
        processing: mockOrders.filter(o => o.status === "processing").length,
        completed: mockOrders.filter(o => o.status === "completed").length,
        cancelled: mockOrders.filter(o => o.status === "cancelled").length,
    };

    const getStatusBadge = (status) => {
        const styles = {
            pending: { bg: "linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)", color: "#92400e", border: "#fbbf24" },
            processing: { bg: "linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%)", color: "#1e40af", border: "#60a5fa" },
            completed: { bg: "linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)", color: "#065f46", border: "#34d399" },
            cancelled: { bg: "linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)", color: "#991b1b", border: "#f87171" },
        };
        return styles[status] || styles.pending;
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
                        Orders Management
                    </h1>
                </div>

                <div className="summary-stats">
                    <div className="stat-card">
                        <div className="stat-label">Total Orders</div>
                        <div className="stat-value">{statusCounts.all}</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-label">Pending</div>
                        <div className="stat-value" style={{color: '#d97706'}}>{statusCounts.pending}</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-label">Processing</div>
                        <div className="stat-value" style={{color: '#2563eb'}}>{statusCounts.processing}</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-label">Completed</div>
                        <div className="stat-value" style={{color: '#059669'}}>{statusCounts.completed}</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-label">Cancelled</div>
                        <div className="stat-value" style={{color: '#dc2626'}}>{statusCounts.cancelled}</div>
                    </div>
                </div>

                <div className="controls-section">
                    <div className="search-box">
                        <span className="search-icon">🔍</span>
                        <input
                            type="text"
                            className="search-input"
                            placeholder="Search by Order ID or Customer Name..."
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
                            className={`filter-tab ${statusFilter === "pending" ? "active" : ""}`}
                            onClick={() => {
                                setStatusFilter("pending");
                                setCurrentPage(1);
                            }}
                        >
                            Pending <span>({statusCounts.pending})</span>
                        </button>
                        <button
                            className={`filter-tab ${statusFilter === "processing" ? "active" : ""}`}
                            onClick={() => {
                                setStatusFilter("processing");
                                setCurrentPage(1);
                            }}
                        >
                            Processing <span>({statusCounts.processing})</span>
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
                                <th>Order ID</th>
                                <th>Date & Time</th>
                                <th>Customer</th>
                                <th>Items</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {paginatedOrders.length > 0 ? (
                                paginatedOrders.map(order => {
                                    const statusStyle = getStatusBadge(order.status);
                                    return (
                                        <tr key={order.id}>
                                            <td className="order-id">{order.id}</td>
                                            <td>{order.date}</td>
                                            <td>{order.customer}</td>
                                            <td>{order.items} items</td>
                                            <td style={{fontWeight: 700}}>{order.total}</td>
                                            <td>
                                                <span 
                                                    className="status-badge"
                                                    style={{
                                                        background: statusStyle.bg,
                                                        color: statusStyle.color,
                                                        borderColor: statusStyle.border
                                                    }}
                                                >
                                                    {order.status}
                                                </span>
                                            </td>
                                            <td>
                                                <div className="action-buttons">
                                                    <button className="btn-action btn-view">👁️ View</button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })
                            ) : (
                                <tr>
                                    <td colSpan="7" className="no-orders">
                                        No orders found
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    {filteredOrders.length > 0 && (
                        <div className="pagination">
                            <div className="pagination-info">
                                <span>
                                    Showing {startIndex + 1}-{Math.min(endIndex, filteredOrders.length)} of {filteredOrders.length}
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
            </div>
        </>
    );
}

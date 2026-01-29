import { useState, useEffect } from "react";
import { apiGetAllBills, apiGetBillDetails } from "../../services/api";

export default function OrdersManagement() {
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchQuery, setSearchQuery] = useState("");
    const [statusFilter, setStatusFilter] = useState("all");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);
    const [selectedBill, setSelectedBill] = useState(null);
    const [showDetailsModal, setShowDetailsModal] = useState(false);

    useEffect(() => {
        loadBills();
    }, []);

    const loadBills = async () => {
        try {
            setLoading(true);
            setError("");
            const response = await apiGetAllBills();
            if (response.success) {
                setBills(response.data || []);
            } else {
                setError(response.message || "Failed to load bills");
            }
        } catch (err) {
            setError("Error loading bills: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetails = async (billNumber) => {
        try {
            const response = await apiGetBillDetails(billNumber);
            console.log('📄 Bill Details Response:', response);
            if (response.success) {
                console.log('📄 Bill Data:', response.data);
                setSelectedBill(response.data);
                setShowDetailsModal(true);
            } else {
                setError(response.message || "Failed to load bill details");
            }
        } catch (err) {
            console.error('Error loading bill:', err);
            setError("Error loading bill details: " + err.message);
        }
    };

    const downloadBillReceipt = () => {
        if (!selectedBill) return;
        
        const bill = selectedBill;
        const receiptContent = `
========================================
           SYOS SUPERMARKET
          Receipt / Invoice
========================================
Bill Number: ${bill.billNumber}
Date: ${bill.billDate || 'N/A'}
Cashier: ${bill.cashierName || 'N/A'}
========================================

ITEMS:
${bill.items?.map(item => 
    `${item.productName || 'Product'}\n  Qty: ${item.quantity} x Rs. ${Number(item.unitPrice || 0).toFixed(2)} = Rs. ${Number(item.totalPrice || 0).toFixed(2)}`
).join('\n\n') || 'No items'}

========================================
Subtotal: Rs. ${Number(bill.subtotal || 0).toFixed(2)}
Total Amount: Rs. ${Number(bill.totalAmount || 0).toFixed(2)}
Payment Method: ${(bill.paymentMethod || 'Cash').toUpperCase()}
Amount Paid: Rs. ${Number(bill.amountPaid || 0).toFixed(2)}
Change: Rs. ${Number(bill.changeAmount || 0).toFixed(2)}
========================================
Status: ${bill.status || 'Completed'}

     Thank you for shopping with us!
========================================
        `;

        const blob = new Blob([receiptContent], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Receipt_${bill.billNumber}.txt`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    };

    // Filter bills
    const filteredBills = bills.filter(bill => {
        const matchesSearch = bill.billNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            bill.cashierName?.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesStatus = statusFilter === "all" || bill.status === statusFilter;
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
        pending: bills.filter(b => b.status?.toLowerCase() === "pending").length,
        processing: bills.filter(b => b.status?.toLowerCase() === "processing").length,
        completed: bills.filter(b => !b.status || b.status?.toLowerCase() === "completed").length,
        cancelled: bills.filter(b => b.status?.toLowerCase() === "cancelled").length,
    };

    // Analytics calculations
    const completedBills = bills.filter(b => !b.status || b.status?.toLowerCase() === 'completed');
    const totalRevenue = completedBills.reduce((sum, bill) => sum + Number(bill.totalAmount || 0), 0);
    const averageBillValue = completedBills.length > 0 ? totalRevenue / completedBills.length : 0;
    
    // Today's bills
    const today = new Date().toISOString().split('T')[0];
    const todayBills = bills.filter(bill => {
        const billDate = bill.billDate || bill.createdAt || '';
        return billDate.startsWith(today);
    });
    const todayRevenue = todayBills
        .filter(b => !b.status || b.status?.toLowerCase() === 'completed')
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
                                <th>Bill Number</th>
                                <th>Date & Time</th>
                                <th>Cashier</th>
                                <th>Total Amount</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {paginatedBills.length > 0 ? (
                                paginatedBills.map(bill => {
                                    const statusStyle = getStatusBadge(bill.status || 'completed');
                                    return (
                                        <tr key={bill.billNumber}>
                                            <td className="order-id">{bill.billNumber}</td>
                                            <td>{bill.billDate || bill.createdAt || 'N/A'}</td>
                                            <td>{bill.cashierName || bill.cashierId || 'N/A'}</td>
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
                                                    {bill.status || 'completed'}
                                                </span>
                                            </td>
                                            <td>
                                                <div className="action-buttons">
                                                    <button 
                                                        className="btn-action btn-view"
                                                        onClick={() => handleViewDetails(bill.billNumber)}
                                                    >
                                                        👁️ View
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    );
                                })
                            ) : (
                                <tr>
                                    <td colSpan="6" className="no-orders">
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

                {showDetailsModal && selectedBill && (
                    <BillDetailsModal
                        bill={selectedBill}
                        onClose={() => {
                            setShowDetailsModal(false);
                            setSelectedBill(null);
                        }}
                        onDownload={downloadBillReceipt}
                    />
                )}
            </div>
        </>
    );
}

// Bill Details Modal Component
function BillDetailsModal({ bill, onClose, onDownload }) {
    return (
        <>
            <style>{`
                .bill-modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.7);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 9999;
                }

                .bill-modal {
                    background: white;
                    border-radius: 16px;
                    padding: 32px;
                    max-width: 600px;
                    width: 90%;
                    max-height: 85vh;
                    overflow-y: auto;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                }

                .bill-header {
                    text-align: center;
                    border-bottom: 2px solid #52B788;
                    padding-bottom: 16px;
                    margin-bottom: 24px;
                }

                .bill-header h2 {
                    font-size: 24px;
                    color: #333;
                    margin-bottom: 8px;
                }

                .bill-number {
                    font-size: 18px;
                    color: #52B788;
                    font-weight: 700;
                }

                .bill-info {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 12px;
                    margin-bottom: 24px;
                    padding: 16px;
                    background: #f9f9f9;
                    border-radius: 8px;
                }

                .bill-info-item {
                    display: flex;
                    flex-direction: column;
                }

                .bill-info-label {
                    font-size: 12px;
                    color: #666;
                    text-transform: uppercase;
                    margin-bottom: 4px;
                }

                .bill-info-value {
                    font-size: 14px;
                    color: #333;
                    font-weight: 600;
                }

                .bill-items {
                    margin-bottom: 24px;
                }

                .bill-items h3 {
                    font-size: 16px;
                    color: #333;
                    margin-bottom: 12px;
                }

                .bill-item {
                    display: flex;
                    justify-content: space-between;
                    padding: 12px;
                    background: white;
                    border: 1px solid #e0e0e0;
                    border-radius: 8px;
                    margin-bottom: 8px;
                }

                .bill-item-name {
                    font-weight: 600;
                    color: #333;
                }

                .bill-item-details {
                    font-size: 13px;
                    color: #666;
                    margin-top: 4px;
                }

                .bill-item-price {
                    font-weight: 700;
                    color: #52B788;
                }

                .bill-totals {
                    border-top: 2px solid #e0e0e0;
                    padding-top: 16px;
                    margin-bottom: 24px;
                }

                .bill-total-row {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    font-size: 14px;
                }

                .bill-total-row.grand-total {
                    font-size: 20px;
                    font-weight: 700;
                    color: #52B788;
                    border-top: 2px solid #52B788;
                    padding-top: 12px;
                    margin-top: 8px;
                }

                .bill-modal-actions {
                    display: flex;
                    gap: 12px;
                }

                .btn-download-bill {
                    flex: 1;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 14px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 15px;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-download-bill:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .btn-close-bill {
                    flex: 1;
                    background: white;
                    color: #666;
                    border: 2px solid #e0e0e0;
                    padding: 14px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 15px;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-close-bill:hover {
                    border-color: #52B788;
                    color: #52B788;
                }
            `}</style>

            <div className="bill-modal-overlay" onClick={onClose}>
                <div className="bill-modal" onClick={(e) => e.stopPropagation()}>
                    <div className="bill-header">
                        <h2>📄 Receipt</h2>
                        <div className="bill-number">{bill.billNumber}</div>
                    </div>

                    <div className="bill-info">
                        <div className="bill-info-item">
                            <div className="bill-info-label">Date & Time</div>
                            <div className="bill-info-value">{bill.billDate || bill.createdAt || 'N/A'}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Cashier</div>
                            <div className="bill-info-value">{bill.cashierName || 'N/A'}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Payment Method</div>
                            <div className="bill-info-value">{(bill.paymentMethod || 'Cash').toUpperCase()}</div>
                        </div>
                        <div className="bill-info-item">
                            <div className="bill-info-label">Status</div>
                            <div className="bill-info-value">{bill.status || 'Completed'}</div>
                        </div>
                    </div>

                    <div className="bill-items">
                        <h3>Items Purchased</h3>
                        {bill.items && bill.items.length > 0 ? (
                            bill.items.map((item, index) => (
                                <div key={index} className="bill-item">
                                    <div>
                                        <div className="bill-item-name">{item.productName || 'Product'}</div>
                                        <div className="bill-item-details">
                                            Qty: {item.quantity} × Rs. {Number(item.unitPrice || 0).toFixed(2)}
                                        </div>
                                    </div>
                                    <div className="bill-item-price">
                                        Rs. {Number(item.totalPrice || (item.quantity * item.unitPrice) || 0).toFixed(2)}
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p style={{ color: '#888', textAlign: 'center', padding: '20px' }}>No items found</p>
                        )}
                    </div>

                    <div className="bill-totals">
                        <div className="bill-total-row">
                            <span>Subtotal:</span>
                            <span>Rs. {Number(bill.subtotal || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row">
                            <span>Amount Paid:</span>
                            <span>Rs. {Number(bill.amountPaid || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row">
                            <span>Change:</span>
                            <span>Rs. {Number(bill.changeAmount || 0).toFixed(2)}</span>
                        </div>
                        <div className="bill-total-row grand-total">
                            <span>Total Amount:</span>
                            <span>Rs. {Number(bill.totalAmount || 0).toFixed(2)}</span>
                        </div>
                    </div>

                    <div className="bill-modal-actions">
                        <button className="btn-download-bill" onClick={onDownload}>
                            ⬇️ Download Receipt
                        </button>
                        <button className="btn-close-bill" onClick={onClose}>
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}

import { useState, useEffect } from "react";
import { apiGetInventory, apiReceiveStock, apiTransferStock, apiGetProducts } from "../../services/api";

export default function InventoryManagement() {
    const [inventory, setInventory] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [activeFilter, setActiveFilter] = useState("all");
    const [searchTerm, setSearchTerm] = useState("");
    const [showReceiveModal, setShowReceiveModal] = useState(false);
    const [showTransferModal, setShowTransferModal] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    useEffect(() => {
        loadInventory();
    }, [activeFilter]);

    useEffect(() => {
        setCurrentPage(1);
    }, [searchTerm, activeFilter]);

    const loadInventory = async () => {
        try {
            setLoading(true);
            setError("");
            let response;

            switch (activeFilter) {
                case "main":
                    response = await apiGetInventory({ location: "MAIN" });
                    break;
                case "shelf":
                    response = await apiGetInventory({ location: "SHELF" });
                    break;
                case "website":
                    response = await apiGetInventory({ location: "WEBSITE" });
                    break;
                case "expired":
                    response = await apiGetInventory({ view: "expired" });
                    break;
                case "near-expiry":
                    response = await apiGetInventory({ view: "near-expiry" });
                    break;
                default:
                    response = await apiGetInventory();
            }

            if (response.success) {
                setInventory(response.data);
            } else {
                setError(response.message || "Failed to load inventory");
            }
        } catch (err) {
            setError("Error loading inventory: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const filteredItems = inventory?.inventoryLocations?.filter(item =>
        item.productCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.productName?.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

    // Pagination calculations
    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedItems = filteredItems.slice(startIndex, endIndex);

    const formatDate = (dateString) => {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" });
    };

    const getStatusBadge = (item) => {
        if (item.isExpired) {
            return <span className="badge badge-expired">Expired</span>;
        }
        if (item.isNearExpiry) {
            return <span className="badge badge-near-expiry">Near Expiry</span>;
        }
        return <span className="badge badge-good">Good</span>;
    };

    const getLocationBadge = (location) => {
        const colors = {
            MAIN: "location-main",
            SHELF: "location-shelf",
            WEBSITE: "location-website"
        };
        return <span className={`badge ${colors[location]}`}>{location}</span>;
    };

    return (
        <>
            <style>{`
                .inventory-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .inventory-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .inventory-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                }

                .summary-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 16px;
                    margin-bottom: 24px;
                }

                .summary-card {
                    background: white;
                    padding: 24px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    border-left: 4px solid #52B788;
                    transition: all 0.3s ease;
                    cursor: pointer;
                }

                .summary-card:hover {
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.15);
                    transform: translateY(-2px);
                }

                .summary-card.warning {
                    border-left-color: #f59e0b;
                }

                .summary-card.danger {
                    border-left-color: #ef4444;
                }

                .summary-card-title {
                    font-size: 14px;
                    color: #666;
                    margin-bottom: 8px;
                }

                .summary-card-value {
                    font-size: 32px;
                    font-weight: 700;
                    color: #333;
                }

                .summary-card-subtitle {
                    font-size: 12px;
                    color: #999;
                    margin-top: 4px;
                }

                .action-buttons {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 24px;
                }

                .btn-primary, .btn-secondary {
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-primary {
                    background: #52B788;
                    color: white;
                }

                .btn-primary:hover {
                    background: #40916C;
                    transform: translateY(-1px);
                }

                .btn-secondary {
                    background: white;
                    color: #52B788;
                    border: 2px solid #52B788;
                }

                .btn-secondary:hover {
                    background: #f0fdf4;
                }

                .filter-tabs {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 24px;
                    flex-wrap: wrap;
                    background: white;
                    padding: 16px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }

                .filter-tab {
                    padding: 12px 24px;
                    border: 2px solid transparent;
                    background: #f5f5f5;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 600;
                    transition: all 0.2s;
                    color: #666;
                }

                .filter-tab:hover {
                    background: #e8f5e9;
                    color: #52B788;
                }

                .filter-tab.active {
                    background: #52B788;
                    color: white;
                    border-color: transparent;
                    box-shadow: 0 2px 8px rgba(82, 183, 136, 0.3);
                }

                .search-box {
                    margin-bottom: 20px;
                }

                .search-input {
                    width: 100%;
                    max-width: 400px;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 15px;
                    transition: all 0.2s;
                }

                .search-input:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .inventory-table-container {
                    background: white;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }

                .inventory-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .inventory-table thead {
                    background: linear-gradient(135deg, #f9f9f9 0%, #f5f5f5 100%);
                }

                .inventory-table th {
                    padding: 18px 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #555;
                    font-size: 13px;
                    border-bottom: 2px solid #e0e0e0;
                    white-space: nowrap;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }

                .inventory-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f5f5f5;
                    color: #666;
                    font-size: 14px;
                }

                .inventory-table tbody tr {
                    transition: all 0.2s;
                }

                .inventory-table tbody tr:hover {
                    background: #f8fef9;
                    box-shadow: inset 0 0 0 1px rgba(82, 183, 136, 0.1);
                }

                .badge {
                    padding: 6px 14px;
                    border-radius: 16px;
                    font-size: 12px;
                    font-weight: 600;
                    display: inline-block;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }

                .badge-expired {
                    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                    color: #dc2626;
                }

                .badge-near-expiry {
                    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                    color: #d97706;
                }

                .badge-good {
                    background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
                    color: #16a34a;
                }

                .location-main {
                    background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
                    color: #2563eb;
                }

                .location-shelf {
                    background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
                    color: #16a34a;
                }

                .location-website {
                    background: linear-gradient(135deg, #e9d5ff 0%, #d8b4fe 100%);
                    color: #9333ea;
                }

                .alert {
                    padding: 12px 16px;
                    border-radius: 8px;
                    margin-bottom: 16px;
                    font-size: 14px;
                }

                .alert-error {
                    background: #fee2e2;
                    color: #dc2626;
                    border-left: 4px solid #dc2626;
                }

                .alert-success {
                    background: #dcfce7;
                    color: #16a34a;
                    border-left: 4px solid #16a34a;
                }

                .loading {
                    text-align: center;
                    padding: 40px;
                    font-size: 18px;
                    color: #666;
                }

                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    color: #999;
                }

                .empty-icon {
                    font-size: 64px;
                    margin-bottom: 16px;
                }

                .empty-text {
                    font-size: 18px;
                    font-weight: 600;
                }

                .pagination-container {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 20px;
                    background: white;
                    border-top: 1px solid #e0e0e0;
                }

                .pagination-info {
                    color: #666;
                    font-size: 14px;
                }

                .pagination-controls {
                    display: flex;
                    gap: 8px;
                    align-items: center;
                }

                .pagination-btn {
                    padding: 8px 12px;
                    border: 1px solid #e0e0e0;
                    background: white;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 14px;
                    transition: all 0.2s;
                    min-width: 36px;
                    text-align: center;
                }

                .pagination-btn:hover:not(:disabled) {
                    background: #f5f5f5;
                    border-color: #52B788;
                }

                .pagination-btn:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .pagination-btn.active {
                    background: #52B788;
                    color: white;
                    border-color: #52B788;
                }

                .items-per-page {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 14px;
                    color: #666;
                }

                .items-per-page select {
                    padding: 6px 10px;
                    border: 1px solid #e0e0e0;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 14px;
                }

                .modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.5);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 1000;
                }

                .modal {
                    background: white;
                    border-radius: 12px;
                    padding: 32px;
                    max-width: 500px;
                    width: 90%;
                    max-height: 90vh;
                    overflow-y: auto;
                }

                .modal-header {
                    font-size: 24px;
                    font-weight: 700;
                    margin-bottom: 24px;
                    color: #333;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                .form-label {
                    display: block;
                    font-size: 14px;
                    font-weight: 600;
                    color: #555;
                    margin-bottom: 8px;
                }

                .form-input, .form-select {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 15px;
                    transition: all 0.2s;
                }

                .form-input:focus, .form-select:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .form-hint {
                    font-size: 12px;
                    color: #999;
                    margin-top: 4px;
                }

                .info-box {
                    background: #f0fdf4;
                    border: 2px solid #52B788;
                    border-radius: 8px;
                    padding: 12px 16px;
                    margin-bottom: 20px;
                    font-size: 14px;
                    color: #166534;
                }

                .modal-actions {
                    display: flex;
                    gap: 12px;
                    justify-content: flex-end;
                    margin-top: 24px;
                }

                .btn-cancel {
                    padding: 12px 24px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-cancel:hover {
                    background: #f5f5f5;
                }
            `}</style>

            <div className="inventory-management">
                <div className="inventory-header">
                    <h1 className="inventory-title">📦 Inventory Management</h1>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                {loading ? (
                    <div className="loading">Loading inventory...</div>
                ) : (
                    <>
                        <div className="summary-grid">
                            <div className="summary-card">
                                <div className="summary-card-title">Total Stock</div>
                                <div className="summary-card-value">{inventory?.totalStock || 0}</div>
                                <div className="summary-card-subtitle">units</div>
                            </div>
                            <div className="summary-card">
                                <div className="summary-card-title">MAIN Warehouse</div>
                                <div className="summary-card-value">{inventory?.totalMainStock || 0}</div>
                                <div className="summary-card-subtitle">units</div>
                            </div>
                            <div className="summary-card">
                                <div className="summary-card-title">SHELF Stock</div>
                                <div className="summary-card-value">{inventory?.totalShelfStock || 0}</div>
                                <div className="summary-card-subtitle">units</div>
                            </div>
                            <div className="summary-card">
                                <div className="summary-card-title">WEBSITE Stock</div>
                                <div className="summary-card-value">{inventory?.totalWebsiteStock || 0}</div>
                                <div className="summary-card-subtitle">units</div>
                            </div>
                            <div className={`summary-card ${inventory?.expiredBatchesCount > 0 ? 'danger' : ''}`}>
                                <div className="summary-card-title">⚠️ Expired Batches</div>
                                <div className="summary-card-value">{inventory?.expiredBatchesCount || 0}</div>
                                <div className="summary-card-subtitle">batches</div>
                            </div>
                            <div className={`summary-card ${inventory?.nearExpiryBatchesCount > 0 ? 'warning' : ''}`}>
                                <div className="summary-card-title">⚠️ Near Expiry</div>
                                <div className="summary-card-value">{inventory?.nearExpiryBatchesCount || 0}</div>
                                <div className="summary-card-subtitle">batches</div>
                            </div>
                        </div>

                        <div className="action-buttons">
                            <button className="btn-primary" onClick={() => setShowReceiveModal(true)}>
                                📦 Receive Stock
                            </button>
                            <button className="btn-secondary" onClick={() => setShowTransferModal(true)}>
                                🔄 Transfer Stock
                            </button>
                        </div>

                        <div className="filter-tabs">
                            <button
                                className={`filter-tab ${activeFilter === "all" ? "active" : ""}`}
                                onClick={() => setActiveFilter("all")}
                            >
                                All Inventory
                            </button>
                            <button
                                className={`filter-tab ${activeFilter === "main" ? "active" : ""}`}
                                onClick={() => setActiveFilter("main")}
                            >
                                MAIN Only
                            </button>
                            <button
                                className={`filter-tab ${activeFilter === "shelf" ? "active" : ""}`}
                                onClick={() => setActiveFilter("shelf")}
                            >
                                SHELF Only
                            </button>
                            <button
                                className={`filter-tab ${activeFilter === "website" ? "active" : ""}`}
                                onClick={() => setActiveFilter("website")}
                            >
                                WEBSITE Only
                            </button>
                            <button
                                className={`filter-tab ${activeFilter === "expired" ? "active" : ""}`}
                                onClick={() => setActiveFilter("expired")}
                            >
                                Expired
                            </button>
                            <button
                                className={`filter-tab ${activeFilter === "near-expiry" ? "active" : ""}`}
                                onClick={() => setActiveFilter("near-expiry")}
                            >
                                Near Expiry
                            </button>
                        </div>

                        <div className="search-box">
                            <input
                                type="text"
                                className="search-input"
                                placeholder="Search by product code or name..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>

                        {filteredItems.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">📦</div>
                                <div className="empty-text">
                                    {searchTerm ? "No inventory items found" : "No inventory available"}
                                </div>
                            </div>
                        ) : (
                            <>
                                <div className="inventory-table-container">
                                    <table className="inventory-table">
                                        <thead>
                                            <tr>
                                                <th>Product Code</th>
                                                <th>Product Name</th>
                                                <th>Batch ID</th>
                                                <th>Location</th>
                                                <th>Quantity</th>
                                                <th>Purchase Date</th>
                                                <th>Expiry Date</th>
                                                <th>Days Until Expiry</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {paginatedItems.map((item) => (
                                                <tr key={item.id}>
                                                    <td><strong>{item.productCode}</strong></td>
                                                    <td>{item.productName}</td>
                                                    <td>#{item.batchId}</td>
                                                    <td>{getLocationBadge(item.location)}</td>
                                                    <td><strong>{item.quantity}</strong></td>
                                                    <td>{formatDate(item.purchaseDate)}</td>
                                                    <td>{formatDate(item.expiryDate)}</td>
                                                    <td>{item.daysUntilExpiry !== null ? item.daysUntilExpiry : "-"}</td>
                                                    <td>{getStatusBadge(item)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>

                                    <div className="pagination-container">
                                        <div className="pagination-info">
                                            Showing {startIndex + 1} to {Math.min(endIndex, filteredItems.length)} of {filteredItems.length} items
                                        </div>
                                        
                                        <div className="pagination-controls">
                                            <div className="items-per-page">
                                                <span>Show</span>
                                                <select 
                                                    value={itemsPerPage} 
                                                    onChange={(e) => {
                                                        setItemsPerPage(Number(e.target.value));
                                                        setCurrentPage(1);
                                                    }}
                                                >
                                                    <option value="5">5</option>
                                                    <option value="10">10</option>
                                                    <option value="25">25</option>
                                                    <option value="50">50</option>
                                                </select>
                                            </div>
                                            
                                            <button
                                                className="pagination-btn"
                                                onClick={() => setCurrentPage(1)}
                                                disabled={currentPage === 1}
                                            >
                                                «
                                            </button>
                                            
                                            <button
                                                className="pagination-btn"
                                                onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                                disabled={currentPage === 1}
                                            >
                                                ‹
                                            </button>
                                            
                                            {[...Array(totalPages)].map((_, index) => {
                                                const pageNum = index + 1;
                                                if (
                                                    pageNum === 1 ||
                                                    pageNum === totalPages ||
                                                    (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)
                                                ) {
                                                    return (
                                                        <button
                                                            key={pageNum}
                                                            className={`pagination-btn ${currentPage === pageNum ? 'active' : ''}`}
                                                            onClick={() => setCurrentPage(pageNum)}
                                                        >
                                                            {pageNum}
                                                        </button>
                                                    );
                                                } else if (
                                                    pageNum === currentPage - 2 ||
                                                    pageNum === currentPage + 2
                                                ) {
                                                    return <span key={pageNum} style={{ padding: '0 4px' }}>...</span>;
                                                }
                                                return null;
                                            })}
                                            
                                            <button
                                                className="pagination-btn"
                                                onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                                                disabled={currentPage === totalPages}
                                            >
                                                ›
                                            </button>
                                            
                                            <button
                                                className="pagination-btn"
                                                onClick={() => setCurrentPage(totalPages)}
                                                disabled={currentPage === totalPages}
                                            >
                                                »
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </>
                        )}
                    </>
                )}

                {showReceiveModal && (
                    <ReceiveStockModal
                        onClose={() => setShowReceiveModal(false)}
                        onSuccess={() => {
                            setShowReceiveModal(false);
                            loadInventory();
                        }}
                        setSuccess={setSuccess}
                        setError={setError}
                    />
                )}

                {showTransferModal && (
                    <TransferStockModal
                        onClose={() => setShowTransferModal(false)}
                        onSuccess={() => {
                            setShowTransferModal(false);
                            loadInventory();
                        }}
                        setSuccess={setSuccess}
                        setError={setError}
                    />
                )}
            </div>
        </>
    );
}

function ReceiveStockModal({ onClose, onSuccess, setSuccess, setError }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        productCode: "",
        quantity: "",
        purchaseDate: new Date().toISOString().split("T")[0],
        expiryDate: ""
    });

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {
        const response = await apiGetProducts();
        if (response.success) {
            setProducts(response.data.products || []);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!formData.productCode || !formData.quantity || !formData.purchaseDate) {
            setError("Please fill in all required fields");
            return;
        }

        if (parseInt(formData.quantity) <= 0) {
            setError("Quantity must be greater than 0");
            return;
        }

        if (formData.expiryDate && formData.expiryDate <= formData.purchaseDate) {
            setError("Expiry date must be after purchase date");
            return;
        }

        try {
            setLoading(true);
            const submitData = {
                productCode: formData.productCode,
                quantity: parseInt(formData.quantity),
                purchaseDate: formData.purchaseDate,
                expiryDate: formData.expiryDate || null
            };

            const response = await apiReceiveStock(submitData);

            if (response.success) {
                const product = products.find(p => p.productCode === formData.productCode);
                setSuccess(`✅ Received ${formData.quantity} units of ${product?.productName || formData.productCode}`);
                setTimeout(() => setSuccess(""), 5000);
                onSuccess();
            } else {
                setError(response.message || "Failed to receive stock");
            }
        } catch (err) {
            setError("Error receiving stock: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">📦 Receive Stock</div>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Product *</label>
                        <select
                            className="form-select"
                            value={formData.productCode}
                            onChange={(e) => setFormData({ ...formData, productCode: e.target.value })}
                            required
                        >
                            <option value="">Select a product</option>
                            {products.map((product) => (
                                <option key={product.productCode} value={product.productCode}>
                                    {product.productCode} - {product.productName}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Quantity *</label>
                        <input
                            type="number"
                            className="form-input"
                            placeholder="e.g., 100"
                            min="1"
                            value={formData.quantity}
                            onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Purchase Date *</label>
                        <input
                            type="date"
                            className="form-input"
                            max={new Date().toISOString().split("T")[0]}
                            value={formData.purchaseDate}
                            onChange={(e) => setFormData({ ...formData, purchaseDate: e.target.value })}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Expiry Date (Optional)</label>
                        <input
                            type="date"
                            className="form-input"
                            min={formData.purchaseDate}
                            value={formData.expiryDate}
                            onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
                        />
                        <div className="form-hint">Leave empty if no expiry date</div>
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn-cancel" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary" disabled={loading}>
                            {loading ? "Receiving..." : "Receive Stock"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function TransferStockModal({ onClose, onSuccess, setSuccess, setError }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [availableQty, setAvailableQty] = useState(null);
    const [formData, setFormData] = useState({
        productCode: "",
        quantity: "",
        fromLocation: "",
        toLocation: ""
    });

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        if (formData.productCode && formData.fromLocation) {
            checkAvailableQuantity();
        } else {
            setAvailableQty(null);
        }
    }, [formData.productCode, formData.fromLocation]);

    const loadProducts = async () => {
        const response = await apiGetProducts();
        if (response.success) {
            setProducts(response.data.products || []);
        }
    };

    const checkAvailableQuantity = async () => {
        const response = await apiGetInventory({
            product: formData.productCode,
            location: formData.fromLocation
        });

        console.log('🔍 Checking available quantity for:', formData.productCode, 'at', formData.fromLocation);
        console.log('📊 Inventory response:', response);

        if (response.success) {
            // Filter inventory items to only include the selected product
            const productItems = response.data.inventoryLocations?.filter(
                item => item.productCode === formData.productCode && item.location === formData.fromLocation
            ) || [];
            
            const total = productItems.reduce((sum, item) => sum + item.quantity, 0);
            
            console.log('🔎 Filtered items for', formData.productCode, ':', productItems);
            console.log('✅ Total available quantity for', formData.productCode, ':', total);
            setAvailableQty(total);
        } else {
            console.error('❌ Failed to get inventory:', response.message);
            setAvailableQty(0);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!formData.productCode || !formData.quantity || !formData.fromLocation || !formData.toLocation) {
            setError("Please fill in all required fields");
            return;
        }

        if (formData.fromLocation === formData.toLocation) {
            setError("From and To locations must be different");
            return;
        }

        if (parseInt(formData.quantity) <= 0) {
            setError("Quantity must be greater than 0");
            return;
        }

        // Refresh available quantity right before submitting to get latest data
        console.log('🔄 Refreshing inventory before transfer...');
        await checkAvailableQuantity();
        
        // Wait a moment for state to update
        await new Promise(resolve => setTimeout(resolve, 100));

        try {
            setLoading(true);
            const submitData = {
                productCode: formData.productCode,
                quantity: parseInt(formData.quantity),
                fromLocation: formData.fromLocation,
                toLocation: formData.toLocation
            };

            console.log('📦 Transfer: Submitting transfer request:', submitData);
            console.log('📊 Transfer: Frontend shows available quantity:', availableQty);
            
            const response = await apiTransferStock(submitData);
            
            console.log('📥 Transfer: Response from backend:', response);

            if (response.success) {
                const batchCount = response.data.batchesUsed?.length || 0;
                setSuccess(
                    `✅ Transferred ${formData.quantity} units from ${formData.fromLocation} to ${formData.toLocation}` +
                    (batchCount > 0 ? ` using ${batchCount} batch(es) (FEFO)` : "")
                );
                setTimeout(() => setSuccess(""), 5000);
                onSuccess();
            } else {
                console.error('❌ Transfer failed:', response.message);
                // Refresh to show actual available quantity after error
                await checkAvailableQuantity();
                setError(response.message + " (Quantity refreshed - please check available stock)");
            }
        } catch (err) {
            console.error('💥 Transfer exception:', err);
            setError("Error transferring stock: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const locations = ["MAIN", "SHELF", "WEBSITE"];

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">🔄 Transfer Stock</div>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Product *</label>
                        <select
                            className="form-select"
                            value={formData.productCode}
                            onChange={(e) => setFormData({ ...formData, productCode: e.target.value })}
                            required
                        >
                            <option value="">Select a product</option>
                            {products.map((product) => (
                                <option key={product.productCode} value={product.productCode}>
                                    {product.productCode} - {product.productName}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label">From Location *</label>
                        <select
                            className="form-select"
                            value={formData.fromLocation}
                            onChange={(e) => setFormData({ ...formData, fromLocation: e.target.value })}
                            required
                        >
                            <option value="">Select location</option>
                            {locations.map((loc) => (
                                <option key={loc} value={loc}>
                                    {loc}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label">To Location *</label>
                        <select
                            className="form-select"
                            value={formData.toLocation}
                            onChange={(e) => setFormData({ ...formData, toLocation: e.target.value })}
                            required
                        >
                            <option value="">Select location</option>
                            {locations.map((loc) => (
                                <option
                                    key={loc}
                                    value={loc}
                                    disabled={loc === formData.fromLocation}
                                >
                                    {loc}
                                </option>
                            ))}
                        </select>
                    </div>

                    {availableQty !== null && (
                        <div className="info-box">
                            📦 Available in {formData.fromLocation}: <strong>{availableQty} units</strong>
                            <button 
                                type="button" 
                                onClick={checkAvailableQuantity}
                                style={{
                                    marginLeft: '12px',
                                    padding: '4px 12px',
                                    fontSize: '12px',
                                    background: '#52B788',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '4px',
                                    cursor: 'pointer'
                                }}
                            >
                                🔄 Refresh
                            </button>
                        </div>
                    )}

                    <div className="form-group">
                        <label className="form-label">Quantity *</label>
                        <input
                            type="number"
                            className="form-input"
                            placeholder="e.g., 50"
                            min="1"
                            max={availableQty || undefined}
                            value={formData.quantity}
                            onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                            required
                        />
                        {availableQty !== null && (
                            <div className="form-hint">Maximum: {availableQty} units</div>
                        )}
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn-cancel" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary" disabled={loading}>
                            {loading ? "Transferring..." : "Transfer Stock"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

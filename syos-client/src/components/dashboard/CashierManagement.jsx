import { useState, useEffect } from "react";
import { apiGetCashiers, apiCreateCashier, apiUpdateCashier, apiDeactivateCashier } from "../../services/api";

export default function CashierManagement() {
    const [cashiers, setCashiers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeactivateModal, setShowDeactivateModal] = useState(false);
    const [selectedCashier, setSelectedCashier] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);

    useEffect(() => {
        loadCashiers();
    }, []);

    useEffect(() => {
        setCurrentPage(1);
    }, [searchTerm]);

    const loadCashiers = async () => {
        try {
            setLoading(true);
            setError("");
            const response = await apiGetCashiers();
            if (response.success) {
                setCashiers(response.data || []);
            } else {
                setError(response.message || "Failed to load cashiers");
            }
        } catch (err) {
            setError("Error loading cashiers: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setShowCreateModal(true);
    };

    const handleEdit = (cashier) => {
        setSelectedCashier(cashier);
        setShowEditModal(true);
    };

    const handleDeactivate = (cashier) => {
        setSelectedCashier(cashier);
        setShowDeactivateModal(true);
    };

    const filteredCashiers = cashiers.filter(cashier =>
        cashier.userId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        cashier.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        cashier.email?.toLowerCase().includes(searchTerm.toLowerCase())
    ).sort((a, b) => a.fullName?.localeCompare(b.fullName));

    // Pagination
    const totalPages = Math.ceil(filteredCashiers.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedCashiers = filteredCashiers.slice(startIndex, endIndex);

    return (
        <>
            <style>{`
                .cashier-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .cashier-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .cashier-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #333;
                }

                .btn-add {
                    padding: 12px 24px;
                    background: #52B788;
                    color: white;
                    border: none;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-add:hover {
                    background: #40916C;
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
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

                .cashier-table-container {
                    background: white;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }

                .cashier-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .cashier-table thead {
                    background: linear-gradient(135deg, #f9f9f9 0%, #f5f5f5 100%);
                }

                .cashier-table th {
                    padding: 18px 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #555;
                    font-size: 13px;
                    border-bottom: 2px solid #e0e0e0;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }

                .cashier-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f5f5f5;
                    color: #666;
                    font-size: 14px;
                }

                .cashier-table tbody tr {
                    transition: all 0.2s;
                }

                .cashier-table tbody tr:hover {
                    background: #f8fef9;
                }

                .badge {
                    padding: 6px 14px;
                    border-radius: 16px;
                    font-size: 12px;
                    font-weight: 600;
                    display: inline-block;
                }

                .badge-active {
                    background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
                    color: #16a34a;
                }

                .badge-inactive {
                    background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
                    color: #6b7280;
                }

                .action-buttons {
                    display: flex;
                    gap: 8px;
                }

                .btn-edit, .btn-deactivate {
                    padding: 8px 16px;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 13px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-edit {
                    background: #e8f5e9;
                    color: #2e7d32;
                }

                .btn-edit:hover {
                    background: #c8e6c9;
                }

                .btn-deactivate {
                    background: #ffebee;
                    color: #c62828;
                }

                .btn-deactivate:hover {
                    background: #ffcdd2;
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
                    margin-bottom: 8px;
                }

                .empty-subtext {
                    font-size: 14px;
                    color: #bbb;
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

                .form-input {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 15px;
                    transition: all 0.2s;
                }

                .form-input:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .form-input:disabled {
                    background: #f5f5f5;
                    cursor: not-allowed;
                }

                .form-hint {
                    font-size: 12px;
                    color: #999;
                    margin-top: 4px;
                }

                .form-error {
                    font-size: 12px;
                    color: #dc2626;
                    margin-top: 4px;
                }

                .password-input-wrapper {
                    position: relative;
                }

                .password-toggle {
                    position: absolute;
                    right: 12px;
                    top: 50%;
                    transform: translateY(-50%);
                    background: none;
                    border: none;
                    cursor: pointer;
                    font-size: 18px;
                    color: #666;
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

                .btn-submit {
                    padding: 12px 24px;
                    background: #52B788;
                    color: white;
                    border: none;
                    border-radius: 8px;
                    cursor: pointer;
                    font-size: 15px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .btn-submit:hover:not(:disabled) {
                    background: #40916C;
                }

                .btn-submit:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .btn-danger {
                    background: #dc2626;
                }

                .btn-danger:hover:not(:disabled) {
                    background: #b91c1c;
                }

                .confirm-message {
                    font-size: 15px;
                    color: #666;
                    line-height: 1.6;
                    margin-bottom: 20px;
                }

                .confirm-warning {
                    background: #fef3c7;
                    border-left: 4px solid #f59e0b;
                    padding: 12px;
                    margin-top: 16px;
                    border-radius: 4px;
                    font-size: 14px;
                    color: #92400e;
                }
            `}</style>

            <div className="cashier-management">
                <div className="cashier-header">
                    <h1 className="cashier-title">👤 Cashier Management</h1>
                    <button className="btn-add" onClick={handleCreate}>
                        ➕ Add New Cashier
                    </button>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                {loading ? (
                    <div className="loading">Loading cashiers...</div>
                ) : (
                    <>
                        <div className="search-box">
                            <input
                                type="text"
                                className="search-input"
                                placeholder="Search by user ID, name, or email..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>

                        {filteredCashiers.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">👥</div>
                                <div className="empty-text">
                                    {searchTerm ? "No cashiers found" : "No cashiers yet"}
                                </div>
                                <div className="empty-subtext">
                                    {!searchTerm && "Click 'Add New Cashier' to get started"}
                                </div>
                            </div>
                        ) : (
                            <>
                                <div className="cashier-table-container">
                                    <table className="cashier-table">
                                        <thead>
                                            <tr>
                                                <th>User ID</th>
                                                <th>Full Name</th>
                                                <th>Email</th>
                                                <th>Contact Number</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {paginatedCashiers.map((cashier) => (
                                                <tr key={cashier.userId}>
                                                    <td><strong>{cashier.userId}</strong></td>
                                                    <td>{cashier.fullName}</td>
                                                    <td>{cashier.email}</td>
                                                    <td>{cashier.contactNumber || "-"}</td>
                                                    <td>
                                                        <span className={cashier.isActive ? "badge badge-active" : "badge badge-inactive"}>
                                                            {cashier.isActive ? "Active" : "Inactive"}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div className="action-buttons">
                                                            <button className="btn-edit" onClick={() => handleEdit(cashier)}>
                                                                ✏️ Edit
                                                            </button>
                                                            {cashier.isActive && (
                                                                <button className="btn-deactivate" onClick={() => handleDeactivate(cashier)}>
                                                                    🗑️ Deactivate
                                                                </button>
                                                            )}
                                                        </div>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>

                                    <div className="pagination-container">
                                        <div className="pagination-info">
                                            Showing {startIndex + 1} to {Math.min(endIndex, filteredCashiers.length)} of {filteredCashiers.length} cashiers
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

                {showCreateModal && (
                    <CreateCashierModal
                        onClose={() => setShowCreateModal(false)}
                        onSuccess={() => {
                            setShowCreateModal(false);
                            loadCashiers();
                        }}
                        setSuccess={setSuccess}
                        setError={setError}
                    />
                )}

                {showEditModal && selectedCashier && (
                    <EditCashierModal
                        cashier={selectedCashier}
                        onClose={() => {
                            setShowEditModal(false);
                            setSelectedCashier(null);
                        }}
                        onSuccess={() => {
                            setShowEditModal(false);
                            setSelectedCashier(null);
                            loadCashiers();
                        }}
                        setSuccess={setSuccess}
                        setError={setError}
                    />
                )}

                {showDeactivateModal && selectedCashier && (
                    <DeactivateModal
                        cashier={selectedCashier}
                        onClose={() => {
                            setShowDeactivateModal(false);
                            setSelectedCashier(null);
                        }}
                        onSuccess={() => {
                            setShowDeactivateModal(false);
                            setSelectedCashier(null);
                            loadCashiers();
                        }}
                        setSuccess={setSuccess}
                        setError={setError}
                    />
                )}
            </div>
        </>
    );
}

function CreateCashierModal({ onClose, onSuccess, setSuccess, setError }) {
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [formData, setFormData] = useState({
        userId: "",
        fullName: "",
        email: "",
        contactNumber: "",
        password: ""
    });
    const [errors, setErrors] = useState({});

    const validate = () => {
        const newErrors = {};
        
        if (!formData.userId || formData.userId.length < 3) {
            newErrors.userId = "User ID must be at least 3 characters";
        }
        if (!formData.fullName) {
            newErrors.fullName = "Full name is required";
        }
        if (!formData.email || !formData.email.includes("@")) {
            newErrors.email = "Valid email is required";
        }
        if (!formData.password || formData.password.length < 6) {
            newErrors.password = "Password must be at least 6 characters";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!validate()) return;

        try {
            setLoading(true);
            const response = await apiCreateCashier(formData);

            if (response.success) {
                setSuccess(`✅ Cashier '${formData.fullName}' created successfully`);
                setTimeout(() => setSuccess(""), 5000);
                onSuccess();
            } else {
                setError(response.message || "Failed to create cashier");
            }
        } catch (err) {
            setError("Error creating cashier: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">➕ Add New Cashier</div>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">User ID *</label>
                        <input
                            type="text"
                            className="form-input"
                            placeholder="e.g., cashier001"
                            value={formData.userId}
                            onChange={(e) => setFormData({ ...formData, userId: e.target.value })}
                        />
                        {errors.userId && <div className="form-error">{errors.userId}</div>}
                        <div className="form-hint">This will be the username for login</div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Full Name *</label>
                        <input
                            type="text"
                            className="form-input"
                            placeholder="e.g., John Doe"
                            value={formData.fullName}
                            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                        />
                        {errors.fullName && <div className="form-error">{errors.fullName}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Email *</label>
                        <input
                            type="email"
                            className="form-input"
                            placeholder="e.g., john@example.com"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        />
                        {errors.email && <div className="form-error">{errors.email}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Contact Number</label>
                        <input
                            type="text"
                            className="form-input"
                            placeholder="e.g., 0771234567"
                            value={formData.contactNumber}
                            onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Password *</label>
                        <div className="password-input-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                className="form-input"
                                placeholder="Minimum 6 characters"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                style={{ paddingRight: "45px" }}
                            />
                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? "👁️" : "👁️‍🗨️"}
                            </button>
                        </div>
                        {errors.password && <div className="form-error">{errors.password}</div>}
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn-cancel" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-submit" disabled={loading}>
                            {loading ? "Creating..." : "Create Cashier"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function EditCashierModal({ cashier, onClose, onSuccess, setSuccess, setError }) {
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [formData, setFormData] = useState({
        userId: cashier.userId,
        fullName: cashier.fullName,
        email: cashier.email,
        contactNumber: cashier.contactNumber || "",
        password: ""
    });
    const [errors, setErrors] = useState({});

    const validate = () => {
        const newErrors = {};
        
        if (!formData.fullName) {
            newErrors.fullName = "Full name is required";
        }
        if (!formData.email || !formData.email.includes("@")) {
            newErrors.email = "Valid email is required";
        }
        if (formData.password && formData.password.length < 6) {
            newErrors.password = "Password must be at least 6 characters";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!validate()) return;

        try {
            setLoading(true);
            const submitData = { ...formData };
            if (!submitData.password) {
                delete submitData.password;
            }

            const response = await apiUpdateCashier(submitData);

            if (response.success) {
                setSuccess("✅ Cashier updated successfully");
                setTimeout(() => setSuccess(""), 5000);
                onSuccess();
            } else {
                setError(response.message || "Failed to update cashier");
            }
        } catch (err) {
            setError("Error updating cashier: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">✏️ Edit Cashier</div>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">User ID</label>
                        <input
                            type="text"
                            className="form-input"
                            value={formData.userId}
                            disabled
                        />
                        <div className="form-hint">User ID cannot be changed</div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Full Name *</label>
                        <input
                            type="text"
                            className="form-input"
                            value={formData.fullName}
                            onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                        />
                        {errors.fullName && <div className="form-error">{errors.fullName}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Email *</label>
                        <input
                            type="email"
                            className="form-input"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        />
                        {errors.email && <div className="form-error">{errors.email}</div>}
                    </div>

                    <div className="form-group">
                        <label className="form-label">Contact Number</label>
                        <input
                            type="text"
                            className="form-input"
                            value={formData.contactNumber}
                            onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">New Password (Optional)</label>
                        <div className="password-input-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                className="form-input"
                                placeholder="Leave empty to keep current password"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                style={{ paddingRight: "45px" }}
                            />
                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? "👁️" : "👁️‍🗨️"}
                            </button>
                        </div>
                        {errors.password && <div className="form-error">{errors.password}</div>}
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn-cancel" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-submit" disabled={loading}>
                            {loading ? "Updating..." : "Update Cashier"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function DeactivateModal({ cashier, onClose, onSuccess, setSuccess, setError }) {
    const [loading, setLoading] = useState(false);

    const handleConfirm = async () => {
        try {
            setLoading(true);
            const response = await apiDeactivateCashier(cashier.userId);

            if (response.success) {
                setSuccess(`✅ Cashier '${cashier.fullName}' deactivated successfully`);
                setTimeout(() => setSuccess(""), 5000);
                onSuccess();
            } else {
                setError(response.message || "Failed to deactivate cashier");
            }
        } catch (err) {
            setError("Error deactivating cashier: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">🗑️ Deactivate Cashier?</div>

                <div className="confirm-message">
                    Are you sure you want to deactivate <strong>{cashier.fullName}</strong> (User ID: <strong>{cashier.userId}</strong>)?
                </div>

                <div className="confirm-warning">
                    ⚠️ This cashier will no longer be able to log in to the system.
                    You can reactivate them later if needed.
                </div>

                <div className="modal-actions">
                    <button type="button" className="btn-cancel" onClick={onClose}>
                        Cancel
                    </button>
                    <button 
                        type="button" 
                        className="btn-submit btn-danger" 
                        onClick={handleConfirm}
                        disabled={loading}
                    >
                        {loading ? "Deactivating..." : "Deactivate"}
                    </button>
                </div>
            </div>
        </div>
    );
}

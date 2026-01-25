import { useState, useEffect } from "react";
import { apiGetCategories, apiCreateCategory, apiUpdateCategory, apiDeleteCategory } from "../../services/api";

export default function CategoryManagement() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState("create");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(10);
    const [formData, setFormData] = useState({
        categoryId: "",
        categoryName: "",
        description: ""
    });

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            setLoading(true);
            const response = await apiGetCategories();
            if (response.success) {
                setCategories(response.data || []);
            } else {
                setError(response.message || "Failed to load categories");
            }
        } catch (err) {
            setError("Error loading categories: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setModalMode("create");
        setFormData({
            categoryId: "",
            categoryName: "",
            description: ""
        });
        setShowModal(true);
    };

    const handleEdit = (category) => {
        setModalMode("edit");
        setFormData({
            categoryId: category.categoryId,
            categoryName: category.categoryName || "",
            description: category.description || ""
        });
        setShowModal(true);
    };

    const handleDelete = async (categoryId, categoryName) => {
        if (!window.confirm(`Are you sure you want to delete '${categoryName}'? This action cannot be undone.`)) {
            return;
        }

        try {
            const response = await apiDeleteCategory(categoryId);
            if (response.success) {
                setSuccess("Category deleted successfully!");
                loadCategories();
                setTimeout(() => setSuccess(""), 3000);
            } else {
                setError(response.message || "Failed to delete category");
                setTimeout(() => setError(""), 5000);
            }
        } catch (err) {
            setError("Error deleting category: " + err.message);
            setTimeout(() => setError(""), 3000);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        if (!formData.categoryName.trim()) {
            setError("Category name is required");
            return;
        }

        try {
            let response;
            if (modalMode === "create") {
                response = await apiCreateCategory({
                    categoryName: formData.categoryName,
                    description: formData.description
                });
            } else {
                response = await apiUpdateCategory(formData);
            }

            if (response.success) {
                setSuccess(`Category ${modalMode === "create" ? "created" : "updated"} successfully!`);
                setShowModal(false);
                loadCategories();
                setTimeout(() => setSuccess(""), 3000);
            } else {
                setError(response.message || `Failed to ${modalMode} category`);
            }
        } catch (err) {
            setError(`Error ${modalMode === "create" ? "creating" : "updating"} category: ${err.message}`);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const filteredCategories = Array.isArray(categories)
        ? categories
            .filter(category =>
                category.categoryName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                category.description?.toLowerCase().includes(searchTerm.toLowerCase())
            )
            .sort((a, b) => a.categoryId - b.categoryId)
        : [];

    // Pagination calculations
    const totalPages = Math.ceil(filteredCategories.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedCategories = filteredCategories.slice(startIndex, endIndex);

    // Reset to page 1 when search term changes
    useEffect(() => {
        setCurrentPage(1);
    }, [searchTerm]);

    return (
        <>
            <style>{`
                .category-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .cm-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .cm-title {
                    font-size: 32px;
                    font-weight: 400;
                    color: #333;
                }

                .btn-create {
                    background: #52B788;
                    color: white;
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    font-size: 15px;
                    font-weight: 600;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.2s;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    box-shadow: 0 2px 8px rgba(82, 183, 136, 0.2);
                }

                .btn-create:hover {
                    background: #40916C;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                    transform: translateY(-1px);
                }

                .search-box {
                    margin-bottom: 20px;
                    display: flex;
                    justify-content: flex-end;
                }

                .search-input {
                    padding: 10px 16px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 15px;
                    width: 300px;
                    background: white;
                }

                .alert {
                    padding: 12px 16px;
                    border-radius: 8px;
                    margin-bottom: 16px;
                    font-size: 14px;
                    font-weight: 500;
                }

                .alert-error {
                    background: #ffebee;
                    color: #c62828;
                    border-left: 4px solid #c62828;
                }

                .alert-success {
                    background: #e8f5e9;
                    color: #2e7d32;
                    border-left: 4px solid #2e7d32;
                }

                .categories-table-container {
                    background: white;
                    border-radius: 4px;
                    overflow: hidden;
                }

                .categories-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .categories-table thead {
                    background: #f9f9f9;
                }

                .categories-table th {
                    padding: 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #555;
                    font-size: 15px;
                    border-bottom: 1px solid #e0e0e0;
                }

                .categories-table th:nth-child(1) {
                    width: 80px;
                    padding-right: 50px;
                }

                .categories-table th:nth-child(2) {
                    width: 35%;
                }

                .categories-table th:nth-child(3) {
                    width: auto;
                }

                .categories-table th:nth-child(4) {
                    width: 200px;
                    text-align: center;
                    padding-right: 24px;
                }

                .categories-table td:nth-child(1) {
                    padding-right: 50px;
                }

                .categories-table td:nth-child(4) {
                    text-align: right;
                    padding-right: 24px;
                }

                .categories-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f0f0f0;
                    color: #666;
                    font-size: 15px;
                }

                .categories-table tbody tr:hover {
                    background: #fafafa;
                }

                .action-buttons {
                    display: flex;
                    gap: 8px;
                    justify-content: flex-end;
                }

                .btn-edit, .btn-delete {
                    padding: 8px 16px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
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

                .btn-delete {
                    background: #ffebee;
                    color: #c62828;
                }

                .btn-delete:hover {
                    background: #ffcdd2;
                }

                .modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.5);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    z-index: 1000;
                    animation: fadeIn 0.2s;
                }

                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }

                .modal {
                    background: white;
                    border-radius: 16px;
                    width: 90%;
                    max-width: 500px;
                    animation: slideUp 0.3s;
                    box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                }

                @keyframes slideUp {
                    from {
                        opacity: 0;
                        transform: translateY(30px);
                    }
                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }

                .modal-header {
                    padding: 24px;
                    border-bottom: 2px solid #f0f0f0;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }

                .modal-title {
                    font-size: 20px;
                    font-weight: 700;
                    color: #333;
                }

                .modal-close {
                    background: none;
                    border: none;
                    font-size: 28px;
                    cursor: pointer;
                    color: #999;
                    width: 32px;
                    height: 32px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 50%;
                    transition: all 0.2s;
                }

                .modal-close:hover {
                    background: #f5f5f5;
                    color: #333;
                }

                .modal-body {
                    padding: 24px;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                .form-label {
                    display: block;
                    margin-bottom: 8px;
                    font-weight: 600;
                    color: #555;
                    font-size: 14px;
                }

                .form-input,
                .form-textarea {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                    transition: all 0.3s;
                    font-family: inherit;
                }

                .form-input:focus,
                .form-textarea:focus {
                    outline: none;
                    border-color: #52B788;
                    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
                }

                .form-textarea {
                    resize: vertical;
                    min-height: 100px;
                }

                .modal-footer {
                    padding: 20px 24px;
                    border-top: 2px solid #f0f0f0;
                    display: flex;
                    justify-content: flex-end;
                    gap: 12px;
                }

                .btn-secondary {
                    background: #f5f5f5;
                    color: #666;
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;
                }

                .btn-secondary:hover {
                    background: #e0e0e0;
                }

                .btn-primary {
                    background: #52B788;
                    color: white;
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;
                    box-shadow: 0 2px 8px rgba(82, 183, 136, 0.2);
                }

                .btn-primary:hover {
                    background: #40916C;
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .loading {
                    text-align: center;
                    padding: 40px;
                    color: #999;
                    font-size: 16px;
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
            `}</style>

            <div className="category-management">
                <div className="cm-header">
                    <h1 className="cm-title">Category Management</h1>
                    <button className="btn-create" onClick={handleCreate}>
                        ➕ CREATE NEW CATEGORY
                    </button>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                {loading ? (
                    <div className="loading">Loading categories...</div>
                ) : (
                    <>
                        <div className="search-box">
                            <input
                                type="text"
                                className="search-input"
                                placeholder="Search categories..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>

                        {filteredCategories.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">📁</div>
                                <div className="empty-text">
                                    {searchTerm ? "No categories found" : "No categories yet"}
                                </div>
                            </div>
                        ) : (
                            <>
                                <div className="categories-table-container">
                                    <table className="categories-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Category Name</th>
                                                <th>Description</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {paginatedCategories.map((category) => (
                                                <tr key={category.categoryId}>
                                                    <td>{category.categoryId}</td>
                                                    <td><strong>{category.categoryName}</strong></td>
                                                    <td>{category.description || '-'}</td>
                                                    <td>
                                                        <div className="action-buttons">
                                                            <button
                                                                className="btn-edit"
                                                                onClick={() => handleEdit(category)}
                                                            >
                                                                ✏️ Edit
                                                            </button>
                                                            <button
                                                                className="btn-delete"
                                                                onClick={() => handleDelete(category.categoryId, category.categoryName)}
                                                            >
                                                                🗑️ Delete
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                    
                                    <div className="pagination-container">
                                        <div className="pagination-info">
                                            Showing {startIndex + 1} to {Math.min(endIndex, filteredCategories.length)} of {filteredCategories.length} categories
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

                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-header">
                                <h2 className="modal-title">
                                    {modalMode === "create" ? "Create New Category" : "Edit Category"}
                                </h2>
                                <button
                                    className="modal-close"
                                    onClick={() => setShowModal(false)}
                                >
                                    ×
                                </button>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="modal-body">
                                    {error && <div className="alert alert-error">{error}</div>}

                                    <div className="form-group">
                                        <label className="form-label">Category Name *</label>
                                        <input
                                            type="text"
                                            className="form-input"
                                            name="categoryName"
                                            value={formData.categoryName}
                                            onChange={handleInputChange}
                                            required
                                            maxLength={100}
                                            placeholder="e.g., Frozen Foods"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Description</label>
                                        <textarea
                                            className="form-textarea"
                                            name="description"
                                            value={formData.description}
                                            onChange={handleInputChange}
                                            maxLength={500}
                                            placeholder="Brief description of the category"
                                        />
                                    </div>
                                </div>

                                <div className="modal-footer">
                                    <button
                                        type="button"
                                        className="btn-secondary"
                                        onClick={() => setShowModal(false)}
                                    >
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn-primary">
                                        {modalMode === "create" ? "Create Category" : "Update Category"}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}

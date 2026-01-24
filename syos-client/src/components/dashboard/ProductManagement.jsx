import { useState, useEffect } from "react";
import {
    apiGetProducts,
    apiCreateProduct,
    apiUpdateProduct,
    apiDeleteProduct,
    apiGetCategories
} from "../../services/api";

export default function ProductManagement() {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState("create"); // create, edit, view
    const [entriesPerPage, setEntriesPerPage] = useState(10);
    const [activeDropdown, setActiveDropdown] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState("");
    const [formData, setFormData] = useState({
        productCode: "",
        name: "",
        unitPrice: "",
        imageUrl: "",
        categoryId: ""
    });

    useEffect(() => {
        loadProducts();
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const response = await apiGetCategories();
            if (response.success) {
                setCategories(response.data || []);
            }
        } catch (err) {
            console.error("Error loading categories:", err);
        }
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (activeDropdown && !event.target.closest('.action-dropdown')) {
                setActiveDropdown(null);
            }
        };
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, [activeDropdown]);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await apiGetProducts();
            if (response.success) {
                setProducts(response.data.products || []);
            } else {
                setError(response.message || "Failed to load products");
            }
        } catch (err) {
            setError("Error loading products: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setModalMode("create");
        setFormData({
            productCode: "",
            name: "",
            unitPrice: "",
            imageUrl: "",
            categoryId: ""
        });
        setImageFile(null);
        setImagePreview("");
        setShowModal(true);
    };

    const handleEdit = async (product) => {
        setModalMode("edit");
        setFormData({
            productCode: product.productCode || "",
            name: product.name || "",
            unitPrice: product.unitPrice || "",
            imageUrl: product.imageUrl || "",
            categoryId: product.categoryId || ""
        });
        setImagePreview(product.imageUrl || "");
        setImageFile(null);
        setShowModal(true);
        setActiveDropdown(null);
    };

    const handleView = async (product) => {
        setModalMode("view");
        setFormData({
            productCode: product.productCode || "",
            name: product.name || "",
            unitPrice: product.unitPrice || "",
            imageUrl: product.imageUrl || "",
            status: product.status || "",
            totalQuantity: product.totalQuantity || 0,
            shelfQuantity: product.shelfQuantity || 0,
            warehouseQuantity: product.warehouseQuantity || 0,
            websiteQuantity: product.websiteQuantity || 0,
            needsReordering: product.needsReordering || false
        });
        setImagePreview(product.imageUrl || "");
        setShowModal(true);
        setActiveDropdown(null);
    };

    const handleDelete = async (productCode) => {
        if (!window.confirm("Are you sure you want to delete this product?")) {
            return;
        }

        try {
            const response = await apiDeleteProduct(productCode);
            if (response.success) {
                setSuccess("Product deleted successfully!");
                loadProducts();
                setTimeout(() => setSuccess(""), 3000);
            } else {
                setError(response.message || "Failed to delete product");
                setTimeout(() => setError(""), 3000);
            }
        } catch (err) {
            setError("Error deleting product: " + err.message);
            setTimeout(() => setError(""), 3000);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        try {
            let response;
            if (modalMode === "create") {
                response = await apiCreateProduct(formData);
            } else if (modalMode === "edit") {
                response = await apiUpdateProduct(formData);
            }

            if (response.success) {
                setSuccess(`Product ${modalMode === "create" ? "created" : "updated"} successfully!`);
                setShowModal(false);
                loadProducts();
                setTimeout(() => setSuccess(""), 3000);
            } else {
                setError(response.message || `Failed to ${modalMode} product`);
            }
        } catch (err) {
            setError(`Error ${modalMode === "create" ? "creating" : "updating"} product: ${err.message}`);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImageFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result);
                setFormData(prev => ({
                    ...prev,
                    imageUrl: reader.result
                }));
            };
            reader.readAsDataURL(file);
        }
    };

    const filteredProducts = Array.isArray(products) 
        ? products.filter(product =>
            product.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            product.productCode?.toLowerCase().includes(searchTerm.toLowerCase())
        ).slice(0, entriesPerPage)
        : [];

    return (
        <>
            <style>{`
                .product-management {
                    width: 100%;
                    background: #f5f5f5;
                    min-height: 100vh;
                    padding: 24px;
                }

                .pm-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }

                .pm-title {
                    font-size: 32px;
                    font-weight: 400;
                    color: #333;
                }

                .btn-add-product {
                    background: #ffc107;
                    color: white;
                    padding: 12px 24px;
                    border: none;
                    border-radius: 4px;
                    font-size: 15px;
                    font-weight: 600;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.2s;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }

                .btn-add-product:hover {
                    background: #ffb300;
                    box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);
                }

                .table-controls {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 16px;
                    padding: 16px;
                    background: white;
                    border-radius: 4px;
                }

                .entries-control {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 15px;
                    color: #666;
                }

                .entries-select {
                    padding: 6px 12px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    font-size: 15px;
                    cursor: pointer;
                }

                .search-control {
                    position: relative;
                }

                .search-input {
                    padding: 8px 16px 8px 36px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    font-size: 15px;
                    width: 300px;
                    transition: all 0.3s;
                }

                .search-input:focus {
                    outline: none;
                    border-color: #ffc107;
                }

                .search-icon {
                    position: absolute;
                    left: 12px;
                    top: 50%;
                    transform: translateY(-50%);
                    font-size: 16px;
                    color: #999;
                }

                .btn {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 4px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .btn-primary {
                    background: #ffc107;
                    color: white;
                }

                .btn-primary:hover {
                    background: #ffb300;
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

                .products-table-container {
                    background: white;
                    border-radius: 4px;
                    overflow: hidden;
                }

                .products-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .products-table thead {
                    background: #f9f9f9;
                }

                .products-table th {
                    padding: 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #555;
                    font-size: 15px;
                    border-bottom: 1px solid #e0e0e0;
                }

                .products-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f0f0f0;
                    color: #666;
                    font-size: 15px;
                }

                .products-table tbody tr:hover {
                    background: #fafafa;
                }

                .action-dropdown {
                    position: relative;
                    display: inline-block;
                }

                .action-btn {
                    background: #ffc107;
                    color: white;
                    padding: 8px 16px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    white-space: nowrap;
                }

                .action-btn:hover {
                    background: #ffb300;
                }

                .dropdown-menu {
                    position: absolute;
                    top: calc(100% + 4px);
                    right: 0;
                    background: white;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                    min-width: 100%;
                    z-index: 1000;
                    overflow: hidden;
                }

                .dropdown-item {
                    padding: 10px 16px;
                    cursor: pointer;
                    font-size: 15px;
                    color: #333;
                    display: block;
                    width: 100%;
                    text-align: left;
                    border: none;
                    background: none;
                    transition: background 0.2s;
                }

                .dropdown-item:hover {
                    background: #f5f5f5;
                }

                .dropdown-item:not(:last-child) {
                    border-bottom: 1px solid #f0f0f0;
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
                    max-width: 600px;
                    max-height: 90vh;
                    overflow-y: auto;
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
                    border-color: #ffd54f;
                    box-shadow: 0 0 0 3px rgba(255, 213, 79, 0.1);
                }

                .form-input:disabled,
                .form-textarea:disabled {
                    background: #f5f5f5;
                    cursor: not-allowed;
                }

                .form-textarea {
                    resize: vertical;
                    min-height: 100px;
                }

                .form-row {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 16px;
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
                }

                .btn-secondary:hover {
                    background: #e0e0e0;
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

                .empty-subtext {
                    font-size: 14px;
                    color: #bbb;
                }

                @media (max-width: 768px) {
                    .pm-header {
                        flex-direction: column;
                        align-items: stretch;
                    }

                    .pm-actions {
                        flex-direction: column;
                    }

                    .search-input {
                        width: 100%;
                    }

                    .products-table-container {
                        overflow-x: auto;
                    }

                    .form-row {
                        grid-template-columns: 1fr;
                    }
                }
            `}</style>

            <div className="product-management">
                <div className="pm-header">
                    <h1 className="pm-title">Product List</h1>
                    <button className="btn-add-product" onClick={handleCreate}>
                        ➕ ADD NEW PRODUCT
                    </button>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                {loading ? (
                    <div className="loading">Loading products...</div>
                ) : (
                    <>
                        <div className="table-controls">
                            <div className="entries-control">
                                <span>Products</span>
                                <span style={{ marginLeft: '16px' }}>Show</span>
                                <select 
                                    className="entries-select"
                                    value={entriesPerPage}
                                    onChange={(e) => setEntriesPerPage(Number(e.target.value))}
                                >
                                    <option value="10">10</option>
                                    <option value="25">25</option>
                                    <option value="50">50</option>
                                    <option value="100">100</option>
                                </select>
                            </div>
                            <div className="search-control">
                                <span className="search-icon">🔍</span>
                                <input
                                    type="text"
                                    className="search-input"
                                    placeholder="Search Product Name"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                        </div>

                        {filteredProducts.length === 0 ? (
                            <div className="empty-state">
                                <div className="empty-icon">📦</div>
                                <div className="empty-text">
                                    {searchTerm ? "No products found" : "No products yet"}
                                </div>
                                <div className="empty-subtext">
                                    {searchTerm ? "Try a different search term" : "Click 'ADD NEW PRODUCT' to create your first product"}
                                </div>
                            </div>
                        ) : (
                            <div className="products-table-container">
                                <table className="products-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Image</th>
                                            <th>Product Code</th>
                                            <th>Name</th>
                                            <th>Category</th>
                                            <th>Base Price</th>
                                            <th>Options</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {filteredProducts.map((product, index) => (
                                            <tr key={product.productCode}>
                                                <td>{index + 1}</td>
                                                <td>
                                                    {product.imageUrl ? (
                                                        <img
                                                            src={product.imageUrl}
                                                            alt={product.name}
                                                            style={{
                                                                width: '50px',
                                                                height: '50px',
                                                                objectFit: 'cover',
                                                                borderRadius: '4px',
                                                                border: '1px solid #e0e0e0'
                                                            }}
                                                            onError={(e) => {
                                                                e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 50 50"%3E%3Crect fill="%23f5f5f5" width="50" height="50"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="20"%3E📦%3C/text%3E%3C/svg%3E';
                                                            }}
                                                        />
                                                    ) : (
                                                        <div style={{
                                                            width: '50px',
                                                            height: '50px',
                                                            display: 'flex',
                                                            alignItems: 'center',
                                                            justifyContent: 'center',
                                                            background: '#f5f5f5',
                                                            borderRadius: '4px',
                                                            border: '1px solid #e0e0e0',
                                                            fontSize: '24px'
                                                        }}>
                                                            📦
                                                        </div>
                                                    )}
                                                </td>
                                                <td>{product.productCode}</td>
                                                <td>{product.name}</td>
                                                <td>
                                                    {product.categoryId ? (
                                                        categories.find(cat => cat.categoryId === product.categoryId)?.categoryName || '-'
                                                    ) : '-'}
                                                </td>
                                                <td>Rs.{parseFloat(product.unitPrice || 0).toFixed(0)}</td>
                                                <td>
                                                    <div className="action-dropdown">
                                                        <button 
                                                            className="action-btn"
                                                            onClick={() => setActiveDropdown(activeDropdown === product.productCode ? null : product.productCode)}
                                                        >
                                                            Action ▼
                                                        </button>
                                                        {activeDropdown === product.productCode && (
                                                            <div className="dropdown-menu">
                                                                <button 
                                                                    className="dropdown-item"
                                                                    onClick={() => handleView(product)}
                                                                >
                                                                    View
                                                                </button>
                                                                <button 
                                                                    className="dropdown-item"
                                                                    onClick={() => handleEdit(product)}
                                                                >
                                                                    Edit
                                                                </button>
                                                                <button 
                                                                    className="dropdown-item"
                                                                    onClick={() => handleDelete(product.productCode)}
                                                                >
                                                                    Delete
                                                                </button>
                                                            </div>
                                                        )}
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </>
                )}

                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-header">
                                <h2 className="modal-title">
                                    {modalMode === "create" ? "➕ Create Product" :
                                     modalMode === "edit" ? "✏️ Edit Product" :
                                     "👁️ View Product"}
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
                                        <label className="form-label">Product Code *</label>
                                        <input
                                            type="text"
                                            className="form-input"
                                            name="productCode"
                                            value={formData.productCode}
                                            onChange={handleInputChange}
                                            disabled={modalMode === "edit" || modalMode === "view"}
                                            required
                                            placeholder="e.g., WATER001"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Product Name *</label>
                                        <input
                                            type="text"
                                            className="form-input"
                                            name="name"
                                            value={formData.name}
                                            onChange={handleInputChange}
                                            disabled={modalMode === "view"}
                                            required
                                            placeholder="e.g., Mineral Water 1L"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Category</label>
                                        <select
                                            className="form-input"
                                            name="categoryId"
                                            value={formData.categoryId}
                                            onChange={handleInputChange}
                                            disabled={modalMode === "view"}
                                            style={{ cursor: modalMode === "view" ? 'not-allowed' : 'pointer' }}
                                        >
                                            <option value="">Select a category (optional)</option>
                                            {categories.map((category) => (
                                                <option key={category.categoryId} value={category.categoryId}>
                                                    {category.categoryName}
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Unit Price ($) *</label>
                                        <input
                                            type="number"
                                            step="0.01"
                                            className="form-input"
                                            name="unitPrice"
                                            value={formData.unitPrice}
                                            onChange={handleInputChange}
                                            disabled={modalMode === "view"}
                                            required
                                            placeholder="80.00"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Product Image</label>
                                        
                                        {modalMode !== "view" && (
                                            <div>
                                                <input
                                                    type="file"
                                                    className="form-input"
                                                    accept="image/*"
                                                    onChange={handleImageChange}
                                                    disabled={modalMode === "view"}
                                                    style={{ padding: '8px' }}
                                                />
                                                <div style={{ 
                                                    fontSize: '13px', 
                                                    color: '#666', 
                                                    marginTop: '4px' 
                                                }}>
                                                    Upload an image or enter URL below
                                                </div>
                                            </div>
                                        )}
                                        
                                        {imagePreview && (
                                            <div style={{ 
                                                marginTop: '12px',
                                                textAlign: 'center'
                                            }}>
                                                <img 
                                                    src={imagePreview} 
                                                    alt="Preview" 
                                                    style={{
                                                        maxWidth: '200px',
                                                        maxHeight: '200px',
                                                        borderRadius: '8px',
                                                        border: '2px solid #e0e0e0',
                                                        objectFit: 'contain'
                                                    }}
                                                />
                                            </div>
                                        )}
                                        
                                        {modalMode !== "view" && (
                                            <input
                                                type="text"
                                                className="form-input"
                                                name="imageUrl"
                                                value={formData.imageUrl || ''}
                                                onChange={handleInputChange}
                                                placeholder="Or paste image URL here (optional)"
                                                style={{ marginTop: '8px' }}
                                            />
                                        )}
                                    </div>

                                    {modalMode === "view" && (
                                        <>
                                            <div className="form-group">
                                                <label className="form-label">Status</label>
                                                <input
                                                    type="text"
                                                    className="form-input"
                                                    value={formData.status || ''}
                                                    disabled
                                                />
                                            </div>

                                            <div className="form-row">
                                                <div className="form-group">
                                                    <label className="form-label">Shelf Quantity</label>
                                                    <input
                                                        type="number"
                                                        className="form-input"
                                                        value={formData.shelfQuantity || 0}
                                                        disabled
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label className="form-label">Warehouse Quantity</label>
                                                    <input
                                                        type="number"
                                                        className="form-input"
                                                        value={formData.warehouseQuantity || 0}
                                                        disabled
                                                    />
                                                </div>
                                            </div>

                                            <div className="form-row">
                                                <div className="form-group">
                                                    <label className="form-label">Website Quantity</label>
                                                    <input
                                                        type="number"
                                                        className="form-input"
                                                        value={formData.websiteQuantity || 0}
                                                        disabled
                                                    />
                                                </div>

                                                <div className="form-group">
                                                    <label className="form-label">Total Quantity</label>
                                                    <input
                                                        type="number"
                                                        className="form-input"
                                                        value={formData.totalQuantity || 0}
                                                        disabled
                                                    />
                                                </div>
                                            </div>

                                            <div className="form-group">
                                                <label className="form-label">Needs Reordering</label>
                                                <input
                                                    type="text"
                                                    className="form-input"
                                                    value={formData.needsReordering ? 'Yes ⚠️' : 'No'}
                                                    disabled
                                                />
                                            </div>
                                        </>
                                    )}
                                </div>

                                <div className="modal-footer">
                                    <button
                                        type="button"
                                        className="btn btn-secondary"
                                        onClick={() => setShowModal(false)}
                                    >
                                        {modalMode === "view" ? "Close" : "Cancel"}
                                    </button>
                                    {modalMode !== "view" && (
                                        <button type="submit" className="btn btn-primary">
                                            {modalMode === "create" ? "Create Product" : "Update Product"}
                                        </button>
                                    )}
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}


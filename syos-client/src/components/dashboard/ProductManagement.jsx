import { useState, useEffect } from "react";
import {
    apiGetProducts,
    apiCreateProduct,
    apiUpdateProduct,
    apiDeleteProduct
} from "../../services/api";

export default function ProductManagement() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState("create"); // create, edit, view
    const [formData, setFormData] = useState({
        productCode: "",
        name: "",
        unitPrice: "",
        imageUrl: ""
    });

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await apiGetProducts();
            if (response.success) {
                setProducts(response.data || []);
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
            imageUrl: ""
        });
        setShowModal(true);
    };

    const handleEdit = async (product) => {
        setModalMode("edit");
        setFormData({
            productCode: product.productCode || "",
            name: product.name || "",
            unitPrice: product.unitPrice || "",
            imageUrl: product.imageUrl || ""
        });
        setShowModal(true);
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
        setShowModal(true);
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

    const filteredProducts = Array.isArray(products) 
        ? products.filter(product =>
            product.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            product.productCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            product.status?.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : [];

    return (
        <>
            <style>{`
                .product-management {
                    width: 100%;
                }

                .pm-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                    flex-wrap: wrap;
                    gap: 16px;
                }

                .pm-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #333;
                }

                .pm-actions {
                    display: flex;
                    gap: 12px;
                    align-items: center;
                }

                .search-box {
                    position: relative;
                }

                .search-input {
                    padding: 10px 16px 10px 40px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                    width: 280px;
                    transition: all 0.3s;
                }

                .search-input:focus {
                    outline: none;
                    border-color: #ffd54f;
                    box-shadow: 0 0 0 3px rgba(255, 213, 79, 0.1);
                }

                .search-icon {
                    position: absolute;
                    left: 12px;
                    top: 50%;
                    transform: translateY(-50%);
                    font-size: 18px;
                }

                .btn {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .btn-primary {
                    background: #ffd54f;
                    color: #333;
                }

                .btn-primary:hover {
                    background: #ffc107;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
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
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    overflow: hidden;
                }

                .products-table {
                    width: 100%;
                    border-collapse: collapse;
                }

                .products-table thead {
                    background: #f5f5f5;
                }

                .products-table th {
                    padding: 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #666;
                    font-size: 13px;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    border-bottom: 2px solid #e0e0e0;
                }

                .products-table td {
                    padding: 16px;
                    border-bottom: 1px solid #f0f0f0;
                    color: #333;
                }

                .products-table tbody tr {
                    transition: background 0.2s;
                }

                .products-table tbody tr:hover {
                    background: #fafafa;
                }

                .product-image {
                    width: 50px;
                    height: 50px;
                    border-radius: 8px;
                    object-fit: cover;
                    border: 2px solid #f0f0f0;
                }

                .product-code {
                    font-family: 'Courier New', monospace;
                    background: #f5f5f5;
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                }

                .product-price {
                    font-weight: 600;
                    color: #2e7d32;
                }

                .product-stock {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: 600;
                }

                .stock-high {
                    background: #e8f5e9;
                    color: #2e7d32;
                }

                .stock-medium {
                    background: #fff3e0;
                    color: #f57c00;
                }

                .stock-low {
                    background: #ffebee;
                    color: #c62828;
                }

                .action-buttons {
                    display: flex;
                    gap: 8px;
                }

                .btn-icon {
                    padding: 8px 12px;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                    transition: all 0.2s;
                    font-size: 16px;
                }

                .btn-view {
                    background: #e3f2fd;
                    color: #1976d2;
                }

                .btn-view:hover {
                    background: #bbdefb;
                    transform: scale(1.1);
                }

                .btn-edit {
                    background: #fff3e0;
                    color: #f57c00;
                }

                .btn-edit:hover {
                    background: #ffe0b2;
                    transform: scale(1.1);
                }

                .btn-delete {
                    background: #ffebee;
                    color: #c62828;
                }

                .btn-delete:hover {
                    background: #ffcdd2;
                    transform: scale(1.1);
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
                    <h1 className="pm-title">📦 Product Management</h1>
                    <div className="pm-actions">
                        <div className="search-box">
                            <span className="search-icon">🔍</span>
                            <input
                                type="text"
                                className="search-input"
                                placeholder="Search products..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                        <button className="btn btn-primary" onClick={handleCreate}>
                            ➕ Add Product
                        </button>
                    </div>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                {loading ? (
                    <div className="loading">Loading products...</div>
                ) : filteredProducts.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">📦</div>
                        <div className="empty-text">
                            {searchTerm ? "No products found" : "No products yet"}
                        </div>
                        <div className="empty-subtext">
                            {searchTerm ? "Try a different search term" : "Click 'Add Product' to create your first product"}
                        </div>
                    </div>
                ) : (
                    <div className="products-table-container">
                        <table className="products-table">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Unit Price</th>
                                    <th>Total Stock</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredProducts.map((product) => (
                                    <tr key={product.productCode}>
                                        <td>
                                            {product.imageUrl ? (
                                                <img
                                                    src={product.imageUrl}
                                                    alt={product.name}
                                                    className="product-image"
                                                    onError={(e) => {
                                                        e.target.style.display = 'none';
                                                    }}
                                                />
                                            ) : (
                                                <div className="product-image" style={{
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    background: '#f5f5f5',
                                                    fontSize: '24px'
                                                }}>📦</div>
                                            )}
                                        </td>
                                        <td>
                                            <span className="product-code">{product.productCode}</span>
                                        </td>
                                        <td>{product.name}</td>
                                        <td className="product-price">
                                            ${parseFloat(product.unitPrice || 0).toFixed(2)}
                                        </td>
                                        <td>
                                            <span className={`product-stock ${
                                                product.totalQuantity > 50 ? 'stock-high' :
                                                product.totalQuantity > 20 ? 'stock-medium' : 'stock-low'
                                            }`}>
                                                {product.totalQuantity || 0}
                                            </span>
                                        </td>
                                        <td>
                                            <span className={`product-stock ${
                                                product.status === 'In Stock' ? 'stock-high' :
                                                product.status === 'Low Stock' ? 'stock-medium' : 'stock-low'
                                            }`}>
                                                {product.status || 'Unknown'}
                                                {product.needsReordering && ' ⚠️'}
                                            </span>
                                        </td>
                                        <td>
                                            <div className="action-buttons">
                                                <button
                                                    className="btn-icon btn-view"
                                                    onClick={() => handleView(product)}
                                                    title="View"
                                                >
                                                    👁️
                                                </button>
                                                <button
                                                    className="btn-icon btn-edit"
                                                    onClick={() => handleEdit(product)}
                                                    title="Edit"
                                                >
                                                    ✏️
                                                </button>
                                                <button
                                                    className="btn-icon btn-delete"
                                                    onClick={() => handleDelete(product.productCode)}
                                                    title="Delete"
                                                >
                                                    🗑️
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
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
                                        <label className="form-label">Image URL</label>
                                        <input
                                            type="text"
                                            className="form-input"
                                            name="imageUrl"
                                            value={formData.imageUrl || ''}
                                            onChange={handleInputChange}
                                            disabled={modalMode === "view"}
                                            placeholder="https://example.com/image.jpg (optional)"
                                        />
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


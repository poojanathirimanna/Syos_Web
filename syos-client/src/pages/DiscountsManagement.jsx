import { useEffect, useState } from "react";
import { apiGetProducts, apiSetProductDiscount, apiRemoveProductDiscount, apiSetBatchDiscount, apiRemoveBatchDiscount, apiGetProductBatches } from "../services/api";

export default function DiscountsManagement() {
    const [activeTab, setActiveTab] = useState("product");
    const [products, setProducts] = useState([]);
    const [search, setSearch] = useState("");

    // Product discount modal state
    const [showProductModal, setShowProductModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [form, setForm] = useState({ productCode: "", percentage: 0, startDate: "", endDate: "" });

    // Batch discounts
    const [batches, setBatches] = useState([]);
    const [selectedProductForBatch, setSelectedProductForBatch] = useState("");
    const [showBatchModal, setShowBatchModal] = useState(false);
    const [batchForm, setBatchForm] = useState({ productCode: "", batchId: "", percentage: 0, startDate: "", endDate: "" });

    // Toast notifications
    const [toasts, setToasts] = useState([]);
    
    // Confirmation modal
    const [confirmModal, setConfirmModal] = useState({ show: false, message: '', onConfirm: null });

    const showToast = (message, type = 'success') => {
        const id = Date.now();
        setToasts(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, 4000);
    };

    const showConfirm = (message, onConfirm) => {
        setConfirmModal({ show: true, message, onConfirm });
    };

    const handleConfirm = () => {
        if (confirmModal.onConfirm) confirmModal.onConfirm();
        setConfirmModal({ show: false, message: '', onConfirm: null });
    };

    const handleCancel = () => {
        setConfirmModal({ show: false, message: '', onConfirm: null });
    };

    useEffect(() => { loadProducts(); }, []);

    const loadProducts = async () => {
        try {
            const res = await apiGetProducts();
            
            if (res.success) {
                const productsData = res.data?.products || res.data || [];
                setProducts(Array.isArray(productsData) ? [...productsData] : []);
            } else {
                setProducts([]);
            }
        } catch (err) { 
            setProducts([]);
        }
    };

    const openAddProduct = () => {
        setEditingProduct(null);
        setForm({ productCode: "", percentage: 0, startDate: "", endDate: "" });
        setShowProductModal(true);
    };

    const openEditProduct = (p) => {
        setEditingProduct(p);
        setForm({ 
            productCode: p.productCode, 
            percentage: p.discountPercentage || 0, 
            startDate: p.discountStartDate || "", 
            endDate: p.discountEndDate || "" 
        });
        setShowProductModal(true);
    };

    const applyProductDiscount = async () => {
        const payload = {
            productCode: form.productCode,
            discountPercentage: Number(form.percentage),
            startDate: form.startDate || null,
            endDate: form.endDate || null
        };
        try {
            const res = await apiSetProductDiscount(payload);
            if (res.success) {
                setShowProductModal(false);
                await loadProducts();
                showToast(`${form.percentage}% discount applied to ${form.productCode}`, 'success');
            } else {
                showToast(res.message || "Failed to apply discount", 'error');
            }
        } catch (err) { 
            showToast(err.message || 'Error applying discount', 'error'); 
        }
    };

    const removeProductDiscount = async (code) => {
        showConfirm(`Remove discount for ${code}?`, async () => {
            try {
                const res = await apiRemoveProductDiscount(code);
                if (res.success) {
                    await loadProducts();
                    showToast(`Discount removed from ${code}`, 'success');
                } else {
                    showToast(res.message || "Failed to remove discount", 'error');
                }
            } catch (err) { 
                showToast(err.message || 'Error removing discount', 'error'); 
            }
        });
    };

    // Batch flows
    const openAddBatch = () => {
        setBatchForm({ productCode: "", batchId: "", percentage: 0, startDate: "", endDate: "" });
        setSelectedProductForBatch("");
        setBatches([]);
        setShowBatchModal(true);
    };

    const loadBatchesForProduct = async (code) => {
        setSelectedProductForBatch(code);
        setBatchForm({ ...batchForm, productCode: code });
        try {
            const res = await apiGetProductBatches(code);
            if (res.success) {
                setBatches(res.data || []);
            } else {
                setBatches([]);
            }
        } catch (err) { 
            console.error(err); 
            setBatches([]);
        }
    };

    const applyBatchDiscount = async () => {
        const payload = {
            batchId: batchForm.batchId,
            discountPercentage: Number(batchForm.percentage),
            startDate: batchForm.startDate || null,
            endDate: batchForm.endDate || null
        };
        try {
            const res = await apiSetBatchDiscount(payload);
            if (res.success) { 
                setShowBatchModal(false);
                if (selectedProductForBatch) {
                    await loadBatchesForProduct(selectedProductForBatch);
                }
                showToast(`${batchForm.percentage}% discount applied to Batch #${batchForm.batchId}`, 'success');
            } else {
                showToast(res.message || "Failed to apply batch discount", 'error');
            }
        } catch (err) { 
            showToast(err.message || 'Error applying batch discount', 'error'); 
        }
    };

    const removeBatchDiscount = async (batchId) => {
        showConfirm(`Remove discount for batch ${batchId}?`, async () => {
            try {
                const res = await apiRemoveBatchDiscount(batchId);
                if (res.success) { 
                    if (selectedProductForBatch) {
                        await loadBatchesForProduct(selectedProductForBatch);
                    }
                    showToast(`Discount removed from Batch #${batchId}`, 'success');
                } else {
                    showToast(res.message || "Failed to remove batch discount", 'error');
                }
            } catch (err) { 
                showToast(err.message || 'Error removing batch discount', 'error'); 
            }
        });
    };

    const filtered = products.filter(p =>
        p.name?.toLowerCase().includes(search.toLowerCase()) || p.productCode?.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <>
            <style>{`
                .discounts-container { padding: 20px; }
                .discounts-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
                .discounts-title { font-size: 24px; font-weight: 700; color: #333; }
                .tabs-container { display: flex; gap: 8px; margin-bottom: 20px; border-bottom: 2px solid #e0e0e0; }
                .tab-btn { padding: 12px 24px; border: none; background: transparent; cursor: pointer; font-weight: 600; font-size: 14px; color: #666; transition: all 0.2s; border-bottom: 3px solid transparent; text-align: center; }
                .tab-btn:hover { color: #52B788; background: rgba(82, 183, 136, 0.05); }
                .tab-btn.active { color: #52B788; border-bottom-color: #52B788; background: rgba(82, 183, 136, 0.08); }
                .controls-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
                .search-input { padding: 10px 16px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; width: 300px; }
                .search-input:focus { outline: none; border-color: #52B788; }
                .btn-primary { background: linear-gradient(135deg, #52B788 0%, #40916C 100%); color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
                .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3); }
                .discounts-table { width: 100%; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
                .discounts-table table { width: 100%; border-collapse: collapse; }
                .discounts-table th { background: #f5f5f5; padding: 16px; text-align: left; font-weight: 600; color: #666; font-size: 13px; text-transform: uppercase; }
                .discounts-table td { padding: 16px; border-top: 1px solid #e0e0e0; color: #333; }
                .badge { display: inline-block; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }
                .badge.active { background: #e8f5e9; color: #4caf50; }
                .badge.inactive { background: #f5f5f5; color: #999; }
                .badge.warning { background: #fff3e0; color: #ff9800; }
                .btn-small { padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s; margin-right: 6px; border: none; }
                .btn-edit { background: #e3f2fd; color: #1976d2; }
                .btn-edit:hover { background: #1976d2; color: white; }
                .btn-remove { background: #ffebee; color: #d32f2f; }
                .btn-remove:hover { background: #d32f2f; color: white; }
                .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 9999; }
                .modal { background: white; border-radius: 16px; padding: 32px; width: 90%; max-width: 600px; max-height: 85vh; overflow-y: auto; box-shadow: 0 8px 32px rgba(0,0,0,0.2); }
                .modal-title { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 24px; }
                .form-group { margin-bottom: 16px; }
                .form-label { display: block; font-weight: 600; color: #333; margin-bottom: 8px; font-size: 14px; }
                .form-input, .form-select { width: 100%; padding: 10px 12px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; }
                .form-input:focus, .form-select:focus { outline: none; border-color: #52B788; }
                .form-row { display: flex; gap: 12px; }
                .form-row .form-group { flex: 1; }
                .modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px; }
                .btn-cancel { background: white; color: #666; border: 2px solid #e0e0e0; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
                .btn-cancel:hover { border-color: #f44336; color: #f44336; }
                .empty-state { text-align: center; padding: 60px 20px; color: #999; }
                .toast-container { position: fixed; top: 20px; right: 20px; z-index: 10000; display: flex; flex-direction: column; gap: 10px; }
                .toast { min-width: 300px; padding: 16px 20px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); display: flex; align-items: center; gap: 12px; animation: slideIn 0.3s ease-out; font-weight: 500; }
                .toast.success { background: #4caf50; color: white; }
                .toast.error { background: #f44336; color: white; }
                .toast-icon { font-size: 20px; }
                @keyframes slideIn { from { transform: translateX(400px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
                .confirm-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 10001; animation: fadeIn 0.2s ease-out; }
                .confirm-dialog { background: white; border-radius: 12px; padding: 24px; min-width: 400px; box-shadow: 0 8px 32px rgba(0,0,0,0.3); animation: scaleIn 0.2s ease-out; }
                .confirm-message { font-size: 16px; color: #333; margin-bottom: 24px; font-weight: 500; }
                .confirm-actions { display: flex; gap: 12px; justify-content: flex-end; }
                .confirm-btn { padding: 10px 24px; border-radius: 8px; font-weight: 600; cursor: pointer; border: none; font-size: 14px; transition: all 0.2s; }
                .confirm-btn.cancel { background: #f5f5f5; color: #666; }
                .confirm-btn.cancel:hover { background: #e0e0e0; }
                .confirm-btn.ok { background: linear-gradient(135deg, #52B788 0%, #40916C 100%); color: white; }
                .confirm-btn.ok:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3); }
                @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
                @keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
            `}</style>

            {/* Confirmation Modal */}
            {confirmModal.show && (
                <div className="confirm-overlay" onClick={handleCancel}>
                    <div className="confirm-dialog" onClick={(e) => e.stopPropagation()}>
                        <div className="confirm-message">{confirmModal.message}</div>
                        <div className="confirm-actions">
                            <button className="confirm-btn cancel" onClick={handleCancel}>Cancel</button>
                            <button className="confirm-btn ok" onClick={handleConfirm}>OK</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Toast Notifications */}
            <div className="toast-container">
                {toasts.map(toast => (
                    <div key={toast.id} className={`toast ${toast.type}`}>
                        <span className="toast-icon">{toast.type === 'success' ? '✅' : '⚠️'}</span>
                        <span>{toast.message}</span>
                    </div>
                ))}
            </div>

            <div className="discounts-container">
                <div className="discounts-header">
                    <h1 className="discounts-title">Discount Management</h1>
                </div>

                <div className="tabs-container">
                    <button onClick={() => setActiveTab('product')} className={`tab-btn ${activeTab === 'product' ? 'active' : ''}`}>
                        📦 Product Discounts
                        <div style={{fontSize: '11px', fontWeight: '400', marginTop: '2px', opacity: '0.8'}}>
                            (Applies to all batches)
                        </div>
                    </button>
                    <button onClick={() => setActiveTab('batch')} className={`tab-btn ${activeTab === 'batch' ? 'active' : ''}`}>
                        🏷️ Batch Discounts
                        <div style={{fontSize: '11px', fontWeight: '400', marginTop: '2px', opacity: '0.8'}}>
                            (Specific batch only)
                        </div>
                    </button>
                </div>

                {activeTab === 'product' ? (
                    <div>
                        <div className="controls-row">
                            <input 
                                className="search-input"
                                placeholder="Search product name or code..." 
                                value={search} 
                                onChange={(e) => setSearch(e.target.value)} 
                            />
                            <button className="btn-primary" onClick={openAddProduct}>+ Add Discount</button>
                        </div>

                        <div className="discounts-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Product Code</th>
                                        <th>Product Name</th>
                                        <th>Original Price</th>
                                        <th>Current Discount %</th>
                                        <th>Discount Period</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filtered.length === 0 ? (
                                        <tr><td colSpan={7} className="empty-state">No products found</td></tr>
                                    ) : (
                                        filtered.map(p => (
                                            <tr key={p.productCode}>
                                                <td>{p.productCode}</td>
                                                <td>{p.name}</td>
                                                <td>Rs. {Number(p.unitPrice || p.price || 0).toFixed(2)}</td>
                                                <td>{p.discountPercentage ? `${p.discountPercentage}%` : '—'}</td>
                                                <td>{p.discountStartDate ? `${p.discountStartDate} - ${p.discountEndDate || '∞'}` : '—'}</td>
                                                <td>
                                                    <span className={`badge ${p.discountPercentage ? 'active' : 'inactive'}`}>
                                                        {p.discountPercentage ? 'Active' : 'Inactive'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <button className="btn-small btn-edit" onClick={() => openEditProduct(p)}>Edit</button>
                                                    {p.discountPercentage && (
                                                        <button className="btn-small btn-remove" onClick={() => removeProductDiscount(p.productCode)}>Remove</button>
                                                    )}
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                ) : (
                    <div>
                        <div className="controls-row">
                            <div style={{display: 'flex', gap: '12px', alignItems: 'center', flex: 1}}>
                                <select 
                                    className="search-input" 
                                    style={{width: '350px'}}
                                    value={selectedProductForBatch}
                                    onChange={(e) => loadBatchesForProduct(e.target.value)}
                                >
                                    <option value="">🔍 Select a product to view its batches...</option>
                                    {products.map(p => (
                                        <option key={p.productCode} value={p.productCode}>
                                            {p.productCode} — {p.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <button className="btn-primary" onClick={openAddBatch}>+ Add Batch Discount</button>
                        </div>

                        <div className="discounts-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Batch ID</th>
                                        <th>Product Name</th>
                                        <th>Expiry Date</th>
                                        <th>Days Until Expiry</th>
                                        <th>Available Quantity</th>
                                        <th>Current Discount %</th>
                                        <th>Discount Period</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {!selectedProductForBatch ? (
                                        <tr><td colSpan={9} className="empty-state">
                                            👆 Select a product from the dropdown above to view and manage its batches
                                        </td></tr>
                                    ) : batches.length === 0 ? (
                                        <tr><td colSpan={9} className="empty-state">
                                            No batches found for this product
                                        </td></tr>
                                    ) : (
                                        batches.map(batch => {
                                            const daysLeft = batch.daysUntilExpiry || 0;
                                            const isNearExpiry = daysLeft < 7;
                                            return (
                                                <tr key={batch.batchId} style={isNearExpiry ? {background: '#fff8e1'} : {}}>
                                                    <td><strong>#{batch.batchId}</strong></td>
                                                    <td>{batch.productName}</td>
                                                    <td>{batch.expiryDate}</td>
                                                    <td>
                                                        <span className={`badge ${isNearExpiry ? 'warning' : ''}`}>
                                                            {isNearExpiry && '⚠️ '}
                                                            {daysLeft} {daysLeft === 1 ? 'day' : 'days'}
                                                        </span>
                                                    </td>
                                                    <td>{batch.availableQuantity}</td>
                                                    <td>{batch.discountPercentage ? `${batch.discountPercentage}%` : '—'}</td>
                                                    <td>{batch.discountStartDate ? `${batch.discountStartDate} - ${batch.discountEndDate || '∞'}` : '—'}</td>
                                                    <td>
                                                        <span className={`badge ${batch.discountPercentage ? 'active' : 'inactive'}`}>
                                                            {batch.discountPercentage ? 'Active' : 'Inactive'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <button className="btn-small btn-edit" onClick={() => {
                                                            setBatchForm({ 
                                                                productCode: batch.productCode, 
                                                                batchId: batch.batchId, 
                                                                percentage: batch.discountPercentage || 0, 
                                                                startDate: batch.discountStartDate || '', 
                                                                endDate: batch.discountEndDate || '' 
                                                            }); 
                                                            setShowBatchModal(true);
                                                        }}>Edit</button>
                                                        {batch.discountPercentage && (
                                                            <button className="btn-small btn-remove" onClick={() => removeBatchDiscount(batch.batchId)}>Remove</button>
                                                        )}
                                                    </td>
                                                </tr>
                                            );
                                        })
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}

                {/* Product Discount Modal */}
                {showProductModal && (
                    <div className="modal-overlay" onClick={() => setShowProductModal(false)}>
                        <div className="modal" onClick={(e) => e.stopPropagation()}>
                            <h3 className="modal-title">{editingProduct ? 'Edit Product Discount' : 'Add Product Discount'}</h3>
                            <div className="form-group">
                                <label className="form-label">Product</label>
                                <select 
                                    className="form-select"
                                    value={form.productCode} 
                                    onChange={(e) => setForm({...form, productCode: e.target.value})}
                                    disabled={editingProduct}
                                >
                                    <option value="">-- Select Product --</option>
                                    {products.map(p => (
                                        <option key={p.productCode} value={p.productCode}>
                                            {p.productCode} — {p.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-row">
                                <div className="form-group">
                                    <label className="form-label">Discount %</label>
                                    <input 
                                        className="form-input"
                                        type="number" 
                                        min={0} 
                                        max={100} 
                                        value={form.percentage} 
                                        onChange={(e) => setForm({...form, percentage: e.target.value})} 
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Start Date (Optional)</label>
                                    <input 
                                        className="form-input"
                                        type="date" 
                                        value={form.startDate} 
                                        onChange={(e) => setForm({...form, startDate: e.target.value})} 
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">End Date (Optional)</label>
                                    <input 
                                        className="form-input"
                                        type="date" 
                                        value={form.endDate} 
                                        onChange={(e) => setForm({...form, endDate: e.target.value})} 
                                    />
                                </div>
                            </div>
                            
                            <div style={{background: '#e8f5e9', padding: '12px 16px', borderRadius: '8px', marginTop: '16px', border: '2px solid #81c784'}}>
                                <div style={{display: 'flex', alignItems: 'center', gap: '8px', color: '#2e7d32', fontSize: '14px', fontWeight: '600'}}>
                                    <span style={{fontSize: '18px'}}>ℹ️</span>
                                    <span>This will apply to ALL batches of this product</span>
                                </div>
                                <p style={{margin: '8px 0 0 26px', fontSize: '13px', color: '#1b5e20', lineHeight: '1.4'}}>
                                    Perfect for store-wide promotions, brand deals, or category sales that affect the entire product line regardless of batch.
                                </p>
                            </div>

                            <div className="modal-actions">
                                <button className="btn-cancel" onClick={() => setShowProductModal(false)}>Cancel</button>
                                <button className="btn-primary" onClick={applyProductDiscount}>Apply Discount</button>
                            </div>
                        </div>
                    </div>
                )}

                {/* Batch Discount Modal */}
                {showBatchModal && (
                    <div className="modal-overlay" onClick={() => setShowBatchModal(false)}>
                        <div className="modal" onClick={(e) => e.stopPropagation()}>
                            <h3 className="modal-title">{batchForm.batchId ? 'Edit Batch Discount' : 'Add Batch Discount'}</h3>
                            <div className="form-group">
                                <label className="form-label">Product</label>
                                <select 
                                    className="form-select"
                                    value={batchForm.productCode} 
                                    onChange={(e) => loadBatchesForProduct(e.target.value)}
                                >
                                    <option value="">-- Select Product --</option>
                                    {products.map(p => (
                                        <option key={p.productCode} value={p.productCode}>
                                            {p.productCode} — {p.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Batch</label>
                                <select 
                                    className="form-select"
                                    value={batchForm.batchId} 
                                    onChange={(e) => setBatchForm({...batchForm, batchId: e.target.value})}
                                    disabled={!batchForm.productCode}
                                >
                                    <option value="">-- Select Batch --</option>
                                    {batches.map(b => (
                                        <option key={b.batchId} value={b.batchId}>
                                            Batch #{b.batchId} — Exp: {b.expiryDate} — Qty: {b.availableQuantity}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label className="form-label">Discount %</label>
                                    <input 
                                        className="form-input"
                                        type="number" 
                                        min={0} 
                                        max={100} 
                                        value={batchForm.percentage} 
                                        onChange={(e) => setBatchForm({...batchForm, percentage: e.target.value})} 
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Start Date (Optional)</label>
                                    <input 
                                        className="form-input"
                                        type="date" 
                                        value={batchForm.startDate} 
                                        onChange={(e) => setBatchForm({...batchForm, startDate: e.target.value})} 
                                    />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">End Date (Optional)</label>
                                    <input 
                                        className="form-input"
                                        type="date" 
                                        value={batchForm.endDate} 
                                        onChange={(e) => setBatchForm({...batchForm, endDate: e.target.value})} 
                                    />
                                </div>
                            </div>

                            <div style={{background: '#fff3e0', padding: '12px 16px', borderRadius: '8px', marginTop: '16px', border: '2px solid #ffb74d'}}>
                                <div style={{display: 'flex', alignItems: 'center', gap: '8px', color: '#e65100', fontSize: '14px', fontWeight: '600'}}>
                                    <span style={{fontSize: '18px'}}>⚠️</span>
                                    <span>This applies ONLY to this specific batch</span>
                                </div>
                                <p style={{margin: '8px 0 0 26px', fontSize: '13px', color: '#e65100', lineHeight: '1.4'}}>
                                    Ideal for clearance sales on near-expiry items, damaged stock, overstocked batches, or emergency clearance situations.
                                </p>
                            </div>

                            <div className="modal-actions">
                                <button className="btn-cancel" onClick={() => setShowBatchModal(false)}>Cancel</button>
                                <button className="btn-primary" onClick={applyBatchDiscount}>Apply Discount</button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}

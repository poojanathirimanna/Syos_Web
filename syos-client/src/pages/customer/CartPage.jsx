import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { apiGetCart, apiUpdateCartItem, apiRemoveCartItem, apiClearCart } from "../../services/api";

export default function CartPage({ user, onLogout }) {
    const [cart, setCart] = useState({ items: [], subtotal: 0, totalDiscount: 0, total: 0 });
    const [loading, setLoading] = useState(true);
    const [notification, setNotification] = useState(null);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        setLoading(true);
        const result = await apiGetCart();
        if (result.success && result.data) {
            // Backend returns: { items: [], summary: { subtotal, totalDiscount, totalAmount } }
            const cartData = {
                items: result.data.items || [],
                subtotal: result.data.summary?.subtotal || 0,
                totalDiscount: result.data.summary?.totalDiscount || 0,
                total: result.data.summary?.totalAmount || 0
            };
            setCart(cartData);
        } else {
            showNotification("Failed to load cart", "error");
        }
        setLoading(false);
    };

    const handleUpdateQuantity = async (cartId, newQuantity) => {
        if (newQuantity < 1) return;
        
        const result = await apiUpdateCartItem(cartId, newQuantity);
        if (result.success) {
            loadCart(); // Reload cart to get updated totals
        } else {
            showNotification(result.message || "Failed to update quantity", "error");
        }
    };

    const handleRemoveItem = async (cartId) => {
        const result = await apiRemoveCartItem(cartId);
        if (result.success) {
            showNotification("Item removed from cart", "success");
            loadCart();
        } else {
            showNotification(result.message || "Failed to remove item", "error");
        }
    };

    const handleClearCart = () => {
        setShowConfirmModal(true);
    };

    const confirmClearCart = async () => {
        setShowConfirmModal(false);
        const result = await apiClearCart();
        if (result.success) {
            showNotification("Cart cleared", "success");
            loadCart();
        } else {
            showNotification(result.message || "Failed to clear cart", "error");
        }
    };

    const cancelClearCart = () => {
        setShowConfirmModal(false);
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    return (
        <>
            <style>{`
                .cart-page {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                }
                .cart-container {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .page-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                }
                .page-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0;
                }
                .clear-cart-btn {
                    background: #ef4444;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .clear-cart-btn:hover {
                    background: #dc2626;
                }
                .cart-content {
                    display: grid;
                    grid-template-columns: 1fr 400px;
                    gap: 24px;
                }
                .cart-items {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                .cart-item {
                    display: flex;
                    gap: 16px;
                    padding: 16px 0;
                    border-bottom: 1px solid #e5e7eb;
                }
                .cart-item:last-child {
                    border-bottom: none;
                }
                .item-image {
                    width: 100px;
                    height: 100px;
                    object-fit: cover;
                    border-radius: 8px;
                    background: #f3f4f6;
                }
                .item-details {
                    flex: 1;
                }
                .item-name {
                    font-size: 18px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .item-price {
                    font-size: 16px;
                    color: #22c55e;
                    font-weight: 600;
                }
                .item-original-price {
                    font-size: 14px;
                    color: #9ca3af;
                    text-decoration: line-through;
                    margin-left: 8px;
                }
                .item-discount-badge {
                    display: inline-block;
                    background: #fef3c7;
                    color: #f59e0b;
                    padding: 2px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                    margin-left: 8px;
                }
                .item-actions {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    margin-top: 12px;
                }
                .quantity-controls {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    border: 2px solid #e5e7eb;
                    border-radius: 8px;
                    overflow: hidden;
                }
                .qty-btn {
                    background: #f3f4f6;
                    border: none;
                    width: 36px;
                    height: 36px;
                    cursor: pointer;
                    font-weight: 600;
                    transition: background 0.2s;
                }
                .qty-btn:hover {
                    background: #e5e7eb;
                }
                .qty-input {
                    width: 50px;
                    text-align: center;
                    border: none;
                    font-weight: 600;
                }
                .remove-btn {
                    background: none;
                    border: none;
                    color: #ef4444;
                    cursor: pointer;
                    font-weight: 600;
                    transition: color 0.2s;
                }
                .remove-btn:hover {
                    color: #dc2626;
                }
                .cart-summary {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    height: fit-content;
                    position: sticky;
                    top: 90px;
                }
                .summary-title {
                    font-size: 20px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 16px 0;
                }
                .summary-row {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 12px;
                    font-size: 15px;
                }
                .summary-row.total {
                    font-size: 20px;
                    font-weight: 700;
                    color: #1f2937;
                    padding-top: 16px;
                    border-top: 2px solid #e5e7eb;
                    margin-top: 16px;
                }
                .savings-text {
                    color: #10b981;
                    font-weight: 600;
                }
                .checkout-btn {
                    width: 100%;
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white;
                    border: none;
                    padding: 16px;
                    border-radius: 12px;
                    font-size: 17px;
                    font-weight: 700;
                    cursor: pointer;
                    margin-top: 16px;
                    transition: all 0.2s;
                    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
                }
                .checkout-btn:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
                }
                .checkout-btn:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                }
                .continue-shopping-btn {
                    width: 100%;
                    background: #f3f4f6;
                    color: #22c55e;
                    border: none;
                    padding: 12px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    margin-top: 12px;
                    transition: background 0.2s;
                }
                .continue-shopping-btn:hover {
                    background: #e5e7eb;
                }
                .empty-cart {
                    text-align: center;
                    padding: 60px 20px;
                    background: white;
                    border-radius: 12px;
                }
                .empty-cart-icon {
                    font-size: 64px;
                    margin-bottom: 16px;
                }
                .empty-cart-title {
                    font-size: 24px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .empty-cart-text {
                    color: #6b7280;
                    margin-bottom: 24px;
                }
                .notification {
                    position: fixed;
                    top: 90px;
                    right: 20px;
                    padding: 16px 24px;
                    border-radius: 12px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.2);
                    font-weight: 600;
                    z-index: 1000;
                    animation: slideIn 0.3s ease;
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255,255,255,0.3);
                }
                .notification.success { 
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white; 
                }
                .notification.error { 
                    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                    color: white; 
                }
                @keyframes slideIn {
                    from { transform: translateX(400px); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
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
                    z-index: 2000;
                    animation: fadeIn 0.2s ease;
                }
                .modal-content {
                    background: white;
                    padding: 32px;
                    border-radius: 16px;
                    box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    max-width: 400px;
                    width: 90%;
                    animation: modalSlideIn 0.3s ease;
                }
                .modal-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 12px 0;
                }
                .modal-message {
                    font-size: 16px;
                    color: #6b7280;
                    margin: 0 0 24px 0;
                }
                .modal-buttons {
                    display: flex;
                    gap: 12px;
                    justify-content: flex-end;
                }
                .modal-btn {
                    padding: 10px 24px;
                    border-radius: 8px;
                    font-weight: 600;
                    font-size: 14px;
                    border: none;
                    cursor: pointer;
                    transition: all 0.2s;
                }
                .modal-btn-cancel {
                    background: #e5e7eb;
                    color: #374151;
                }
                .modal-btn-cancel:hover {
                    background: #d1d5db;
                }
                .modal-btn-confirm {
                    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                    color: white;
                }
                .modal-btn-confirm:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
                }
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                @keyframes modalSlideIn {
                    from { transform: translateY(-20px); opacity: 0; }
                    to { transform: translateY(0); opacity: 1; }
                }
                @media (max-width: 1024px) {
                    .cart-content {
                        grid-template-columns: 1fr;
                    }
                    .cart-summary {
                        position: relative;
                        top: 0;
                    }
                }
            `}</style>

            <div className="cart-page">
                <CustomerHeader user={user} onLogout={onLogout} cartCount={cart.items.length} />

                <div className="cart-container">
                    <div className="page-header">
                        <h1 className="page-title">🛒 Shopping Cart</h1>
                        {cart.items.length > 0 && (
                            <button className="clear-cart-btn" onClick={handleClearCart}>
                                Clear Cart
                            </button>
                        )}
                    </div>

                    {loading ? (
                        <div style={{ textAlign: 'center', padding: '60px', fontSize: '18px', color: '#6b7280' }}>
                            ⏳ Loading cart...
                        </div>
                    ) : cart.items.length === 0 ? (
                        <div className="empty-cart">
                            <div className="empty-cart-icon">🛒</div>
                            <h2 className="empty-cart-title">Your cart is empty</h2>
                            <p className="empty-cart-text">Add some products to your cart to get started!</p>
                            <button 
                                className="checkout-btn" 
                                onClick={() => navigate('/customer/products')}
                            >
                                Browse Products
                            </button>
                        </div>
                    ) : (
                        <div className="cart-content">
                            <div className="cart-items">
                                {cart.items.map(item => (
                                    <div key={item.cartId} className="cart-item">
                                        <img 
                                            src={item.imageUrl || "/placeholder-product.jpg"}
                                            alt={item.productName}
                                            className="item-image"
                                            onError={(e) => e.target.src = "/placeholder-product.jpg"}
                                        />
                                        <div className="item-details">
                                            <h3 className="item-name">{item.productName}</h3>
                                            <div>
                                                <span className="item-price">
                                                    LKR {item.unitPrice?.toFixed(2)}
                                                </span>
                                                {item.discountPercentage > 0 && (
                                                    <>
                                                        <span className="item-original-price">
                                                            LKR {item.originalPrice?.toFixed(2)}
                                                        </span>
                                                        <span className="item-discount-badge">
                                                            {item.discountPercentage}% OFF
                                                        </span>
                                                    </>
                                                )}
                                            </div>
                                            <div className="item-actions">
                                                <div className="quantity-controls">
                                                    <button 
                                                        className="qty-btn"
                                                        onClick={() => handleUpdateQuantity(item.cartId, item.quantity - 1)}
                                                    >
                                                        −
                                                    </button>
                                                    <input 
                                                        type="number"
                                                        className="qty-input"
                                                        value={item.quantity}
                                                        readOnly
                                                    />
                                                    <button 
                                                        className="qty-btn"
                                                        onClick={() => handleUpdateQuantity(item.cartId, item.quantity + 1)}
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                                <button 
                                                    className="remove-btn"
                                                    onClick={() => handleRemoveItem(item.cartId)}
                                                >
                                                    🗑️ Remove
                                                </button>
                                                <div style={{ marginLeft: 'auto', fontWeight: 600, fontSize: '18px' }}>
                                                    LKR {item.subtotal?.toFixed(2)}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            <div className="cart-summary">
                                <h2 className="summary-title">Order Summary</h2>
                                <div className="summary-row">
                                    <span>Subtotal ({cart.items.length} items)</span>
                                    <span>LKR {cart.subtotal?.toFixed(2)}</span>
                                </div>
                                {cart.totalDiscount > 0 && (
                                    <div className="summary-row savings-text">
                                        <span>Savings</span>
                                        <span>− LKR {cart.totalDiscount?.toFixed(2)}</span>
                                    </div>
                                )}
                                <div className="summary-row total">
                                    <span>Total</span>
                                    <span>LKR {cart.total?.toFixed(2)}</span>
                                </div>
                                <button 
                                    className="checkout-btn"
                                    onClick={() => navigate('/customer/checkout')}
                                >
                                    Proceed to Checkout
                                </button>
                                <button 
                                    className="continue-shopping-btn"
                                    onClick={() => navigate('/customer/products')}
                                >
                                    Continue Shopping
                                </button>
                            </div>
                        </div>
                    )}
                </div>

                {notification && (
                    <div className={`notification ${notification.type}`}>
                        {notification.message}
                    </div>
                )}

                {showConfirmModal && (
                    <div className="modal-overlay" onClick={cancelClearCart}>
                        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                            <h3 className="modal-title">Clear Cart?</h3>
                            <p className="modal-message">Are you sure you want to clear your cart? This action cannot be undone.</p>
                            <div className="modal-buttons">
                                <button className="modal-btn modal-btn-cancel" onClick={cancelClearCart}>
                                    Cancel
                                </button>
                                <button className="modal-btn modal-btn-confirm" onClick={confirmClearCart}>
                                    Clear Cart
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}

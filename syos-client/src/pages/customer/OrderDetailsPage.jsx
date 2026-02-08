import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { apiGetOrderDetails, apiCancelOrder } from "../../services/api";

export default function OrderDetailsPage({ user, onLogout }) {
    const { billNumber } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [cancelling, setCancelling] = useState(false);
    const [notification, setNotification] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadOrderDetails();
    }, [billNumber]);

    const loadOrderDetails = async () => {
        setLoading(true);
        const result = await apiGetOrderDetails(billNumber);
        if (result.success) {
            setOrder(result.data);
        } else {
            showNotification("Failed to load order details", "error");
        }
        setLoading(false);
    };

    const handleCancelOrder = async () => {
        if (!window.confirm("Are you sure you want to cancel this order?")) return;

        setCancelling(true);
        const result = await apiCancelOrder(billNumber);
        
        if (result.success) {
            showNotification("Order cancelled successfully", "success");
            loadOrderDetails(); // Reload to show updated status
        } else {
            showNotification(result.message || "Failed to cancel order", "error");
        }
        setCancelling(false);
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    const getStatusBadgeClass = (status) => {
        const statusClasses = {
            PENDING: 'status-pending',
            PROCESSING: 'status-processing',
            SHIPPED: 'status-shipped',
            DELIVERED: 'status-delivered',
            CANCELLED: 'status-cancelled'
        };
        return statusClasses[status] || 'status-pending';
    };

    const canCancelOrder = order && (order.orderStatus === 'PENDING' || order.orderStatus === 'PROCESSING');

    if (loading) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ textAlign: 'center', padding: '60px', fontSize: '18px', color: '#6b7280' }}>
                    ⏳ Loading order details...
                </div>
            </div>
        );
    }

    if (!order) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ maxWidth: '800px', margin: '0 auto', padding: '60px 20px', textAlign: 'center' }}>
                    <div style={{ fontSize: '64px', marginBottom: '16px' }}>❌</div>
                    <h2 style={{ fontSize: '24px', marginBottom: '16px' }}>Order not found</h2>
                    <button 
                        onClick={() => navigate('/customer/orders')}
                        style={{
                            background: '#22c55e',
                            color: 'white',
                            border: 'none',
                            padding: '12px 24px',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: 'pointer'
                        }}
                    >
                        Back to Orders
                    </button>
                </div>
            </div>
        );
    }

    return (
        <>
            <style>{`
                .order-details-page {
                    min-height: 100vh;
                    background: #f9fafb;
                }
                .order-container {
                    max-width: 1000px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .page-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 24px;
                    flex-wrap: wrap;
                    gap: 16px;
                }
                .back-btn {
                    background: #f3f4f6;
                    color: #4b5563;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .back-btn:hover {
                    background: #e5e7eb;
                }
                .page-title {
                    font-size: 28px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0;
                }
                .order-status-header {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    margin-bottom: 24px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    flex-wrap: wrap;
                    gap: 16px;
                }
                .status-info {
                    flex: 1;
                }
                .order-status-badge {
                    padding: 8px 20px;
                    border-radius: 20px;
                    font-size: 14px;
                    font-weight: 700;
                    display: inline-block;
                    margin-bottom: 12px;
                }
                .status-pending { background: #fef3c7; color: #f59e0b; }
                .status-processing { background: #dbeafe; color: #3b82f6; }
                .status-shipped { background: #e0e7ff; color: #22c55e; }
                .status-delivered { background: #d1fae5; color: #10b981; }
                .status-cancelled { background: #fee2e2; color: #ef4444; }
                .tracking-info {
                    font-size: 20px;
                    font-weight: 700;
                    color: #22c55e;
                    font-family: monospace;
                }
                .cancel-btn {
                    background: #ef4444;
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .cancel-btn:hover {
                    background: #dc2626;
                }
                .cancel-btn:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                }
                .section {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    margin-bottom: 24px;
                }
                .section-title {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 16px 0;
                }
                .info-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 20px;
                }
                .info-item {
                    display: flex;
                    flex-direction: column;
                    gap: 6px;
                }
                .info-label {
                    font-size: 13px;
                    color: #6b7280;
                    font-weight: 600;
                }
                .info-value {
                    font-size: 16px;
                    color: #1f2937;
                    font-weight: 600;
                }
                .order-items {
                    display: flex;
                    flex-direction: column;
                    gap: 16px;
                }
                .order-item {
                    display: flex;
                    gap: 16px;
                    padding: 16px;
                    background: #f9fafb;
                    border-radius: 8px;
                }
                .item-image {
                    width: 80px;
                    height: 80px;
                    object-fit: cover;
                    border-radius: 8px;
                    background: white;
                }
                .item-details {
                    flex: 1;
                }
                .item-name {
                    font-size: 16px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .item-price {
                    font-size: 14px;
                    color: #6b7280;
                }
                .item-total {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1f2937;
                    text-align: right;
                }
                .order-summary {
                    background: #f9fafb;
                    padding: 20px;
                    border-radius: 8px;
                    margin-top: 16px;
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
                    padding-top: 12px;
                    border-top: 2px solid #e5e7eb;
                    margin-top: 12px;
                }
                .notification {
                    position: fixed;
                    top: 80px;
                    right: 20px;
                    padding: 16px 24px;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                    font-weight: 600;
                    z-index: 1000;
                    animation: slideIn 0.3s ease;
                }
                .notification.success { background: #10b981; color: white; }
                .notification.error { background: #ef4444; color: white; }
                @keyframes slideIn {
                    from { transform: translateX(400px); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
            `}</style>

            <div className="order-details-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="order-container">
                    <div className="page-header">
                        <button className="back-btn" onClick={() => navigate('/customer/orders')}>
                            ← Back to Orders
                        </button>
                    </div>

                    <h1 className="page-title">Order #{order.billNumber}</h1>

                    <div className="order-status-header">
                        <div className="status-info">
                            <div className={`order-status-badge ${getStatusBadgeClass(order.orderStatus)}`}>
                                {order.orderStatus}
                            </div>
                            {order.trackingNumber && (
                                <div>
                                    <div style={{ fontSize: '13px', color: '#6b7280', fontWeight: '600', marginBottom: '4px' }}>
                                        Tracking Number
                                    </div>
                                    <div className="tracking-info">{order.trackingNumber}</div>
                                </div>
                            )}
                        </div>
                        {canCancelOrder && (
                            <button 
                                className="cancel-btn"
                                onClick={handleCancelOrder}
                                disabled={cancelling}
                            >
                                {cancelling ? 'Cancelling...' : '❌ Cancel Order'}
                            </button>
                        )}
                    </div>

                    <div className="section">
                        <h2 className="section-title">📋 Order Information</h2>
                        <div className="info-grid">
                            <div className="info-item">
                                <div className="info-label">Order Date</div>
                                <div className="info-value">
                                    {new Date(order.billDate).toLocaleDateString('en-US', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit'
                                    })}
                                </div>
                            </div>
                            <div className="info-item">
                                <div className="info-label">Payment Status</div>
                                <div className="info-value">{order.paymentStatus}</div>
                            </div>
                            {order.estimatedDeliveryDate && (
                                <div className="info-item">
                                    <div className="info-label">Estimated Delivery</div>
                                    <div className="info-value">
                                        {new Date(order.estimatedDeliveryDate).toLocaleDateString('en-US', {
                                            year: 'numeric',
                                            month: 'long',
                                            day: 'numeric'
                                        })}
                                    </div>
                                </div>
                            )}
                            <div className="info-item">
                                <div className="info-label">Order Channel</div>
                                <div className="info-value">{order.channel}</div>
                            </div>
                        </div>
                    </div>

                    <div className="section">
                        <h2 className="section-title">📍 Delivery Address</h2>
                        <div style={{ fontSize: '15px', color: '#4b5563', lineHeight: '1.6' }}>
                            {order.deliveryAddress}<br />
                            {order.deliveryCity}, {order.deliveryPostalCode}<br />
                            📞 {order.deliveryPhone}
                        </div>
                    </div>

                    <div className="section">
                        <h2 className="section-title">📦 Order Items ({order.items?.length || 0})</h2>
                        <div className="order-items">
                            {order.items?.map((item, index) => (
                                <div key={index} className="order-item">
                                    <img 
                                        src={item.imageUrl || "/placeholder-product.jpg"}
                                        alt={item.productName}
                                        className="item-image"
                                        onError={(e) => e.target.src = "/placeholder-product.jpg"}
                                    />
                                    <div className="item-details">
                                        <h3 className="item-name">{item.productName}</h3>
                                        <div className="item-price">
                                            Qty: {item.quantity} × LKR {item.unitPrice?.toFixed(2)}
                                        </div>
                                        {item.discountApplied > 0 && (
                                            <div style={{ fontSize: '13px', color: '#10b981', fontWeight: '600' }}>
                                                Discount: − LKR {item.discountApplied?.toFixed(2)}
                                            </div>
                                        )}
                                    </div>
                                    <div className="item-total">
                                        LKR {item.total?.toFixed(2)}
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="order-summary">
                            <div className="summary-row">
                                <span>Subtotal</span>
                                <span>LKR {order.subtotal?.toFixed(2)}</span>
                            </div>
                            {order.discountAmount > 0 && (
                                <div className="summary-row" style={{ color: '#10b981' }}>
                                    <span>Total Savings</span>
                                    <span>− LKR {order.discountAmount?.toFixed(2)}</span>
                                </div>
                            )}
                            <div className="summary-row total">
                                <span>Total Amount</span>
                                <span>LKR {order.totalAmount?.toFixed(2)}</span>
                            </div>
                            <div className="summary-row">
                                <span>Amount Paid</span>
                                <span>LKR {order.amountPaid?.toFixed(2)}</span>
                            </div>
                            {order.changeAmount > 0 && (
                                <div className="summary-row">
                                    <span>Change</span>
                                    <span>LKR {order.changeAmount?.toFixed(2)}</span>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {notification && (
                    <div className={`notification ${notification.type}`}>
                        {notification.message}
                    </div>
                )}
            </div>
        </>
    );
}

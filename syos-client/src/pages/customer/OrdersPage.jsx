import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { apiGetCustomerOrders } from "../../services/api";

export default function OrdersPage({ user, onLogout }) {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        setLoading(true);
        const result = await apiGetCustomerOrders();
        console.log("📦 Orders API Response:", result);
        if (result.success) {
            // Backend might return orders in result.data.orders or result.data
            const ordersList = result.data?.orders || result.data || [];
            console.log("📦 Orders List:", ordersList);
            setOrders(Array.isArray(ordersList) ? ordersList : []);
        } else {
            console.error("❌ Failed to load orders:", result.message);
            setOrders([]);
        }
        setLoading(false);
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

    const getStatusIcon = (status) => {
        const icons = {
            PENDING: '⏳',
            PROCESSING: '📦',
            SHIPPED: '🚚',
            DELIVERED: '✅',
            CANCELLED: '❌'
        };
        return icons[status] || '📋';
    };

    const filteredOrders = orders;

    return (
        <>
            <style>{`
                .orders-page {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                }
                .orders-container {
                    max-width: 1200px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .page-header {
                    margin-bottom: 24px;
                }
                .page-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 16px 0;
                }
                .orders-list {
                    display: flex;
                    flex-direction: column;
                    gap: 16px;
                }
                .order-card {
                    background: white;
                    border-radius: 12px;
                    padding: 24px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    transition: transform 0.2s, box-shadow 0.2s;
                    cursor: pointer;
                }
                .order-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                }
                .order-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-bottom: 16px;
                    flex-wrap: wrap;
                    gap: 12px;
                }
                .order-number {
                    font-size: 18px;
                    font-weight: 700;
                    color: #1f2937;
                }
                .order-date {
                    font-size: 14px;
                    color: #6b7280;
                    margin-top: 4px;
                }
                .order-status-badge {
                    padding: 6px 16px;
                    border-radius: 20px;
                    font-size: 13px;
                    font-weight: 700;
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                }
                .status-pending {
                    background: #fef3c7;
                    color: #f59e0b;
                }
                .status-processing {
                    background: #dbeafe;
                    color: #3b82f6;
                }
                .status-shipped {
                    background: #e0e7ff;
                    color: #22c55e;
                }
                .status-delivered {
                    background: #d1fae5;
                    color: #10b981;
                }
                .status-cancelled {
                    background: #fee2e2;
                    color: #ef4444;
                }
                .order-details {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 16px;
                    margin-bottom: 16px;
                }
                .detail-item {
                    display: flex;
                    flex-direction: column;
                    gap: 4px;
                }
                .detail-label {
                    font-size: 13px;
                    color: #6b7280;
                    font-weight: 600;
                }
                .detail-value {
                    font-size: 16px;
                    color: #1f2937;
                    font-weight: 600;
                }
                .tracking-number {
                    color: #22c55e;
                    font-family: monospace;
                }
                .order-footer {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding-top: 16px;
                    border-top: 1px solid #e5e7eb;
                }
                .total-amount {
                    font-size: 20px;
                    font-weight: 700;
                    color: #1f2937;
                }
                .view-details-btn {
                    background: #22c55e;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .view-details-btn:hover {
                    background: #5568d3;
                }
                .loading-spinner {
                    text-align: center;
                    padding: 60px 20px;
                    font-size: 18px;
                    color: #6b7280;
                }
                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    background: white;
                    border-radius: 12px;
                }
                .empty-state-icon {
                    font-size: 64px;
                    margin-bottom: 16px;
                }
                .empty-state-title {
                    font-size: 24px;
                    font-weight: 600;
                    color: #1f2937;
                    margin: 0 0 8px 0;
                }
                .empty-state-text {
                    color: #6b7280;
                    margin-bottom: 24px;
                }
                .start-shopping-btn {
                    background: #22c55e;
                    color: white;
                    border: none;
                    padding: 12px 24px;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                }
                .start-shopping-btn:hover {
                    background: #5568d3;
                }
            `}</style>

            <div className="orders-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="orders-container">
                    <div className="page-header">
                        <h1 className="page-title">📦 My Orders</h1>
                    </div>

                    {loading ? (
                        <div className="loading-spinner">
                            ⏳ Loading orders...
                        </div>
                    ) : filteredOrders.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">📦</div>
                            <h2 className="empty-state-title">
                                {filterStatus === "ALL" ? "No orders yet" : `No ${filterStatus.toLowerCase()} orders`}
                            </h2>
                            <p className="empty-state-text">
                                {filterStatus === "ALL" 
                                    ? "Start shopping to see your orders here!" 
                                    : "Try selecting a different filter"}
                            </p>
                            {filterStatus === "ALL" && (
                                <button 
                                    className="start-shopping-btn"
                                    onClick={() => navigate('/customer/products')}
                                >
                                    Start Shopping
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className="orders-list">
                            {filteredOrders.map(order => (
                                <div 
                                    key={order.billNumber}
                                    className="order-card"
                                    onClick={() => navigate(`/customer/orders/${order.billNumber}`)}
                                >
                                    <div className="order-header">
                                        <div>
                                            <div className="order-number">
                                                Order #{order.billNumber}
                                            </div>
                                            <div className="order-date">
                                                {new Date(order.billDate).toLocaleDateString('en-US', {
                                                    year: 'numeric',
                                                    month: 'long',
                                                    day: 'numeric',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                })}
                                            </div>
                                        </div>
                                        <div className={`order-status-badge ${getStatusBadgeClass('DELIVERED')}`}>
                                            {getStatusIcon('DELIVERED')} DELIVERED
                                        </div>
                                    </div>

                                    <div className="order-details">
                                        <div className="detail-item">
                                            <div className="detail-label">Tracking Number</div>
                                            <div className="detail-value tracking-number">
                                                {order.trackingNumber || 'N/A'}
                                            </div>
                                        </div>
                                        <div className="detail-item">
                                            <div className="detail-label">Payment Status</div>
                                            <div className="detail-value" style={{ color: '#10b981', fontWeight: '600' }}>
                                                ✓ SUCCESS
                                            </div>
                                        </div>
                                        {order.estimatedDeliveryDate && (
                                            <div className="detail-item">
                                                <div className="detail-label">Estimated Delivery</div>
                                                <div className="detail-value">
                                                    {new Date(order.estimatedDeliveryDate).toLocaleDateString('en-US', {
                                                        month: 'short',
                                                        day: 'numeric',
                                                        year: 'numeric'
                                                    })}
                                                </div>
                                            </div>
                                        )}
                                    </div>

                                    <div className="order-footer">
                                        <div className="total-amount">
                                            Total: LKR {order.totalAmount?.toFixed(2)}
                                        </div>
                                        <button 
                                            className="view-details-btn"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                navigate(`/customer/orders/${order.billNumber}`);
                                            }}
                                        >
                                            View Details →
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

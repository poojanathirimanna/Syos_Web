import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomerHeader from "../../components/customer/CustomerHeader";
import { apiGetCart, apiCheckout, apiGetAddresses } from "../../services/api";

export default function CheckoutPage({ user, onLogout }) {
    const [step, setStep] = useState(1); // 1: Address, 2: Payment, 3: Review
    const [cart, setCart] = useState({ items: [], total: 0 });
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [savedAddresses, setSavedAddresses] = useState([]);
    const [notification, setNotification] = useState(null);
    
    const [formData, setFormData] = useState({
        deliveryAddress: "",
        deliveryCity: "",
        deliveryPostalCode: "",
        deliveryPhone: "",
        paymentMethod: "CASH_ON_DELIVERY",
        paymentMethodDetails: ""
    });

    const navigate = useNavigate();

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        setLoading(true);
        const cartResult = await apiGetCart();
        const addressResult = await apiGetAddresses();
        
        if (cartResult.success && cartResult.data) {
            // Backend returns: { items: [], summary: { subtotal, totalDiscount, totalAmount } }
            const cartData = {
                items: cartResult.data.items || [],
                subtotal: cartResult.data.summary?.subtotal || 0,
                totalDiscount: cartResult.data.summary?.totalDiscount || 0,
                total: cartResult.data.summary?.totalAmount || 0
            };
            setCart(cartData);
        }
        
        if (addressResult.success && addressResult.data?.length > 0) {
            setSavedAddresses(addressResult.data);
            // Pre-fill with default address if available
            const defaultAddr = addressResult.data.find(a => a.isDefault);
            if (defaultAddr) {
                setFormData(prev => ({
                    ...prev,
                    deliveryAddress: defaultAddr.addressLine1 + (defaultAddr.addressLine2 ? ', ' + defaultAddr.addressLine2 : ''),
                    deliveryCity: defaultAddr.city,
                    deliveryPostalCode: defaultAddr.postalCode,
                    deliveryPhone: defaultAddr.phoneNumber
                }));
            }
        }
        
        setLoading(false);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleAddressSelect = (address) => {
        setFormData(prev => ({
            ...prev,
            deliveryAddress: address.addressLine1 + (address.addressLine2 ? ', ' + address.addressLine2 : ''),
            deliveryCity: address.city,
            deliveryPostalCode: address.postalCode,
            deliveryPhone: address.phoneNumber
        }));
    };

    const showNotification = (message, type = "info") => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 5000);
    };

    const validateStep = () => {
        if (step === 1) {
            if (!formData.deliveryAddress || !formData.deliveryCity || 
                !formData.deliveryPostalCode || !formData.deliveryPhone) {
                showNotification("Please fill in all delivery details", "error");
                return false;
            }
        }
        return true;
    };

    const handleNext = () => {
        if (validateStep()) {
            setStep(step + 1);
        }
    };

    const handleBack = () => {
        setStep(step - 1);
    };

    const handleSubmit = async () => {
        setSubmitting(true);
        
        const orderData = {
            items: cart.items.map(item => ({
                productCode: item.productCode,
                quantity: item.quantity
            })),
            paymentMethod: formData.paymentMethod,
            deliveryAddress: formData.deliveryAddress,
            deliveryCity: formData.deliveryCity,
            deliveryPostalCode: formData.deliveryPostalCode,
            deliveryPhone: formData.deliveryPhone,
            paymentMethodDetails: formData.paymentMethodDetails ? 
                JSON.stringify({ note: formData.paymentMethodDetails }) : ""
        };

        const result = await apiCheckout(orderData);
        
        if (result.success) {
            const trackingNumber = result.data?.trackingNumber;
            const billNumber = result.data?.billNumber;
            showNotification(`✅ Order placed successfully! Order #: ${billNumber}, Tracking #: ${trackingNumber}`, "success");
            setTimeout(() => navigate('/customer/orders'), 2000);
        } else {
            showNotification(result.message || "Failed to place order. Please try again.", "error");
        }
        
        setSubmitting(false);
    };

    if (loading) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ textAlign: 'center', padding: '60px', fontSize: '18px', color: '#6b7280' }}>
                    ⏳ Loading...
                </div>
            </div>
        );
    }

    if (cart.items.length === 0) {
        return (
            <div style={{ minHeight: '100vh', background: '#f9fafb' }}>
                <CustomerHeader user={user} onLogout={onLogout} />
                <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '60px 20px', textAlign: 'center' }}>
                    <div style={{ fontSize: '64px', marginBottom: '16px' }}>🛒</div>
                    <h2 style={{ fontSize: '24px', marginBottom: '16px' }}>Your cart is empty</h2>
                    <button 
                        onClick={() => navigate('/customer/products')}
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
                        Browse Products
                    </button>
                </div>
            </div>
        );
    }

    return (
        <>
            <style>{`
                .checkout-page {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                }
                .checkout-container {
                    max-width: 900px;
                    margin: 0 auto;
                    padding: 24px 20px;
                }
                .page-title {
                    font-size: 32px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 24px 0;
                }
                .progress-steps {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 40px;
                    position: relative;
                }
                .progress-steps::before {
                    content: '';
                    position: absolute;
                    top: 20px;
                    left: 0;
                    right: 0;
                    height: 2px;
                    background: #e5e7eb;
                    z-index: 0;
                }
                .step {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    position: relative;
                    z-index: 1;
                }
                .step-circle {
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    background: #e5e7eb;
                    color: #9ca3af;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: 700;
                    margin-bottom: 8px;
                }
                .step.active .step-circle {
                    background: #22c55e;
                    color: white;
                }
                .step.completed .step-circle {
                    background: #10b981;
                    color: white;
                }
                .step-label {
                    font-size: 14px;
                    font-weight: 600;
                    color: #9ca3af;
                }
                .step.active .step-label, .step.completed .step-label {
                    color: #1f2937;
                }
                .checkout-content {
                    background: white;
                    border-radius: 12px;
                    padding: 32px;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    margin-bottom: 24px;
                }
                .section-title {
                    font-size: 20px;
                    font-weight: 700;
                    color: #1f2937;
                    margin: 0 0 20px 0;
                }
                .form-group {
                    margin-bottom: 20px;
                }
                .form-label {
                    display: block;
                    font-weight: 600;
                    color: #374151;
                    margin-bottom: 8px;
                }
                .form-input, .form-select {
                    width: 100%;
                    padding: 12px 16px;
                    border: 2px solid #e5e7eb;
                    border-radius: 8px;
                    font-size: 15px;
                    transition: border-color 0.2s;
                }
                .form-input:focus, .form-select:focus {
                    outline: none;
                    border-color: #22c55e;
                }
                .saved-addresses {
                    display: grid;
                    gap: 12px;
                    margin-bottom: 20px;
                }
                .address-card {
                    border: 2px solid #e5e7eb;
                    padding: 16px;
                    border-radius: 8px;
                    cursor: pointer;
                    transition: all 0.2s;
                }
                .address-card:hover {
                    border-color: #22c55e;
                }
                .address-card.selected {
                    border-color: #22c55e;
                    background: #f0f4ff;
                }
                .payment-methods {
                    display: grid;
                    gap: 12px;
                }
                .payment-option {
                    border: 2px solid #e5e7eb;
                    padding: 16px;
                    border-radius: 8px;
                    cursor: pointer;
                    transition: all 0.2s;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }
                .payment-option:hover {
                    border-color: #22c55e;
                }
                .payment-option.selected {
                    border-color: #22c55e;
                    background: #f0f4ff;
                }
                .order-summary {
                    background: #f9fafb;
                    padding: 20px;
                    border-radius: 8px;
                    margin-top: 24px;
                }
                .summary-row {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 12px;
                }
                .summary-row.total {
                    font-size: 20px;
                    font-weight: 700;
                    padding-top: 12px;
                    border-top: 2px solid #e5e7eb;
                    margin-top: 12px;
                }
                .checkout-actions {
                    display: flex;
                    gap: 12px;
                    justify-content: space-between;
                }
                .btn {
                    padding: 14px 32px;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 700;
                    cursor: pointer;
                    border: none;
                    transition: all 0.2s;
                }
                .btn-primary {
                    background: #22c55e;
                    color: white;
                    flex: 1;
                }
                .btn-primary:hover {
                    background: #5568d3;
                }
                .btn-secondary {
                    background: #f3f4f6;
                    color: #4b5563;
                }
                .btn-secondary:hover {
                    background: #e5e7eb;
                }
                .btn-success {
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white;
                    flex: 1;
                    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
                }
                .btn-success:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
                }
                .btn:disabled {
                    background: #d1d5db;
                    cursor: not-allowed;
                }
                .notification {
                    position: fixed;
                    bottom: 24px;
                    right: 24px;
                    padding: 16px 24px;
                    border-radius: 12px;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
                    font-weight: 500;
                    z-index: 1000;
                    animation: slideIn 0.3s ease-out;
                    max-width: 400px;
                }
                .notification.success {
                    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                    color: white;
                }
                .notification.error {
                    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                    color: white;
                }
                .notification.info {
                    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
                    color: white;
                }
                @keyframes slideIn {
                    from {
                        transform: translateX(400px);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
                .review-section {
                    margin-bottom: 24px;
                }
                .review-items {
                    max-height: 300px;
                    overflow-y: auto;
                }
                .review-item {
                    display: flex;
                    justify-content: space-between;
                    padding: 12px 0;
                    border-bottom: 1px solid #e5e7eb;
                }
            `}</style>

            <div className="checkout-page">
                <CustomerHeader user={user} onLogout={onLogout} />

                <div className="checkout-container">
                    <h1 className="page-title">💳 Checkout</h1>

                    <div className="progress-steps">
                        <div className={`step ${step >= 1 ? 'active' : ''} ${step > 1 ? 'completed' : ''}`}>
                            <div className="step-circle">1</div>
                            <div className="step-label">Delivery</div>
                        </div>
                        <div className={`step ${step >= 2 ? 'active' : ''} ${step > 2 ? 'completed' : ''}`}>
                            <div className="step-circle">2</div>
                            <div className="step-label">Payment</div>
                        </div>
                        <div className={`step ${step >= 3 ? 'active' : ''}`}>
                            <div className="step-circle">3</div>
                            <div className="step-label">Review</div>
                        </div>
                    </div>

                    <div className="checkout-content">
                        {/* STEP 1: DELIVERY ADDRESS */}
                        {step === 1 && (
                            <>
                                <h2 className="section-title">📍 Delivery Address</h2>
                                
                                {savedAddresses.length > 0 && (
                                    <>
                                        <div style={{ marginBottom: '20px', fontSize: '14px', color: '#6b7280' }}>
                                            Select a saved address or enter a new one
                                        </div>
                                        <div className="saved-addresses">
                                            {savedAddresses.map(addr => (
                                                <div 
                                                    key={addr.addressId}
                                                    className="address-card"
                                                    onClick={() => handleAddressSelect(addr)}
                                                >
                                                    <div style={{ fontWeight: '600', marginBottom: '4px' }}>
                                                        {addr.addressLine1}
                                                    </div>
                                                    {addr.addressLine2 && (
                                                        <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                            {addr.addressLine2}
                                                        </div>
                                                    )}
                                                    <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                        {addr.city}, {addr.postalCode}
                                                    </div>
                                                    <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                        📞 {addr.phoneNumber}
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                        <div style={{ margin: '20px 0', textAlign: 'center', color: '#9ca3af' }}>
                                            OR
                                        </div>
                                    </>
                                )}

                                <div className="form-group">
                                    <label className="form-label">Street Address *</label>
                                    <input 
                                        type="text"
                                        name="deliveryAddress"
                                        className="form-input"
                                        value={formData.deliveryAddress}
                                        onChange={handleInputChange}
                                        placeholder="123 Main Street, Apartment 4B"
                                    />
                                </div>

                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                    <div className="form-group">
                                        <label className="form-label">City *</label>
                                        <input 
                                            type="text"
                                            name="deliveryCity"
                                            className="form-input"
                                            value={formData.deliveryCity}
                                            onChange={handleInputChange}
                                            placeholder="Colombo"
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label className="form-label">Postal Code *</label>
                                        <input 
                                            type="text"
                                            name="deliveryPostalCode"
                                            className="form-input"
                                            value={formData.deliveryPostalCode}
                                            onChange={handleInputChange}
                                            placeholder="10100"
                                        />
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Phone Number *</label>
                                    <input 
                                        type="tel"
                                        name="deliveryPhone"
                                        className="form-input"
                                        value={formData.deliveryPhone}
                                        onChange={handleInputChange}
                                        placeholder="0771234567"
                                    />
                                </div>
                            </>
                        )}

                        {/* STEP 2: PAYMENT METHOD */}
                        {step === 2 && (
                            <>
                                <h2 className="section-title">💳 Payment Method</h2>
                                
                                <div className="payment-methods">
                                    <div 
                                        className={`payment-option ${formData.paymentMethod === 'CASH_ON_DELIVERY' ? 'selected' : ''}`}
                                        onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'CASH_ON_DELIVERY' }))}
                                    >
                                        <div style={{ fontSize: '24px' }}>💵</div>
                                        <div>
                                            <div style={{ fontWeight: '600' }}>Cash on Delivery</div>
                                            <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                Pay when you receive your order
                                            </div>
                                        </div>
                                    </div>

                                    <div 
                                        className={`payment-option ${formData.paymentMethod === 'CREDIT_CARD' ? 'selected' : ''}`}
                                        onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'CREDIT_CARD' }))}
                                    >
                                        <div style={{ fontSize: '24px' }}>💳</div>
                                        <div>
                                            <div style={{ fontWeight: '600' }}>Credit/Debit Card</div>
                                            <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                Pay securely with your card
                                            </div>
                                        </div>
                                    </div>

                                    <div 
                                        className={`payment-option ${formData.paymentMethod === 'BANK_TRANSFER' ? 'selected' : ''}`}
                                        onClick={() => setFormData(prev => ({ ...prev, paymentMethod: 'BANK_TRANSFER' }))}
                                    >
                                        <div style={{ fontSize: '24px' }}>🏦</div>
                                        <div>
                                            <div style={{ fontWeight: '600' }}>Bank Transfer</div>
                                            <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                Transfer payment directly to our bank account
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group" style={{ marginTop: '20px' }}>
                                    <label className="form-label">Additional Notes (Optional)</label>
                                    <textarea 
                                        name="paymentMethodDetails"
                                        className="form-input"
                                        value={formData.paymentMethodDetails}
                                        onChange={handleInputChange}
                                        rows="3"
                                        placeholder="Any special instructions for your order..."
                                    />
                                </div>
                            </>
                        )}

                        {/* STEP 3: REVIEW ORDER */}
                        {step === 3 && (
                            <>
                                <h2 className="section-title">📋 Review Order</h2>
                                
                                <div className="review-section">
                                    <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '12px' }}>
                                        📦 Order Items ({cart.items.length})
                                    </h3>
                                    <div className="review-items">
                                        {cart.items.map(item => (
                                            <div key={item.cartId} className="review-item">
                                                <div>
                                                    <div style={{ fontWeight: '600' }}>{item.productName}</div>
                                                    <div style={{ fontSize: '14px', color: '#6b7280' }}>
                                                        Qty: {item.quantity} × LKR {item.unitPrice?.toFixed(2)}
                                                    </div>
                                                </div>
                                                <div style={{ fontWeight: '600' }}>
                                                    LKR {item.subtotal?.toFixed(2)}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="review-section">
                                    <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '12px' }}>
                                        📍 Delivery Address
                                    </h3>
                                    <div style={{ fontSize: '14px', color: '#4b5563' }}>
                                        {formData.deliveryAddress}<br />
                                        {formData.deliveryCity}, {formData.deliveryPostalCode}<br />
                                        📞 {formData.deliveryPhone}
                                    </div>
                                </div>

                                <div className="review-section">
                                    <h3 style={{ fontSize: '16px', fontWeight: '600', marginBottom: '12px' }}>
                                        💳 Payment Method
                                    </h3>
                                    <div style={{ fontSize: '14px', color: '#4b5563' }}>
                                        {formData.paymentMethod.replace(/_/g, ' ')}
                                    </div>
                                </div>

                                <div className="order-summary">
                                    <div className="summary-row">
                                        <span>Subtotal</span>
                                        <span>LKR {cart.subtotal?.toFixed(2)}</span>
                                    </div>
                                    {cart.totalDiscount > 0 && (
                                        <div className="summary-row" style={{ color: '#10b981' }}>
                                            <span>Savings</span>
                                            <span>− LKR {cart.totalDiscount?.toFixed(2)}</span>
                                        </div>
                                    )}
                                    <div className="summary-row total">
                                        <span>Total</span>
                                        <span>LKR {cart.total?.toFixed(2)}</span>
                                    </div>
                                </div>
                            </>
                        )}
                    </div>

                    <div className="checkout-actions">
                        {step > 1 && (
                            <button className="btn btn-secondary" onClick={handleBack}>
                                ← Back
                            </button>
                        )}
                        
                        {step < 3 ? (
                            <button className="btn btn-primary" onClick={handleNext}>
                                Next →
                            </button>
                        ) : (
                            <button 
                                className="btn btn-success" 
                                onClick={handleSubmit}
                                disabled={submitting}
                            >
                                {submitting ? '⏳ Placing Order...' : '✓ Place Order'}
                            </button>
                        )}
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

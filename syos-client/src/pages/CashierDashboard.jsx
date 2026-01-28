import { useState, useEffect } from "react";
import Sidebar from "../components/common/Sidebar";
import Header from "../components/common/Header";
import { apiGetProducts, apiCreateBill, apiGetCashierBills, apiGetCategories } from "../services/api";
import syosLogo from "../assets/syos-logo-text.png";

export default function CashierDashboard({ user, onLogout }) {
    const [activeMenu, setActiveMenu] = useState("pos");
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [cart, setCart] = useState([]);
    const [showCheckoutModal, setShowCheckoutModal] = useState(false);
    const [bills, setBills] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("all");
    const [categories, setCategories] = useState([]);

    const menuItems = [
        { id: "pos", icon: "🛒", label: "Point of Sale" },
        { id: "transactions", icon: "💰", label: "Transactions" },
        { id: "customers", icon: "👥", label: "Customers" },
        { id: "rewards", icon: "🎁", label: "Rewards" },
        { id: "reports", icon: "📊", label: "Reports" },
    ];

    useEffect(() => {
        loadProducts();
        loadCategories();
    }, []);

    useEffect(() => {
        if (activeMenu === "transactions") {
            loadBills();
        }
    }, [activeMenu]);

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

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await apiGetProducts();
            console.log('🛒 POS: API Response:', response);
            if (response.success) {
                // Products are nested inside data.products
                const productsData = response.data?.products || response.data || [];
                console.log('🛒 POS: Products data:', productsData);
                const productsArray = Array.isArray(productsData) ? productsData : [];
                console.log('🛒 POS: Setting products array:', productsArray);
                setProducts(productsArray);
            } else {
                setError(response.message || "Failed to load products");
                setProducts([]);
            }
        } catch (err) {
            setError("Error loading products: " + err.message);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const loadBills = async () => {
        try {
            const response = await apiGetCashierBills();
            if (response.success) {
                // Ensure data is an array
                const billsData = Array.isArray(response.data) ? response.data : [];
                setBills(billsData);
            } else {
                setBills([]);
            }
        } catch (err) {
            console.error("Error loading bills:", err);
            setBills([]);
        }
    };

    const addToCart = (product) => {
        const existingItem = cart.find(item => item.productCode === product.productCode);
        if (existingItem) {
            setCart(cart.map(item =>
                item.productCode === product.productCode
                    ? { ...item, quantity: item.quantity + 1 }
                    : item
            ));
        } else {
            setCart([...cart, { ...product, quantity: 1 }]);
        }
    };

    const updateQuantity = (productCode, newQuantity) => {
        if (newQuantity <= 0) {
            setCart(cart.filter(item => item.productCode !== productCode));
        } else {
            setCart(cart.map(item =>
                item.productCode === productCode
                    ? { ...item, quantity: newQuantity }
                    : item
            ));
        }
    };

    const removeFromCart = (productCode) => {
        setCart(cart.filter(item => item.productCode !== productCode));
    };

    const getCartTotal = () => {
        return cart.reduce((total, item) => total + (item.unitPrice * item.quantity), 0);
    };

    const handleCheckout = () => {
        if (cart.length === 0) return;
        setShowCheckoutModal(true);
    };

    const clearCart = () => {
        setCart([]);
    };

    const filteredProducts = Array.isArray(products) 
        ? products.filter(product => {
            const matchesSearch = product.name?.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesCategory = selectedCategory === "all" || product.categoryId === selectedCategory;
            return matchesSearch && matchesCategory;
        })
        : [];

    console.log('🛒 POS: products state:', products);
    console.log('🛒 POS: filteredProducts:', filteredProducts);
    console.log('🛒 POS: searchQuery:', searchQuery);

    return (
        <>
            <style>{`
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                .dashboard-container {
                    display: flex;
                    min-height: 100vh;
                    background: #f5f5f5;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                }
                
                .main-content {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                }
                
                .content-area {
                    flex: 1;
                    padding: 32px;
                    overflow-y: auto;
                }
                
                .section-title {
                    font-size: 24px;
                    color: #333;
                    font-weight: 700;
                    margin-bottom: 24px;
                }
                
                .quick-stats {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                    gap: 20px;
                    margin-bottom: 32px;
                }
                
                .stat-card {
                    background: white;
                    padding: 24px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    border-left: 4px solid #66bb6a;
                }
                
                .stat-label {
                    color: #888;
                    font-size: 13px;
                    margin-bottom: 8px;
                    text-transform: uppercase;
                    font-weight: 600;
                }
                
                .stat-value {
                    font-size: 32px;
                    font-weight: 700;
                    color: #333;
                }
                
                .pos-section {
                    background: white;
                    padding: 32px;
                    border-radius: 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                }
                
                .pos-grid {
                    display: grid;
                    grid-template-columns: 2fr 1fr;
                    gap: 24px;
                }
                
                .products-section {
                    background: #f9f9f9;
                    padding: 24px;
                    border-radius: 8px;
                }
                
                .cart-section {
                    background: #f9f9f9;
                    padding: 24px;
                    border-radius: 8px;
                }
                
                .section-subtitle {
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 16px;
                }
                
                .product-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
                    gap: 12px;
                }
                
                .product-card {
                    background: white;
                    padding: 16px;
                    border-radius: 8px;
                    text-align: center;
                    cursor: pointer;
                    transition: all 0.2s ease;
                    border: 2px solid #e0e0e0;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                    display: flex;
                    flex-direction: column;
                }
                
                .product-card:hover {
                    border-color: #52B788;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 8px rgba(82, 183, 136, 0.2);
                }
                
                .product-image {
                    width: 100%;
                    height: 120px;
                    object-fit: cover;
                    border-radius: 8px;
                    margin-bottom: 8px;
                    background: #f5f5f5;
                }

                .product-icon {
                    font-size: 48px;
                    margin-bottom: 8px;
                    height: 120px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: #f5f5f5;
                    border-radius: 8px;
                }
                
                .product-name {
                    font-size: 14px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 4px;
                    min-height: 40px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                
                .product-price {
                    font-size: 16px;
                    color: #52B788;
                    font-weight: 700;
                }

                .product-stock {
                    font-size: 11px;
                    color: #888;
                    margin-top: 4px;
                }

                .category-filters {
                    display: flex;
                    gap: 8px;
                    margin-bottom: 16px;
                    flex-wrap: wrap;
                }

                .category-btn {
                    padding: 8px 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 20px;
                    cursor: pointer;
                    font-size: 13px;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .category-btn:hover {
                    border-color: #52B788;
                    color: #52B788;
                }

                .category-btn.active {
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border-color: #52B788;
                }

                .search-box-pos {
                    margin-bottom: 20px;
                }

                .search-input-pos {
                    width: 100%;
                    max-width: 400px;
                    padding: 12px 16px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 14px;
                }

                .search-input-pos:focus {
                    outline: none;
                    border-color: #52B788;
                }

                .cart-items {
                    max-height: 400px;
                    overflow-y: auto;
                    margin-bottom: 20px;
                }

                .cart-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 12px;
                    background: white;
                    border-radius: 8px;
                    margin-bottom: 8px;
                }

                .cart-item-info {
                    flex: 1;
                }

                .cart-item-name {
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 4px;
                }

                .cart-item-price {
                    color: #52B788;
                    font-size: 14px;
                }

                .cart-item-controls {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .qty-btn {
                    width: 28px;
                    height: 28px;
                    border: 2px solid #52B788;
                    background: white;
                    color: #52B788;
                    border-radius: 4px;
                    cursor: pointer;
                    font-weight: 700;
                    transition: all 0.2s;
                }

                .qty-btn:hover {
                    background: #52B788;
                    color: white;
                }

                .qty-display {
                    min-width: 30px;
                    text-align: center;
                    font-weight: 600;
                }

                .remove-btn {
                    background: #f44336;
                    color: white;
                    border: none;
                    padding: 6px 10px;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 12px;
                }

                .cart-total {
                    padding: 16px;
                    background: white;
                    border-radius: 8px;
                    margin-bottom: 12px;
                }

                .cart-total-label {
                    font-size: 14px;
                    color: #666;
                    margin-bottom: 8px;
                }

                .cart-total-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #52B788;
                }
                
                .cart-empty {
                    text-align: center;
                    padding: 40px;
                    color: #999;
                }
                
                .checkout-button {
                    width: 100%;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    padding: 16px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 16px;
                    cursor: pointer;
                    transition: all 0.3s;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }
                
                .checkout-button:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .checkout-button:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .clear-cart-btn {
                    width: 100%;
                    background: white;
                    color: #f44336;
                    border: 2px solid #f44336;
                    padding: 12px;
                    border-radius: 8px;
                    font-weight: 600;
                    font-size: 14px;
                    cursor: pointer;
                    margin-top: 8px;
                    transition: all 0.2s;
                }

                .clear-cart-btn:hover {
                    background: #f44336;
                    color: white;
                }
                
                .logout-button {
                    background: #f44336;
                    color: white;
                    border: none;
                    padding: 8px 16px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-weight: 600;
                    font-size: 14px;
                    transition: background 0.2s ease;
                }
                
                .logout-button:hover {
                    background: #d32f2f;
                }
                
                .transactions-table {
                    background: white;
                    border-radius: 8px;
                    overflow: hidden;
                }
                
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                
                th {
                    background: #f5f5f5;
                    padding: 16px;
                    text-align: left;
                    font-weight: 600;
                    color: #666;
                    font-size: 13px;
                    text-transform: uppercase;
                }
                
                td {
                    padding: 16px;
                    border-top: 1px solid #e0e0e0;
                    color: #333;
                }
                
                .badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: 600;
                }
                
                .badge.success {
                    background: #e8f5e9;
                    color: #4caf50;
                }
                
                .badge.pending {
                    background: #fff3e0;
                    color: #ff9800;
                }
            `}</style>

            <div className="dashboard-container">
                {/* Sidebar */}
                <Sidebar
                    logo={syosLogo}
                    menuItems={menuItems}
                    activeMenu={activeMenu}
                    onMenuClick={setActiveMenu}
                    isOpen={sidebarOpen}
                    accentColor="#66bb6a"
                />

                {/* Main Content */}
                <main className="main-content">
                    <Header
                        user={user}
                        onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
                        onLogout={onLogout}
                        showNotifications={false}
                    />

                    {/* Content Area */}
                    <div className="content-area">
                        {activeMenu === "pos" && (
                            <>
                                <h1 className="section-title">Point of Sale</h1>

                                {error && (
                                    <div style={{
                                        padding: '16px',
                                        marginBottom: '20px',
                                        background: 'linear-gradient(135deg, #fee2e2 0%, #fecaca 100%)',
                                        color: '#991b1b',
                                        borderRadius: '12px',
                                        fontWeight: 600
                                    }}>
                                        ⚠️ {error}
                                    </div>
                                )}

                                {loading ? (
                                    <div style={{ textAlign: 'center', padding: '60px', color: '#888' }}>
                                        <div style={{ fontSize: '24px', marginBottom: '12px' }}>⏳</div>
                                        Loading products...
                                    </div>
                                ) : (
                                    <>
                                        {/* POS Interface */}
                                        <div className="pos-section">
                                            <div className="pos-grid">
                                                <div className="products-section">
                                                    <h3 className="section-subtitle">Products</h3>
                                                    <div className="search-box-pos">
                                                        <input
                                                            type="text"
                                                            className="search-input-pos"
                                                            placeholder="🔍 Search products..."
                                                            value={searchQuery}
                                                            onChange={(e) => setSearchQuery(e.target.value)}
                                                        />
                                                    </div>
                                                    <div className="category-filters">
                                                        <button
                                                            className={`category-btn ${selectedCategory === "all" ? "active" : ""}`}
                                                            onClick={() => setSelectedCategory("all")}
                                                        >
                                                            All Products
                                                        </button>
                                                        {categories.map(category => (
                                                            <button
                                                                key={category.categoryId}
                                                                className={`category-btn ${selectedCategory === category.categoryId ? "active" : ""}`}
                                                                onClick={() => setSelectedCategory(category.categoryId)}
                                                            >
                                                                {category.categoryName}
                                                            </button>
                                                        ))}
                                                    </div>
                                                    <div className="product-grid">
                                                        {filteredProducts.map(product => (
                                                            <div
                                                                key={product.productCode}
                                                                className="product-card"
                                                                onClick={() => addToCart(product)}
                                                            >
                                                                {product.imageUrl ? (
                                                                    <img 
                                                                        src={product.imageUrl} 
                                                                        alt={product.name}
                                                                        className="product-image"
                                                                        onError={(e) => {
                                                                            e.target.style.display = 'none';
                                                                            e.target.nextSibling.style.display = 'flex';
                                                                        }}
                                                                    />
                                                                ) : null}
                                                                <div className="product-icon" style={{ display: product.imageUrl ? 'none' : 'flex' }}>📦</div>
                                                                <div className="product-name">{product.name}</div>
                                                                <div className="product-price">₱{Number(product.unitPrice).toFixed(2)}</div>
                                                                <div className="product-stock">Stock: {product.totalQuantity || 0}</div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                    {filteredProducts.length === 0 && (
                                                        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
                                                            No products found
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="cart-section">
                                                    <h3 className="section-subtitle">Current Order</h3>
                                                    {cart.length === 0 ? (
                                                        <div className="cart-empty">
                                                            <p>🛒</p>
                                                            <p>No items in cart</p>
                                                        </div>
                                                    ) : (
                                                        <>
                                                            <div className="cart-items">
                                                                {cart.map(item => (
                                                                    <div key={item.productCode} className="cart-item">
                                                                        <div className="cart-item-info">
                                                                            <div className="cart-item-name">{item.name}</div>
                                                                            <div className="cart-item-price">₱{Number(item.unitPrice).toFixed(2)} each</div>
                                                                        </div>
                                                                        <div className="cart-item-controls">
                                                                            <button
                                                                                className="qty-btn"
                                                                                onClick={() => updateQuantity(item.productCode, item.quantity - 1)}
                                                                            >
                                                                                -
                                                                            </button>
                                                                            <span className="qty-display">{item.quantity}</span>
                                                                            <button
                                                                                className="qty-btn"
                                                                                onClick={() => updateQuantity(item.productCode, item.quantity + 1)}
                                                                            >
                                                                                +
                                                                            </button>
                                                                            <button
                                                                                className="remove-btn"
                                                                                onClick={() => removeFromCart(item.productCode)}
                                                                            >
                                                                                🗑️
                                                                            </button>
                                                                        </div>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                            <div className="cart-total">
                                                                <div className="cart-total-label">Total Amount:</div>
                                                                <div className="cart-total-value">₱{getCartTotal().toFixed(2)}</div>
                                                            </div>
                                                            <button className="checkout-button" onClick={handleCheckout}>
                                                                💳 Checkout
                                                            </button>
                                                            <button className="clear-cart-btn" onClick={clearCart}>
                                                                Clear Cart
                                                            </button>
                                                        </>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    </>
                                )}
                            </>
                        )}

                        {activeMenu === "transactions" && (
                            <>
                                <h1 className="section-title">My Transactions</h1>
                                <div className="transactions-table">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Bill Number</th>
                                                <th>Date & Time</th>
                                                <th>Items</th>
                                                <th>Total</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {bills.length > 0 ? (
                                                bills.map(bill => (
                                                    <tr key={bill.billNumber}>
                                                        <td>{bill.billNumber}</td>
                                                        <td>{bill.createdAt || bill.billDate || 'N/A'}</td>
                                                        <td>{bill.itemCount || bill.items?.length || 0}</td>
                                                        <td>₱{Number(bill.totalAmount || 0).toFixed(2)}</td>
                                                        <td>
                                                            <span className="badge success">
                                                                {bill.status || 'Completed'}
                                                            </span>
                                                        </td>
                                                    </tr>
                                                ))
                                            ) : (
                                                <tr>
                                                    <td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: '#888' }}>
                                                        No transactions yet
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </div>
                            </>
                        )}

                        {activeMenu !== "pos" && activeMenu !== "transactions" && (
                            <div>
                                <h1 className="section-title">{menuItems.find(m => m.id === activeMenu)?.label}</h1>
                                <p style={{color: '#666'}}>Content for {activeMenu} section coming soon...</p>
                            </div>
                        )}
                    </div>
                </main>

                {showCheckoutModal && (
                    <CheckoutModal
                        cart={cart}
                        total={getCartTotal()}
                        onClose={() => setShowCheckoutModal(false)}
                        onSuccess={() => {
                            setShowCheckoutModal(false);
                            clearCart();
                            loadBills();
                        }}
                    />
                )}
            </div>
        </>
    );
}

function CheckoutModal({ cart, total, onClose, onSuccess }) {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [paymentMethod, setPaymentMethod] = useState("cash");
    const [amountPaid, setAmountPaid] = useState("");

    const change = amountPaid ? Number(amountPaid) - total : 0;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (paymentMethod === "cash" && Number(amountPaid) < total) {
            setError("Amount paid must be greater than or equal to total");
            return;
        }

        const billData = {
            items: cart.map(item => ({
                productCode: item.productCode,
                quantity: item.quantity,
                price: item.unitPrice
            })),
            totalAmount: total,
            paymentMethod: paymentMethod,
            amountPaid: paymentMethod === "cash" ? Number(amountPaid) : total,
            change: paymentMethod === "cash" ? change : 0
        };

        try {
            setLoading(true);
            const response = await apiCreateBill(billData);

            if (response.success) {
                alert(`✅ Bill created successfully!\nBill Number: ${response.data?.billNumber || 'N/A'}\nChange: ₱${change.toFixed(2)}`);
                onSuccess();
            } else {
                setError(response.message || "Failed to create bill");
            }
        } catch (err) {
            setError("Error creating bill: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <style>{`
                .modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0, 0, 0, 0.7);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 9999;
                }

                .checkout-modal {
                    background: white;
                    border-radius: 16px;
                    padding: 32px;
                    max-width: 500px;
                    width: 90%;
                    max-height: 80vh;
                    overflow-y: auto;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.2);
                }

                .modal-title {
                    font-size: 24px;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 24px;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                }

                .order-summary {
                    background: #f9f9f9;
                    border-radius: 12px;
                    padding: 16px;
                    margin-bottom: 24px;
                }

                .order-item {
                    display: flex;
                    justify-content: space-between;
                    padding: 8px 0;
                    border-bottom: 1px solid #e0e0e0;
                }

                .order-item:last-child {
                    border-bottom: none;
                }

                .order-total {
                    font-size: 20px;
                    font-weight: 700;
                    color: #52B788;
                    margin-top: 16px;
                    padding-top: 16px;
                    border-top: 2px solid #52B788;
                    display: flex;
                    justify-content: space-between;
                }

                .form-group-checkout {
                    margin-bottom: 20px;
                }

                .form-label-checkout {
                    display: block;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 8px;
                }

                .payment-methods {
                    display: flex;
                    gap: 12px;
                    margin-bottom: 20px;
                }

                .payment-method-btn {
                    flex: 1;
                    padding: 16px;
                    border: 2px solid #e0e0e0;
                    background: white;
                    border-radius: 8px;
                    cursor: pointer;
                    font-weight: 600;
                    transition: all 0.2s;
                }

                .payment-method-btn.active {
                    border-color: #52B788;
                    background: linear-gradient(135deg, #e8f5ed 0%, #d4ede1 100%);
                    color: #52B788;
                }

                .input-checkout {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-size: 16px;
                }

                .input-checkout:focus {
                    outline: none;
                    border-color: #52B788;
                }

                .change-display {
                    padding: 16px;
                    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                    border-radius: 8px;
                    text-align: center;
                    margin-bottom: 20px;
                }

                .change-label {
                    font-size: 14px;
                    color: #065f46;
                    margin-bottom: 4px;
                }

                .change-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: #065f46;
                }

                .modal-actions {
                    display: flex;
                    gap: 12px;
                }

                .btn-cancel-modal {
                    flex: 1;
                    padding: 14px;
                    background: white;
                    border: 2px solid #e0e0e0;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                }

                .btn-cancel-modal:hover {
                    border-color: #f44336;
                    color: #f44336;
                }

                .btn-confirm-modal {
                    flex: 1;
                    padding: 14px;
                    background: linear-gradient(135deg, #52B788 0%, #40916C 100%);
                    color: white;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.2s;
                    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
                }

                .btn-confirm-modal:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
                }

                .btn-confirm-modal:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                }

                .error-message {
                    padding: 12px;
                    background: #fee2e2;
                    color: #991b1b;
                    border-radius: 8px;
                    margin-bottom: 16px;
                    font-weight: 600;
                }
            `}</style>

            <div className="modal-overlay" onClick={onClose}>
                <div className="checkout-modal" onClick={(e) => e.stopPropagation()}>
                    <h2 className="modal-title">💳 Checkout</h2>

                    {error && <div className="error-message">⚠️ {error}</div>}

                    <div className="order-summary">
                        <h3 style={{ marginBottom: '12px', color: '#666' }}>Order Summary</h3>
                        {cart.map(item => (
                            <div key={item.productCode} className="order-item">
                                <span>{item.name} x{item.quantity}</span>
                                <span>₱{(item.unitPrice * item.quantity).toFixed(2)}</span>
                            </div>
                        ))}
                        <div className="order-total">
                            <span>Total:</span>
                            <span>₱{total.toFixed(2)}</span>
                        </div>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group-checkout">
                            <label className="form-label-checkout">Payment Method</label>
                            <div className="payment-methods">
                                <button
                                    type="button"
                                    className={`payment-method-btn ${paymentMethod === 'cash' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('cash')}
                                >
                                    💵 Cash
                                </button>
                                <button
                                    type="button"
                                    className={`payment-method-btn ${paymentMethod === 'card' ? 'active' : ''}`}
                                    onClick={() => setPaymentMethod('card')}
                                >
                                    💳 Card
                                </button>
                            </div>
                        </div>

                        {paymentMethod === 'cash' && (
                            <>
                                <div className="form-group-checkout">
                                    <label className="form-label-checkout">Amount Paid</label>
                                    <input
                                        type="number"
                                        className="input-checkout"
                                        placeholder="Enter amount"
                                        value={amountPaid}
                                        onChange={(e) => setAmountPaid(e.target.value)}
                                        step="0.01"
                                        required
                                    />
                                </div>

                                {amountPaid && change >= 0 && (
                                    <div className="change-display">
                                        <div className="change-label">Change</div>
                                        <div className="change-value">₱{change.toFixed(2)}</div>
                                    </div>
                                )}
                            </>
                        )}

                        <div className="modal-actions">
                            <button type="button" className="btn-cancel-modal" onClick={onClose}>
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                className="btn-confirm-modal"
                                disabled={loading || (paymentMethod === 'cash' && !amountPaid)}
                            >
                                {loading ? "Processing..." : "Confirm Payment"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </>
    );
}

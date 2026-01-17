import { useState } from "react";
import Sidebar from "../components/common/Sidebar";
import Header from "../components/common/Header";
import syosLogo from "../assets/syos-logo-text.png";

export default function CashierDashboard({ user, onLogout }) {
    const [activeMenu, setActiveMenu] = useState("pos");
    const [sidebarOpen, setSidebarOpen] = useState(true);

    const menuItems = [
        { id: "pos", icon: "🛒", label: "Point of Sale" },
        { id: "transactions", icon: "💰", label: "Transactions" },
        { id: "customers", icon: "👥", label: "Customers" },
        { id: "rewards", icon: "🎁", label: "Rewards" },
        { id: "reports", icon: "📊", label: "Reports" },
    ];

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
                    border: 2px solid transparent;
                }
                
                .product-card:hover {
                    border-color: #66bb6a;
                    transform: scale(1.05);
                }
                
                .product-icon {
                    font-size: 48px;
                    margin-bottom: 8px;
                }
                
                .product-name {
                    font-size: 13px;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 4px;
                }
                
                .product-price {
                    font-size: 14px;
                    color: #66bb6a;
                    font-weight: 700;
                }
                
                .cart-empty {
                    text-align: center;
                    padding: 40px;
                    color: #999;
                }
                
                .checkout-button {
                    width: 100%;
                    background: #66bb6a;
                    color: white;
                    border: none;
                    padding: 16px;
                    border-radius: 8px;
                    font-weight: 700;
                    font-size: 16px;
                    cursor: pointer;
                    margin-top: 20px;
                    transition: background 0.2s ease;
                }
                
                .checkout-button:hover {
                    background: #4caf50;
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

                                {/* Quick Stats */}
                                <div className="quick-stats">
                                    <div className="stat-card">
                                        <div className="stat-label">Today's Sales</div>
                                        <div className="stat-value">$1,234</div>
                                    </div>
                                    <div className="stat-card">
                                        <div className="stat-label">Transactions</div>
                                        <div className="stat-value">47</div>
                                    </div>
                                    <div className="stat-card">
                                        <div className="stat-label">Customers Served</div>
                                        <div className="stat-value">42</div>
                                    </div>
                                    <div className="stat-card">
                                        <div className="stat-label">Avg Transaction</div>
                                        <div className="stat-value">$26</div>
                                    </div>
                                </div>

                                {/* POS Interface */}
                                <div className="pos-section">
                                    <div className="pos-grid">
                                        <div className="products-section">
                                            <h3 className="section-subtitle">Products</h3>
                                            <div className="product-grid">
                                                <div className="product-card">
                                                    <div className="product-icon">☕</div>
                                                    <div className="product-name">Coffee</div>
                                                    <div className="product-price">$3.50</div>
                                                </div>
                                                <div className="product-card">
                                                    <div className="product-icon">🍔</div>
                                                    <div className="product-name">Burger</div>
                                                    <div className="product-price">$8.99</div>
                                                </div>
                                                <div className="product-card">
                                                    <div className="product-icon">🍕</div>
                                                    <div className="product-name">Pizza</div>
                                                    <div className="product-price">$12.99</div>
                                                </div>
                                                <div className="product-card">
                                                    <div className="product-icon">🥗</div>
                                                    <div className="product-name">Salad</div>
                                                    <div className="product-price">$7.50</div>
                                                </div>
                                                <div className="product-card">
                                                    <div className="product-icon">🍰</div>
                                                    <div className="product-name">Cake</div>
                                                    <div className="product-price">$5.99</div>
                                                </div>
                                                <div className="product-card">
                                                    <div className="product-icon">🥤</div>
                                                    <div className="product-name">Drink</div>
                                                    <div className="product-price">$2.50</div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="cart-section">
                                            <h3 className="section-subtitle">Current Order</h3>
                                            <div className="cart-empty">
                                                <p>🛒</p>
                                                <p>No items in cart</p>
                                            </div>
                                            <button className="checkout-button" disabled>
                                                Checkout
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </>
                        )}

                        {activeMenu === "transactions" && (
                            <>
                                <h1 className="section-title">Recent Transactions</h1>
                                <div className="transactions-table">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Transaction ID</th>
                                                <th>Date & Time</th>
                                                <th>Customer</th>
                                                <th>Items</th>
                                                <th>Total</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>#TXN-001</td>
                                                <td>Jan 12, 2026 10:30 AM</td>
                                                <td>John Doe</td>
                                                <td>3</td>
                                                <td>$24.50</td>
                                                <td><span className="badge success">Completed</span></td>
                                            </tr>
                                            <tr>
                                                <td>#TXN-002</td>
                                                <td>Jan 12, 2026 11:15 AM</td>
                                                <td>Jane Smith</td>
                                                <td>2</td>
                                                <td>$18.99</td>
                                                <td><span className="badge success">Completed</span></td>
                                            </tr>
                                            <tr>
                                                <td>#TXN-003</td>
                                                <td>Jan 12, 2026 11:45 AM</td>
                                                <td>Mike Johnson</td>
                                                <td>5</td>
                                                <td>$42.30</td>
                                                <td><span className="badge pending">Pending</span></td>
                                            </tr>
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
            </div>
        </>
    );
}


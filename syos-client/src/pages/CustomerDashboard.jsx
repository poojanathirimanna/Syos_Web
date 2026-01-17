import React from "react";

export default function CustomerDashboard({ user, onLogout }) {
    console.log("🏠 CustomerDashboard props:", { user: user?.username, onLogout: typeof onLogout });

    const username = user?.username || "Customer";
    const fullName = user?.fullName || user?.full_name || username;

    return (
        <>
            <style>{`
                .dashboard-container {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
                    padding: 20px;
                }
                .dashboard-header {
                    background: white;
                    padding: 20px 30px;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 30px;
                }
                .dashboard-title {
                    margin: 0;
                    color: #4facfe;
                    font-size: 28px;
                }
                .user-info {
                    display: flex;
                    align-items: center;
                    gap: 20px;
                }
                .user-name {
                    font-weight: 600;
                    color: #333;
                }
                .role-badge {
                    background: #4facfe;
                    color: white;
                    padding: 5px 15px;
                    border-radius: 20px;
                    font-size: 12px;
                    font-weight: 600;
                }
                .logout-btn {
                    background: #ef4444;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    cursor: pointer;
                    font-weight: 600;
                    transition: background 0.2s;
                }
                .logout-btn:hover {
                    background: #dc2626;
                }
                .dashboard-content {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 20px;
                    max-width: 1200px;
                }
                .dashboard-card {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    transition: transform 0.2s;
                    cursor: pointer;
                }
                .dashboard-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
                }
                .card-title {
                    margin: 0 0 10px 0;
                    color: #4facfe;
                    font-size: 20px;
                }
                .card-description {
                    color: #666;
                    margin: 0;
                }
                .welcome-message {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    margin-bottom: 30px;
                    max-width: 1200px;
                }
                .welcome-message h2 {
                    margin: 0 0 10px 0;
                    color: #333;
                }
                .welcome-message p {
                    margin: 0;
                    color: #666;
                }
            `}</style>

            <div className="dashboard-container">
                <div className="dashboard-header">
                    <h1 className="dashboard-title">🛍️ Customer Dashboard</h1>
                    <div className="user-info">
                        <span className="user-name">{fullName}</span>
                        <span className="role-badge">CUSTOMER</span>
                        <button onClick={onLogout} className="logout-btn">Logout</button>
                    </div>
                </div>

                <div className="welcome-message">
                    <h2>Welcome, {fullName}!</h2>
                    <p>Explore our products and manage your orders.</p>
                </div>

                <div className="dashboard-content">
                    <div className="dashboard-card">
                        <h3 className="card-title">🛒 Browse Products</h3>
                        <p className="card-description">Explore our product catalog</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📦 My Orders</h3>
                        <p className="card-description">View your order history</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">❤️ Favorites</h3>
                        <p className="card-description">Your saved favorite items</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">👤 Profile</h3>
                        <p className="card-description">Manage your account information</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">🎁 Promotions</h3>
                        <p className="card-description">View special offers and deals</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">💬 Support</h3>
                        <p className="card-description">Contact customer service</p>
                    </div>
                </div>
            </div>
        </>
    );
}


import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiMe, apiLogout } from "../services/api";

export default function CashierDashboard() {
    const [username, setUsername] = useState("");
    const [fullName, setFullName] = useState("");
    const nav = useNavigate();

    useEffect(() => {
        (async () => {
            const data = await apiMe();
            if (data?.loggedIn) {
                setUsername(data.username);
                setFullName(data.fullName || data.username);
            } else {
                nav("/login");
            }
        })();
    }, [nav]);

    const onLogout = async () => {
        await apiLogout();
        nav("/login");
    };

    return (
        <>
            <style>{`
                .dashboard-container {
                    min-height: 100vh;
                    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
                    color: #f5576c;
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
                    background: #f5576c;
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
                    color: #f5576c;
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
                    <h1 className="dashboard-title">💳 Cashier Dashboard</h1>
                    <div className="user-info">
                        <span className="user-name">{fullName}</span>
                        <span className="role-badge">CASHIER</span>
                        <button onClick={onLogout} className="logout-btn">Logout</button>
                    </div>
                </div>

                <div className="welcome-message">
                    <h2>Welcome, {fullName}!</h2>
                    <p>Process transactions and manage sales efficiently.</p>
                </div>

                <div className="dashboard-content">
                    <div className="dashboard-card">
                        <h3 className="card-title">🛒 New Sale</h3>
                        <p className="card-description">Process a new customer transaction</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📜 Recent Transactions</h3>
                        <p className="card-description">View recent sales and receipts</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">🔍 Product Lookup</h3>
                        <p className="card-description">Search for products and prices</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">💵 Cash Register</h3>
                        <p className="card-description">View cash drawer and reconcile</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">↩️ Returns & Refunds</h3>
                        <p className="card-description">Process returns and exchanges</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📊 Today's Summary</h3>
                        <p className="card-description">View sales summary for today</p>
                    </div>
                </div>
            </div>
        </>
    );
}


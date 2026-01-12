import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiMe, apiLogout } from "../services/api";

export default function AdminDashboard() {
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
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
                    color: #667eea;
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
                    background: #667eea;
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
                }
                .dashboard-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
                }
                .card-title {
                    margin: 0 0 10px 0;
                    color: #667eea;
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
                    <h1 className="dashboard-title">🛡️ Main Manager Dashboard</h1>
                    <div className="user-info">
                        <span className="user-name">{fullName}</span>
                        <span className="role-badge">MAIN MANAGER</span>
                        <button onClick={onLogout} className="logout-btn">Logout</button>
                    </div>
                </div>

                <div className="welcome-message">
                    <h2>Welcome back, {fullName}!</h2>
                    <p>You have full administrative access to the system.</p>
                </div>

                <div className="dashboard-content">
                    <div className="dashboard-card">
                        <h3 className="card-title">👥 User Management</h3>
                        <p className="card-description">Manage users, roles, and permissions</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📊 Analytics</h3>
                        <p className="card-description">View system analytics and reports</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">💰 Financial Overview</h3>
                        <p className="card-description">Monitor revenue and transactions</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📦 Inventory Control</h3>
                        <p className="card-description">Oversee inventory and stock levels</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">⚙️ System Settings</h3>
                        <p className="card-description">Configure system preferences</p>
                    </div>

                    <div className="dashboard-card">
                        <h3 className="card-title">📝 Reports</h3>
                        <p className="card-description">Generate and export reports</p>
                    </div>
                </div>
            </div>
        </>
    );
}


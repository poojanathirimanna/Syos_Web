import { useState } from "react";
import Sidebar from "../components/common/Sidebar";
import Header from "../components/common/Header";
import StatCard from "../components/dashboard/StatCard";
import CompanyCard from "../components/dashboard/CompanyCard";
import ProductManagement from "../components/dashboard/ProductManagement";
import CategoryManagement from "../components/dashboard/CategoryManagement";
import InventoryManagement from "../components/dashboard/InventoryManagement";
import CashierManagement from "../components/dashboard/CashierManagement";
import ReportsManagement from "../components/dashboard/ReportsManagement";
import OrdersManagement from "../components/dashboard/OrdersManagement";
import syosLogo from "../assets/syos-logo-text.png";

export default function AdminDashboard({ user, onLogout }) {
    console.log("🎯 AdminDashboard props:", { user: user?.username, onLogout: typeof onLogout });

    const [activeMenu, setActiveMenu] = useState("dashboard");
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [expandedMenus, setExpandedMenus] = useState(["product-management"]);

    const menuItems = [
        { id: "dashboard", icon: "📊", label: "Dashboard" },
        { 
            id: "product-management", 
            icon: "📦", 
            label: "Product Management",
            submenu: [
                { id: "products", icon: "🛍️", label: "Products" },
                { id: "categories", icon: "📁", label: "Categories" }
            ]
        },
        { id: "inventory", icon: "📋", label: "Inventory" },
        { id: "orders", icon: "🛒", label: "Orders" },
        { id: "reports", icon: "📈", label: "Reports" },
        { id: "cashiers", icon: "👤", label: "Cashiers" },
    ];

    const handleMenuClick = (id) => {
        // Check if it's a parent menu with submenu
        const menuItem = menuItems.find(item => item.id === id);
        if (menuItem && menuItem.submenu) {
            // Toggle expansion
            setExpandedMenus(prev => 
                prev.includes(id) 
                    ? prev.filter(menuId => menuId !== id)
                    : [...prev, id]
            );
        } else {
            setActiveMenu(id);
        }
    };

    const companies = [
        { name: "DSI", color: "blue" },
        { name: "MA's Kitchen", color: "gray" },
        { name: "Touch of Nature", color: "cyan" },
        { name: "Company Name", color: "pink" },
        { name: "Company Name", color: "purple" },
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
                    font-size: 20px;
                    color: #888;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    margin-bottom: 24px;
                }
                
                .stats-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                    gap: 20px;
                    margin-bottom: 40px;
                }
                
                .platform-stats {
                    display: flex;
                    justify-content: space-around;
                    align-items: center;
                    margin-bottom: 16px;
                }
                
                .platform-item {
                    text-align: center;
                }
                
                .platform-icon {
                    font-size: 40px;
                    margin-bottom: 8px;
                }
                
                .platform-count {
                    font-size: 32px;
                    font-weight: 700;
                    color: #333;
                }
                
                .companies-section {
                    margin-top: 40px;
                }
                
                .companies-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                    gap: 20px;
                }
                
                @media (max-width: 768px) {
                    .stats-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .companies-grid {
                        grid-template-columns: 1fr;
                    }
                }
            `}</style>

            <div className="dashboard-container">
                <Sidebar
                    logo={syosLogo}
                    menuItems={menuItems}
                    activeMenu={activeMenu}
                    onMenuClick={handleMenuClick}
                    isOpen={sidebarOpen}
                    accentColor="#52B788"
                    expandedMenus={expandedMenus}
                />

                <main className="main-content">
                    {console.log("📤 Passing to Header:", { user: user?.username, onLogout: typeof onLogout })}
                    <Header
                        user={user}
                        onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
                        onLogout={onLogout}
                        showNotifications={true}
                    />

                    <div className="content-area">
                        {activeMenu === "dashboard" && (
                            <>
                                <h1 className="section-title">Admin Overview</h1>

                                <div className="stats-grid">
                                    <StatCard
                                        label="No. of Companies"
                                        value="15"
                                        buttonText="View Companies"
                                        onButtonClick={() => console.log('View Companies')}
                                    />

                                    <StatCard
                                        label="No. of Brands"
                                        value="14"
                                        buttonText="View Brands"
                                    />

                                    <StatCard
                                        label="Engagement Count"
                                        value="92"
                                        buttonText="View Details"
                                    />

                                    <StatCard
                                        label="No. of Rewards"
                                        value="1586"
                                        buttonText="View Details"
                                    />

                                    <StatCard
                                        label="Total Customers"
                                        value="242"
                                        buttonText="View Customers"
                                    />

                                    <StatCard>
                                        <div className="platform-stats">
                                            <div className="platform-item">
                                                <div className="platform-icon">🤖</div>
                                                <div className="platform-count">242</div>
                                            </div>
                                            <div className="platform-item">
                                                <div className="platform-icon">🍎</div>
                                                <div className="platform-count">0</div>
                                            </div>
                                        </div>
                                    </StatCard>

                                    <StatCard
                                        label="Ongoing Campaigns"
                                        value="47"
                                        buttonText="View Campaigns"
                                    />

                                    <StatCard
                                        label="Campaigns Ending Soon"
                                        value="0"
                                        buttonText="View Campaigns"
                                    />
                                </div>

                                <div className="companies-section">
                                    <h2 className="section-title">Company Drill Down</h2>
                                    <h3 className="section-title" style={{fontSize: '18px', color: '#aaa', marginTop: '20px'}}>
                                        Companies
                                    </h3>

                                    <div className="companies-grid">
                                        {companies.map((company, index) => (
                                            <CompanyCard
                                                key={index}
                                                name={company.name}
                                                colorScheme={company.color}
                                                onClick={() => console.log(`Clicked ${company.name}`)}
                                            />
                                        ))}
                                    </div>
                                </div>
                            </>
                        )}

                        {activeMenu === "admin" && (
                            <div>
                                <h1 className="section-title">Admin Dashboard</h1>
                                <p style={{color: '#666'}}>Admin dashboard content goes here...</p>
                            </div>
                        )}

                        {activeMenu === "brand" && (
                            <div>
                                <h1 className="section-title">Brand Dashboard</h1>
                                <p style={{color: '#666'}}>Brand dashboard content goes here...</p>
                            </div>
                        )}

                        {activeMenu === "products" && (
                            <ProductManagement />
                        )}

                        {activeMenu === "categories" && (
                            <CategoryManagement />
                        )}

                        {activeMenu === "inventory" && (
                            <InventoryManagement />
                        )}

                        {activeMenu === "cashiers" && (
                            <CashierManagement />
                        )}

                        {activeMenu === "orders" && (
                            <OrdersManagement />
                        )}

                        {activeMenu === "reports" && (
                            <ReportsManagement />
                        )}

                        {activeMenu !== "dashboard" && activeMenu !== "admin" && activeMenu !== "brand" && activeMenu !== "products" && activeMenu !== "categories" && activeMenu !== "inventory" && activeMenu !== "cashiers" && activeMenu !== "orders" && activeMenu !== "reports" && (
                            <div>
                                <h1 className="section-title">
                                    {menuItems.find(m => m.id === activeMenu)?.label}
                                </h1>
                                <p style={{color: '#666'}}>Content for {activeMenu} section coming soon...</p>
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </>
    );
}

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/common/Sidebar";
import Header from "../components/common/Header";
import AnalyticsDashboard from "../components/dashboard/AnalyticsDashboard";
import ProductManagement from "../components/dashboard/ProductManagement";
import CategoryManagement from "../components/dashboard/CategoryManagement";
import InventoryManagement from "../components/dashboard/InventoryManagement";
import CashierManagement from "../components/dashboard/CashierManagement";
import ReportsManagement from "../components/dashboard/ReportsManagement";
import OrdersManagement from "../components/dashboard/OrdersManagement";
import DiscountsManagement from "./DiscountsManagement";
import syosLogo from "../assets/syos-logo-text.png";

export default function AdminDashboard({ user, onLogout, initialMenu }) {
    console.log("🎯 AdminDashboard received:", { 
        user: user?.username, 
        hasOnLogout: !!onLogout,
        onLogoutType: typeof onLogout 
    });

    const [activeMenu, setActiveMenu] = useState(initialMenu || "dashboard");
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [expandedMenus, setExpandedMenus] = useState(["product-management"]);

    const navigate = useNavigate();

    const menuItems = [
        { id: "dashboard", icon: "📊", label: "Analytics" },
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
        { id: "discounts", icon: "🏷️", label: "Discounts" },
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

                    <Header
                        user={user}
                        onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
                        onLogout={onLogout}
                    />

                    <div className="content-area">
                        {activeMenu === "dashboard" && (
                            <AnalyticsDashboard />
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

                        {activeMenu === "discounts" && (
                            <DiscountsManagement />
                        )}

                        {activeMenu === "reports" && (
                            <ReportsManagement />
                        )}

                        {activeMenu !== "dashboard" && activeMenu !== "products" && activeMenu !== "categories" && activeMenu !== "inventory" && activeMenu !== "cashiers" && activeMenu !== "orders" && activeMenu !== "discounts" && activeMenu !== "reports" && (
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

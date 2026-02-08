import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import Login from "./pages/Login.jsx";
import Home from "./pages/Home.jsx";
import Register from "./pages/Register.jsx";
import ProtectedRoute from "./pages/ProtectedRoute.jsx";
import AdminDashboard from "./pages/AdminDashboard";
import CashierDashboard from "./pages/CashierDashboard";

// Customer Pages
import ProductsPage from "./pages/customer/ProductsPage";
import ProductDetailsPage from "./pages/customer/ProductDetailsPage";
import CartPage from "./pages/customer/CartPage";
import CheckoutPage from "./pages/customer/CheckoutPage";
import OrdersPage from "./pages/customer/OrdersPage";
import OrderDetailsPage from "./pages/customer/OrderDetailsPage";
import WishlistPage from "./pages/customer/WishlistPage";

import { apiMe, apiLogout } from "./services/api";

export default function App() {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        // Check for existing session
        const checkSession = async () => {
            const result = await apiMe();
            if (result?.loggedIn) {
                setUser(result);
            }
        };
        checkSession();
    }, []);

    const handleLogout = async () => {
        await apiLogout();
        setUser(null);
        navigate("/login", { replace: true });
    };

    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" />} />

            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            <Route
                path="/home"
                element={
                    <ProtectedRoute>
                        <Home />
                    </ProtectedRoute>
                }
            />

            {/* Customer Routes */}
            <Route
                path="/customer/products"
                element={
                    <ProtectedRoute>
                        <ProductsPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/products/:productCode"
                element={
                    <ProtectedRoute>
                        <ProductDetailsPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/cart"
                element={
                    <ProtectedRoute>
                        <CartPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/checkout"
                element={
                    <ProtectedRoute>
                        <CheckoutPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/orders"
                element={
                    <ProtectedRoute>
                        <OrdersPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/orders/:billNumber"
                element={
                    <ProtectedRoute>
                        <OrderDetailsPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/customer/wishlist"
                element={
                    <ProtectedRoute>
                        <WishlistPage user={user} onLogout={handleLogout} />
                    </ProtectedRoute>
                }
            />

            {/* Redirect all dashboard routes to /home - Home.jsx will handle role-based routing */}
            <Route path="/admin/dashboard" element={<Navigate to="/home" replace />} />
            <Route path="/admin/discounts" element={<Navigate to="/home" replace />} />
            <Route path="/cashier/dashboard" element={<Navigate to="/home" replace />} />
            <Route path="/cashier/promotions" element={<Navigate to="/home" replace />} />
            <Route path="/customer/dashboard" element={<Navigate to="/home" replace />} />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
    );
}

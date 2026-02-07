import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login.jsx";
import Home from "./pages/Home.jsx";
import Register from "./pages/Register.jsx";
import ProtectedRoute from "./pages/ProtectedRoute.jsx";
import AdminDashboard from "./pages/AdminDashboard";
import CashierDashboard from "./pages/CashierDashboard";

export default function App() {
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

import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login.jsx";
import Home from "./pages/Home.jsx";
import Register from "./pages/Register.jsx";
import ProtectedRoute from "./pages/ProtectedRoute.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import CashierDashboard from "./pages/CashierDashboard.jsx";
import CustomerDashboard from "./pages/CustomerDashboard.jsx";

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

            {/* Role-specific Dashboards */}
            <Route
                path="/admin/dashboard"
                element={
                    <ProtectedRoute>
                        <AdminDashboard />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/cashier/dashboard"
                element={
                    <ProtectedRoute>
                        <CashierDashboard />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/customer/dashboard"
                element={
                    <ProtectedRoute>
                        <CustomerDashboard />
                    </ProtectedRoute>
                }
            />
        </Routes>
    );
}

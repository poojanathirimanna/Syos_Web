import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiMe, apiLogout } from "../services/api";
import AdminDashboard from "./AdminDashboard";
import CashierDashboard from "./CashierDashboard";
import CustomerDashboard from "./CustomerDashboard";

export default function Home() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();

    useEffect(() => {
        (async () => {
            const data = await apiMe();

            if (data?.loggedIn) {
                setUser(data);
            } else {
                nav("/login");
            }
            setLoading(false);
        })();
    }, [nav]);

    // Handle customer redirect - must be before any conditional returns
    useEffect(() => {
        if (!loading && user) {
            const roleId = user?.role_id || user?.roleId;
            if (roleId === 3 || roleId === 4) {
                console.log("📤 Home.jsx: Redirecting customer to products page");
                // Small delay to ensure session is fully established
                setTimeout(() => {
                    nav("/customer/products", { replace: true });
                }, 100);
            }
        }
    }, [user, loading, nav]);

    const onLogout = async () => {
        console.log("🚪 Home.jsx: Logout initiated");
        try {
            const result = await apiLogout();
            console.log("✅ Logout API response:", result);
            
            // Clear user state immediately
            setUser(null);
            
            // Navigate to login
            nav("/login", { replace: true });
            console.log("🔄 Redirected to login page");
        } catch (error) {
            console.error("❌ Logout error:", error);
            // Clear state and redirect even if API call fails
            setUser(null);
            nav("/login", { replace: true });
        }
    };

    if (loading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                fontSize: '24px',
                color: '#666'
            }}>
                Loading...
            </div>
        );
    }

    // If no user data, redirect to login
    if (!user) {
        nav("/login");
        return null;
    }

    // Determine dashboard based on user role_id
    // Backend may return roleId or role_id - support both formats
    const roleId = user?.role_id || user?.roleId;

    // Admin Dashboard (role_id = 1)
    if (roleId === 1) {
        console.log("📤 Home.jsx: Passing to AdminDashboard:", { 
            user: user?.username, 
            hasOnLogout: !!onLogout,
            onLogoutType: typeof onLogout 
        });
        return <AdminDashboard user={user} onLogout={onLogout} />;
    }

    // Cashier Dashboard (role_id = 2)
    if (roleId === 2) {
        console.log("📤 Home.jsx: Passing to CashierDashboard:", { 
            user: user?.username, 
            hasOnLogout: !!onLogout 
        });
        return <CashierDashboard user={user} onLogout={onLogout} />;
    }

    // Customer Dashboard (role_id = 3 or 4) - Handled by useEffect redirect
    if (roleId === 3 || roleId === 4) {
        return null;
    }

    // Fallback for unknown roles
    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh',
            fontSize: '24px',
            color: '#666'
        }}>
            Invalid role. Redirecting to login...
        </div>
    );
}


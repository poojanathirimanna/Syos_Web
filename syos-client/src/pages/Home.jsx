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
            console.log("🔍 Home: Fetching user data...");
            const data = await apiMe();
            console.log("🔍 Home: apiMe response:", data);

            if (data?.loggedIn) {
                console.log("✅ Home: User is logged in:", data);
                setUser(data);
            } else {
                console.log("❌ Home: User not logged in, redirecting...");
                nav("/login");
            }
            setLoading(false);
        })();
    }, [nav]);

    const onLogout = async () => {
        console.log("🚪 Starting logout process...");
        try {
            const result = await apiLogout();
            console.log("✅ Logout API response:", result);
            nav("/login");
            console.log("🔄 Redirecting to login page...");
        } catch (error) {
            console.error("❌ Logout error:", error);
            // Still navigate to login even if there's an error
            nav("/login");
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
        console.log("⚠️ Home: No user data, redirecting to login...");
        nav("/login");
        return null;
    }

    // Determine dashboard based on user role_id
    // Backend may return roleId or role_id - support both formats
    const roleId = user?.role_id || user?.roleId;

    console.log("🔍 Home: user state:", user);
    console.log("🔍 Home: role_id:", user?.role_id);
    console.log("🔍 Home: roleId:", user?.roleId);
    console.log("🔍 Home: resolved roleId:", roleId);
    console.log("🔍 Home: onLogout type:", typeof onLogout);

    // Admin Dashboard (role_id = 1)
    if (roleId === 1) {
        console.log("✅ Home: Rendering AdminDashboard for roleId 1");
        return <AdminDashboard user={user} onLogout={onLogout} />;
    }

    // Cashier Dashboard (role_id = 2)
    if (roleId === 2) {
        console.log("✅ Home: Rendering CashierDashboard for roleId 2");
        return <CashierDashboard user={user} onLogout={onLogout} />;
    }

    // Customer Dashboard (role_id = 3 or 4) - Landing/Home Page
    if (roleId === 3 || roleId === 4) {
        console.log("✅ Home: Rendering CustomerDashboard for roleId", roleId);
        return <CustomerDashboard user={user} onLogout={onLogout} />;
    }

    // Fallback for unknown roles - redirect to login
    console.log("⚠️ Home: Unknown roleId:", roleId, "- redirecting to login...");
    setTimeout(() => nav("/login"), 0);  // Use setTimeout to avoid setState during render
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


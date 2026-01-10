import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { apiMe } from "../services/api";

export default function ProtectedRoute({ children }) {
    const [loading, setLoading] = useState(true);
    const [ok, setOk] = useState(false);

    useEffect(() => {
        (async () => {
            const data = await apiMe();
            setOk(data?.loggedIn === true);
            setLoading(false);
        })();
    }, []);

    if (loading) return <div style={{ padding: 24 }}>Checking session...</div>;
    if (!ok) return <Navigate to="/login" replace />;
    return children;
}

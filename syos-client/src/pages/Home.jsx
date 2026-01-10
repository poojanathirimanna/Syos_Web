import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiMe, apiLogout } from "../services/api";

export default function Home() {
    const [username, setUsername] = useState("");
    const nav = useNavigate();

    useEffect(() => {
        (async () => {
            const data = await apiMe();
            if (data?.loggedIn) setUsername(data.username);
        })();
    }, []);

    const onLogout = async () => {
        await apiLogout();
        nav("/login");
    };

    return (
        <div style={{ padding: 24 }}>
            <h2>Home ✅</h2>
            <p>Logged in as: <b>{username}</b></p>
            <button onClick={onLogout}>Logout</button>
        </div>
    );
}

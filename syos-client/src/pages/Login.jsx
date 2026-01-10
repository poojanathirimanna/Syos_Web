import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { GoogleLogin } from '@react-oauth/google';
import { apiLogin, apiGoogleLogin } from "../services/api";
import syosLogo from "../assets/syos-logo.png";
import syosLogoText from "../assets/syos-logo-text.png";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [msg, setMsg] = useState("");
    const nav = useNavigate();

    const submit = async (e) => {
        e.preventDefault();
        setMsg("");

        try {
            const res = await apiLogin(username, password);
            if (res.ok) nav("/home");
            else setMsg(res.message);
        } catch {
            setMsg("Server error");
        }
    };

    const handleGoogleSuccess = async (credentialResponse) => {
        setMsg("");
        try {
            const res = await apiGoogleLogin(credentialResponse.credential);
            if (res.ok) {
                nav("/home");
            } else {
                setMsg(res.message || "Google login failed");
            }
        } catch (error) {
            setMsg("Server error during Google login");
        }
    };

    const handleGoogleError = () => {
        setMsg("Google login failed. Please try again.");
    };

    return (
        <>
            <style>{`
        * {
          box-sizing: border-box;
        }
        .auth-wrap {
          min-height: 100vh;
          display: grid;
          grid-template-columns: 1fr 1fr;
          background: #ffffff;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        }
        .left {
          background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 40px;
        }
        .left-logo {
          width: 500px;
          max-width: 90%;
          filter: drop-shadow(0 10px 30px rgba(34, 197, 94, 0.2));
        }
        .right {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 40px;
          background: #fafafa;
        }
        .card {
          width: 100%;
          max-width: 550px;
          padding: 60px;
          border-radius: 24px;
          box-shadow: 0 20px 60px rgba(0,0,0,.08), 0 0 1px rgba(0,0,0,.1);
          border: 1px solid #e5e7eb;
          background: white;
        }
        .card-logo {
          width: 280px;
          display: block;
          margin: 0 auto 35px;
        }
        h2 {
          text-align: center;
          margin-bottom: 10px;
          font-size: 36px;
          font-weight: 700;
          color: #1a1a1a;
          letter-spacing: -0.5px;
        }
        .subtitle {
          text-align: center;
          color: #64748b;
          font-size: 17px;
          margin-bottom: 35px;
          font-weight: 400;
        }
        label {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #374151;
          margin-bottom: 8px;
          margin-top: 20px;
        }
        input {
          width: 100%;
          padding: 14px 16px;
          border-radius: 12px;
          border: 2px solid #e5e7eb;
          font-size: 16px;
          transition: all 0.2s ease;
          background: #fafafa;
        }
        input:focus {
          outline: none;
          border-color: #22c55e;
          background: white;
          box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
        }
        input::placeholder {
          color: #9ca3af;
        }
        button {
          width: 100%;
          margin-top: 28px;
          padding: 16px;
          border-radius: 12px;
          border: none;
          font-weight: 700;
          font-size: 16px;
          background: #22c55e;
          color: white;
          cursor: pointer;
          transition: all 0.2s ease;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }
        button:hover {
          background: #16a34a;
          transform: translateY(-1px);
          box-shadow: 0 10px 25px rgba(34, 197, 94, 0.3);
        }
        button:active {
          transform: translateY(0);
        }
        .msg {
          margin-top: 20px;
          padding: 12px;
          color: #b91c1c;
          background: #fef2f2;
          border: 1px solid #fecaca;
          border-radius: 8px;
          font-size: 14px;
          text-align: center;
          font-weight: 500;
        }
        .divider {
          margin: 24px 0;
          text-align: center;
          position: relative;
        }
        .divider::before,
        .divider::after {
          content: '';
          position: absolute;
          top: 50%;
          width: 45%;
          height: 1px;
          background: #e5e7eb;
        }
        .divider::before {
          left: 0;
        }
        .divider::after {
          right: 0;
        }
        .divider span {
          background: white;
          padding: 0 10px;
          color: #64748b;
          font-size: 14px;
        }
        .google-btn-container {
          display: flex;
          justify-content: center;
          margin-top: 20px;
        }
        .footer {
          margin-top: 28px;
          text-align: center;
          font-size: 15px;
          color: #64748b;
        }
        .footer a {
          color: #22c55e;
          text-decoration: none;
          font-weight: 600;
          transition: color 0.2s ease;
        }
        .footer a:hover {
          color: #16a34a;
          text-decoration: underline;
        }
        @media (max-width: 900px) {
          .auth-wrap {
            grid-template-columns: 1fr;
          }
          .left {
            display: none;
          }
          .card {
            padding: 40px;
          }
        }
      `}</style>

            <div className="auth-wrap">
                <div className="left">
                    <img src={syosLogo} className="left-logo" alt="SYOS Logo" />
                </div>

                <div className="right">
                    <div className="card">
                        <img src={syosLogoText} className="card-logo" alt="SYOS" />
                        <h2>Welcome</h2>
                        <p className="subtitle">Sign in to SYOS</p>

                        <form onSubmit={submit}>
                            <label htmlFor="username">Username</label>
                            <input
                                id="username"
                                placeholder="Enter your username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />

                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                type="password"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />

                            <button>SIGN IN</button>
                        </form>

                        {msg && <div className="msg">{msg}</div>}

                        <div className="divider">
                            <span>OR</span>
                        </div>

                        <div className="google-btn-container">
                            <GoogleLogin
                                onSuccess={handleGoogleSuccess}
                                onError={handleGoogleError}
                                theme="outline"
                                size="large"
                                text="signin_with"
                                shape="rectangular"
                            />
                        </div>

                        <div className="footer">
                            Don't have an account? <Link to="/register">Sign up</Link>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

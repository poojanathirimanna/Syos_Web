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
    const [showPassword, setShowPassword] = useState(false);
    const nav = useNavigate();

    const submit = async (e) => {
        e.preventDefault();
        setMsg("");

        try {
            const res = await apiLogin(username, password);
            if (res.success) {
                // Always navigate to /home - Home.jsx will handle role-based dashboard rendering
                nav("/home");
            } else {
                setMsg(res.message);
            }
        } catch {
            setMsg("Server error");
        }
    };

    const handleGoogleSuccess = async (credentialResponse) => {
        setMsg("");
        try {
            const res = await apiGoogleLogin(credentialResponse.credential);
            if (res.success) {
                // Always navigate to /home - Home.jsx will handle role-based dashboard rendering
                nav("/home");
            } else {
                setMsg(res.message || "Google login failed");
            }
        } catch (error) {
            console.error("Google login error:", error);
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
        .password-wrapper {
          position: relative;
          display: flex;
          align-items: center;
        }
        .password-wrapper input {
          padding-right: 50px;
        }
        .password-toggle {
          position: absolute;
          right: 12px;
          background: none;
          border: none;
          cursor: pointer;
          padding: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #6b7280;
          transition: color 0.2s ease;
          width: auto;
          margin: 0;
        }
        .password-toggle:hover {
          color: #22c55e;
          background: none;
          transform: none;
          box-shadow: none;
        }
        .password-toggle svg {
          width: 20px;
          height: 20px;
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
                            <div className="password-wrapper">
                                <input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="Enter your password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() => setShowPassword(!showPassword)}
                                    aria-label={showPassword ? "Hide password" : "Show password"}
                                >
                                    {showPassword ? (
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                                        </svg>
                                    ) : (
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                        </svg>
                                    )}
                                </button>
                            </div>

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

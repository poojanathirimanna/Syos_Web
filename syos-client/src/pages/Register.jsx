import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { GoogleLogin } from '@react-oauth/google';
import { apiRegister, apiGoogleLogin } from "../services/api";
import syosLogo from "../assets/syos-logo.png";
import syosLogoText from "../assets/syos-logo-text.png";

export default function Register() {
    const [user_id, setUserId] = useState("");
    const [full_name, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [contact_number, setContactNumber] = useState("");
    const [password, setPassword] = useState("");
    const [confirm_password, setConfirmPassword] = useState("");
    const [msg, setMsg] = useState("");
    const [passwordStrength, setPasswordStrength] = useState("");
    const [touched, setTouched] = useState({});
    const nav = useNavigate();

    // Calculate password strength
    const calculatePasswordStrength = (pwd) => {
        if (pwd.length === 0) return "";
        let strength = 0;
        if (pwd.length >= 6) strength++;
        if (pwd.length >= 8) strength++;
        if (/[a-z]/.test(pwd)) strength++;
        if (/[A-Z]/.test(pwd)) strength++;
        if (/[0-9]/.test(pwd)) strength++;
        if (/[^a-zA-Z0-9]/.test(pwd)) strength++;

        if (strength <= 2) return "weak";
        if (strength <= 4) return "medium";
        return "strong";
    };

    // Handle password change with strength calculation
    const handlePasswordChange = (e) => {
        const pwd = e.target.value;
        setPassword(pwd);
        setPasswordStrength(calculatePasswordStrength(pwd));
    };

    const submit = async (e) => {
        e.preventDefault();
        setMsg("");

        // Validate Username
        if (user_id.trim().length < 3) {
            setMsg("Username must be at least 3 characters long");
            return;
        }
        if (!/^[a-zA-Z0-9_]+$/.test(user_id)) {
            setMsg("Username can only contain letters, numbers, and underscores");
            return;
        }

        // Validate Full Name
        if (full_name.trim().length < 2) {
            setMsg("Full name must be at least 2 characters long");
            return;
        }
        if (!/^[a-zA-Z\s]+$/.test(full_name.trim())) {
            setMsg("Full name can only contain letters and spaces");
            return;
        }

        // Validate Email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setMsg("Please enter a valid email address");
            return;
        }

        // Validate Contact Number
        const phoneRegex = /^[0-9]{10,15}$/;
        if (!phoneRegex.test(contact_number.replace(/[\s\-()]/g, ''))) {
            setMsg("Contact number must be 10-15 digits");
            return;
        }

        // Validate Password
        if (password.length < 6) {
            setMsg("Password must be at least 6 characters long");
            return;
        }
        if (!/(?=.*[a-z])/.test(password)) {
            setMsg("Password must contain at least one lowercase letter");
            return;
        }
        if (!/(?=.*[A-Z])/.test(password)) {
            setMsg("Password must contain at least one uppercase letter");
            return;
        }
        if (!/(?=.*[0-9])/.test(password)) {
            setMsg("Password must contain at least one number");
            return;
        }

        // Validate Password Match
        if (password !== confirm_password) {
            setMsg("Passwords do not match");
            return;
        }

        const data = await apiRegister({ user_id, full_name, email, contact_number, password });

        if (data?.success) {
            setMsg("Account created. Redirecting to login...");
            setTimeout(() => nav("/login"), 700);
        } else {
            setMsg(data?.message || "Registration failed");
        }
    };

    const handleClear = () => {
        setUserId("");
        setFullName("");
        setEmail("");
        setContactNumber("");
        setPassword("");
        setConfirmPassword("");
        setMsg("");
        setPasswordStrength("");
        setTouched({});
    };

    const handleGoogleSuccess = async (credentialResponse) => {
        setMsg("");
        try {
            const res = await apiGoogleLogin(credentialResponse.credential);
            if (res.success) {
                nav("/home");
            } else {
                setMsg(res.message || "Google sign-up failed");
            }
        } catch (error) {
            setMsg("Server error during Google sign-up");
        }
    };

    const handleGoogleError = () => {
        setMsg("Google sign-up failed. Please try again.");
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
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 40px;
          background: #fafafa;
        }
        .right {
          background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 40px;
        }
        .right-logo {
          width: 500px;
          max-width: 90%;
          filter: drop-shadow(0 10px 30px rgba(34, 197, 94, 0.2));
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
        .form-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
        }
        .form-group {
          display: flex;
          flex-direction: column;
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
        .input-error {
          border-color: #ef4444 !important;
        }
        .input-success {
          border-color: #22c55e !important;
        }
        .helper-text {
          font-size: 12px;
          margin-top: 6px;
          color: #6b7280;
        }
        .password-strength {
          margin-top: 8px;
          font-size: 12px;
          font-weight: 600;
        }
        .password-weak {
          color: #ef4444;
        }
        .password-medium {
          color: #f59e0b;
        }
        .password-strong {
          color: #22c55e;
        }
        .button-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
          margin-top: 28px;
        }
        button {
          width: 100%;
          padding: 16px;
          border-radius: 12px;
          border: none;
          font-weight: 700;
          font-size: 16px;
          cursor: pointer;
          transition: all 0.2s ease;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }
        .btn-submit {
          background: #22c55e;
          color: white;
        }
        .btn-submit:hover {
          background: #16a34a;
          transform: translateY(-1px);
          box-shadow: 0 10px 25px rgba(34, 197, 94, 0.3);
        }
        .btn-clear {
          background: #ef4444;
          color: white;
        }
        .btn-clear:hover {
          background: #dc2626;
          transform: translateY(-1px);
          box-shadow: 0 10px 25px rgba(239, 68, 68, 0.3);
        }
        button:active {
          transform: translateY(0);
        }
        .msg {
          margin-top: 20px;
          padding: 12px;
          border-radius: 8px;
          font-size: 14px;
          text-align: center;
          font-weight: 500;
        }
        .msg-success {
          color: #065f46;
          background: #d1fae5;
          border: 1px solid #6ee7b7;
        }
        .msg-error {
          color: #b91c1c;
          background: #fef2f2;
          border: 1px solid #fecaca;
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
          .right {
            display: none;
          }
          .card {
            padding: 40px;
          }
        }
      `}</style>

            <div className="auth-wrap">
                <div className="left">
                    <div className="card">
                        <img src={syosLogoText} className="card-logo" alt="SYOS" />
                        <h2>Create Account</h2>
                        <p className="subtitle">Join SYOS today</p>

                        <form onSubmit={submit}>
                            <label htmlFor="user_id">Username *</label>
                            <input
                                id="user_id"
                                placeholder="Enter your username"
                                value={user_id}
                                onChange={(e) => setUserId(e.target.value)}
                                onBlur={() => setTouched({...touched, user_id: true})}
                                className={touched.user_id && user_id.length > 0 && user_id.length < 3 ? 'input-error' : ''}
                                required
                            />
                            {touched.user_id && user_id.length > 0 && user_id.length < 3 && (
                                <p className="helper-text" style={{color: '#ef4444'}}>At least 3 characters required</p>
                            )}

                            <label htmlFor="full_name">Full Name *</label>
                            <input
                                id="full_name"
                                placeholder="Enter your full name"
                                value={full_name}
                                onChange={(e) => setFullName(e.target.value)}
                                onBlur={() => setTouched({...touched, full_name: true})}
                                className={touched.full_name && full_name.length > 0 && full_name.length < 2 ? 'input-error' : ''}
                                required
                            />
                            {touched.full_name && full_name.length > 0 && full_name.length < 2 && (
                                <p className="helper-text" style={{color: '#ef4444'}}>At least 2 characters required</p>
                            )}

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="email">Email *</label>
                                    <input
                                        id="email"
                                        type="email"
                                        placeholder="Email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        onBlur={() => setTouched({...touched, email: true})}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="contact_number">Contact Number *</label>
                                    <input
                                        id="contact_number"
                                        type="tel"
                                        placeholder="Contact Number"
                                        value={contact_number}
                                        onChange={(e) => setContactNumber(e.target.value)}
                                        onBlur={() => setTouched({...touched, contact_number: true})}
                                        required
                                    />
                                    {touched.contact_number && contact_number.length > 0 && (
                                        <p className="helper-text">10-15 digits</p>
                                    )}
                                </div>
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="password">Password *</label>
                                    <input
                                        id="password"
                                        type="password"
                                        placeholder="Password"
                                        value={password}
                                        onChange={handlePasswordChange}
                                        onBlur={() => setTouched({...touched, password: true})}
                                        required
                                    />
                                    {passwordStrength && (
                                        <p className={`password-strength password-${passwordStrength}`}>
                                            Strength: {passwordStrength.toUpperCase()}
                                        </p>
                                    )}
                                    {touched.password && password.length > 0 && password.length < 6 && (
                                        <p className="helper-text" style={{color: '#ef4444'}}>
                                            Min 6 characters, include uppercase, lowercase, and number
                                        </p>
                                    )}
                                </div>
                                <div className="form-group">
                                    <label htmlFor="confirm_password">Confirm Password *</label>
                                    <input
                                        id="confirm_password"
                                        type="password"
                                        placeholder="Confirm Password"
                                        value={confirm_password}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        onBlur={() => setTouched({...touched, confirm_password: true})}
                                        className={touched.confirm_password && confirm_password.length > 0 && password !== confirm_password ? 'input-error' : ''}
                                        required
                                    />
                                    {touched.confirm_password && confirm_password.length > 0 && password !== confirm_password && (
                                        <p className="helper-text" style={{color: '#ef4444'}}>Passwords do not match</p>
                                    )}
                                    {touched.confirm_password && confirm_password.length > 0 && password === confirm_password && (
                                        <p className="helper-text" style={{color: '#22c55e'}}>Passwords match</p>
                                    )}
                                </div>
                            </div>

                            <div className="button-row">
                                <button type="button" className="btn-clear" onClick={handleClear}>
                                    CLEAR
                                </button>
                                <button type="submit" className="btn-submit">
                                    SIGN UP
                                </button>
                            </div>
                        </form>

                        {msg && (
                            <p className={`msg ${msg.includes("created") ? "msg-success" : "msg-error"}`}>
                                {msg}
                            </p>
                        )}

                        <div className="divider">
                            <span>OR</span>
                        </div>

                        <div className="google-btn-container">
                            <GoogleLogin
                                onSuccess={handleGoogleSuccess}
                                onError={handleGoogleError}
                                theme="outline"
                                size="large"
                                text="signup_with"
                                shape="rectangular"
                            />
                        </div>

                        <p className="footer">
                            Already have an account? <Link to="/login">Sign In</Link>
                        </p>
                    </div>
                </div>

                <div className="right">
                    <img src={syosLogo} className="right-logo" alt="SYOS Logo" />
                </div>
            </div>
        </>
    );
}

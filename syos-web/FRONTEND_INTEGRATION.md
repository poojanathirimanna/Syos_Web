# Frontend Integration - Quick Reference

## 🎯 Copy-Paste Code for Your Login Component

### Option 1: Using if-else statements
```javascript
const submit = async (e) => {
    e.preventDefault();
    setMsg("");

    try {
        const res = await apiLogin(username, password);
        if (res.ok) {
            // Role-based redirection
            if (res.roleId === 1) {
                // Main Manager
                nav("/admin/dashboard");
            } else if (res.roleId === 2) {
                // Cashier
                nav("/cashier/dashboard");
            } else if (res.roleId === 3) {
                // Customer
                nav("/customer/dashboard");
            } else {
                // Fallback
                nav("/home");
            }
        } else {
            setMsg(res.message);
        }
    } catch {
        setMsg("Server error");
    }
};
```

### Option 2: Using switch statement (cleaner)
```javascript
const submit = async (e) => {
    e.preventDefault();
    setMsg("");

    try {
        const res = await apiLogin(username, password);
        if (res.ok) {
            // Role-based redirection
            switch(res.roleId) {
                case 1:
                    nav("/admin/dashboard");  // Main Manager
                    break;
                case 2:
                    nav("/cashier/dashboard");  // Cashier
                    break;
                case 3:
                    nav("/customer/dashboard");  // Customer
                    break;
                default:
                    nav("/home");  // Fallback
            }
        } else {
            setMsg(res.message);
        }
    } catch {
        setMsg("Server error");
    }
};
```

### Google Login Handler
```javascript
const handleGoogleSuccess = async (credentialResponse) => {
    setMsg("");
    try {
        const res = await apiGoogleLogin(credentialResponse.credential);
        if (res.ok) {
            // Same role-based redirection as regular login
            switch(res.roleId) {
                case 1:
                    nav("/admin/dashboard");
                    break;
                case 2:
                    nav("/cashier/dashboard");
                    break;
                case 3:
                    nav("/customer/dashboard");
                    break;
                default:
                    nav("/home");
            }
        } else {
            setMsg(res.message || "Google login failed");
        }
    } catch (error) {
        setMsg("Server error during Google login");
    }
};
```

---

## 🔄 Full Example with User Context (Advanced)

If you want to store user info globally:

```javascript
// In Login.jsx or your login component
const submit = async (e) => {
    e.preventDefault();
    setMsg("");

    try {
        const res = await apiLogin(username, password);
        if (res.ok) {
            // Store user information
            const userInfo = {
                username: res.username,
                fullName: res.fullName,
                email: res.email,
                roleId: res.roleId
            };
            
            // Option A: Use localStorage (persists across sessions)
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            
            // Option B: Use Context API or Redux
            // setUser(userInfo);
            
            // Role-based redirection
            switch(res.roleId) {
                case 1:
                    nav("/admin/dashboard");
                    break;
                case 2:
                    nav("/cashier/dashboard");
                    break;
                case 3:
                    nav("/customer/dashboard");
                    break;
                default:
                    nav("/home");
            }
        } else {
            setMsg(res.message);
        }
    } catch {
        setMsg("Server error");
    }
};
```

---

## 📊 API Response Structure

### Successful Login Response
```json
{
  "ok": true,
  "username": "johndoe",
  "roleId": 1,
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

### Failed Login Response
```json
{
  "ok": false,
  "message": "Invalid credentials"
}
```

---

## 🛣️ Recommended Dashboard Routes

Based on role IDs, create these routes in your React Router:

```javascript
// In your App.jsx or Routes.jsx
<Routes>
  {/* Public Routes */}
  <Route path="/" element={<Login />} />
  <Route path="/register" element={<Register />} />
  
  {/* Role-specific Dashboards */}
  <Route path="/admin/dashboard" element={<AdminDashboard />} />
  <Route path="/cashier/dashboard" element={<CashierDashboard />} />
  <Route path="/customer/dashboard" element={<CustomerDashboard />} />
  
  {/* Fallback */}
  <Route path="/home" element={<Home />} />
</Routes>
```

---

## 🔐 Protected Route Example

```javascript
// ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  
  if (!userInfo.roleId) {
    // Not logged in
    return <Navigate to="/" replace />;
  }
  
  if (requiredRole && userInfo.roleId !== requiredRole) {
    // Wrong role - redirect to their dashboard
    switch(userInfo.roleId) {
      case 1: return <Navigate to="/admin/dashboard" replace />;
      case 2: return <Navigate to="/cashier/dashboard" replace />;
      case 3: return <Navigate to="/customer/dashboard" replace />;
      default: return <Navigate to="/" replace />;
    }
  }
  
  return children;
};

// Usage in Routes
<Route 
  path="/admin/dashboard" 
  element={
    <ProtectedRoute requiredRole={1}>
      <AdminDashboard />
    </ProtectedRoute>
  } 
/>
```

---

## 🎨 Dashboard Pages (Create These)

### 1. AdminDashboard.jsx (Main Manager)
```javascript
export default function AdminDashboard() {
  return (
    <div>
      <h1>Main Manager Dashboard</h1>
      <p>Manage everything from here!</p>
      {/* Add admin-specific features */}
    </div>
  );
}
```

### 2. CashierDashboard.jsx
```javascript
export default function CashierDashboard() {
  return (
    <div>
      <h1>Cashier Dashboard</h1>
      <p>Process transactions and manage sales</p>
      {/* Add cashier-specific features */}
    </div>
  );
}
```

### 3. CustomerDashboard.jsx
```javascript
export default function CustomerDashboard() {
  return (
    <div>
      <h1>Customer Dashboard</h1>
      <p>Welcome! Browse and shop</p>
      {/* Add customer-specific features */}
    </div>
  );
}
```

---

## ✅ Checklist

- [ ] Update `Login.jsx` with role-based redirection
- [ ] Update Google login handler with role-based redirection
- [ ] Create `AdminDashboard.jsx` for Main Manager (roleId = 1)
- [ ] Create `CashierDashboard.jsx` for Cashier (roleId = 2)
- [ ] Create `CustomerDashboard.jsx` for Customer (roleId = 3)
- [ ] Add routes for each dashboard in your router
- [ ] (Optional) Implement protected routes
- [ ] Test login with each role type
- [ ] Deploy and verify

---

**That's it! The backend is ready. Just copy the code above and adjust the dashboard paths to match your app structure.**


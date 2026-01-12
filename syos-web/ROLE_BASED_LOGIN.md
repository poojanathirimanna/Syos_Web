# Role-Based Login Implementation

## Overview
The login system now returns user role information to enable role-based dashboard redirection.

## Role IDs
- **1** = Main Manager
- **2** = Cashier
- **3** = Customer (default for new registrations)

---

## Backend Changes

### 1. UserDao.java
**Added:**
- `getUserDetails(String userId)` - Fetches complete user information including role_id
- `UserDetails` inner class - Holds user data (userId, fullName, email, contactNumber, roleId)

### 2. LoginUseCase.java
**Updated:**
- Now fetches complete user details after successful authentication
- Returns UserDTO with all fields including roleId

### 3. ApiLoginServlet.java
**Updated:**
- Returns roleId, fullName, and email in the login response
- Stores roleId and fullName in session

**Response Format:**
```json
{
  "ok": true,
  "username": "user123",
  "roleId": 1,
  "fullName": "John Doe",
  "email": "john@example.com"
}
```

### 4. ApiGoogleLoginServlet.java
**Updated:**
- Returns roleId and user details for both existing and new users
- Stores roleId and fullName in session

---

## Frontend Integration

### Updated Login Response
When a user logs in successfully, the response now includes:
- `ok`: boolean (success/failure)
- `username`: string
- `roleId`: integer (1, 2, or 3)
- `fullName`: string
- `email`: string

### Example Frontend Implementation

```javascript
// In Login.jsx or your login component
const submit = async (e) => {
    e.preventDefault();
    setMsg("");

    try {
        const res = await apiLogin(username, password);
        if (res.ok) {
            // Role-based redirection
            switch(res.roleId) {
                case 1:
                    // Main Manager
                    nav("/admin/dashboard");
                    break;
                case 2:
                    // Cashier
                    nav("/cashier/dashboard");
                    break;
                case 3:
                    // Customer
                    nav("/customer/dashboard");
                    break;
                default:
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

### Google Login Integration
```javascript
const handleGoogleSuccess = async (credentialResponse) => {
    setMsg("");
    try {
        const res = await apiGoogleLogin(credentialResponse.credential);
        if (res.ok) {
            // Role-based redirection (same as above)
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

## Session Storage
The following data is now stored in the HTTP session:
- `username` - User ID
- `roleId` - User's role ID (1, 2, or 3)
- `fullName` - User's full name

This can be used for authorization checks in other servlets/filters.

---

## Database Schema
Ensure your `users` table has the following structure:
```sql
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    contact_number VARCHAR(20),
    password_hash VARCHAR(255),
    google_id VARCHAR(255) UNIQUE,
    role_id INT NOT NULL DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Testing

### Test Cases
1. **Login as Main Manager (roleId = 1)**
   - Should redirect to `/admin/dashboard` or main manager page

2. **Login as Cashier (roleId = 2)**
   - Should redirect to `/cashier/dashboard` or cashier page

3. **Login as Customer (roleId = 3)**
   - Should redirect to `/customer/dashboard` or customer page

4. **New Google Registration**
   - Should automatically get roleId = 3 (Customer)
   - Should redirect to customer dashboard

### Sample Test Data
```sql
-- Create test users
INSERT INTO users (user_id, full_name, email, contact_number, password_hash, role_id)
VALUES 
    ('manager1', 'John Manager', 'manager@test.com', '1234567890', '$2a$10$...', 1),
    ('cashier1', 'Jane Cashier', 'cashier@test.com', '0987654321', '$2a$10$...', 2),
    ('customer1', 'Bob Customer', 'customer@test.com', '5555555555', '$2a$10$...', 3);
```

---

## Security Notes
- Role information is validated server-side
- Session stores roleId for authorization checks
- Always verify user permissions in backend endpoints, not just frontend
- Consider implementing role-based access control (RBAC) filters for protected routes

---

## Next Steps
1. Update frontend Login component with role-based redirection
2. Create separate dashboard pages for each role
3. Implement authorization filters to protect role-specific endpoints
4. Add role-based navigation menus


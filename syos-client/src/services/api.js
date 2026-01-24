// Base URL of your backend (Tomcat)
const BASE_URL = "http://localhost:8081/syos_web_war_exploded";

// Safely parse JSON (prevents crashes if server returns text/HTML)
async function parseJsonSafe(response) {
    const text = await response.text();
    try {
        return JSON.parse(text);
    } catch {
        return { success: false, message: text || "Invalid server response" };
    }
}

/* =========================
   AUTH APIs
   ========================= */

// LOGIN
export async function apiLogin(username, password) {
    const res = await fetch(`${BASE_URL}/api/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include", // IMPORTANT: send session cookies
        body: JSON.stringify({ username, password }),
    });

    return await parseJsonSafe(res);
}

// REGISTER
export async function apiRegister({ user_id, full_name, email, contact_number, password }) {
    const res = await fetch(`${BASE_URL}/api/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
            user_id,
            full_name,
            email,
            contact_number,
            password,
        }),
    });

    return await parseJsonSafe(res);
}

// GOOGLE LOGIN
export async function apiGoogleLogin(credential) {
    const res = await fetch(`${BASE_URL}/api/google-login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ credential }),
    });

    return await parseJsonSafe(res);
}

// CHECK SESSION
export async function apiMe() {
    const res = await fetch(`${BASE_URL}/api/me`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// LOGOUT
export async function apiLogout() {
    const res = await fetch(`${BASE_URL}/api/logout`, {
        method: "POST",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

/* =========================
   PRODUCT APIs
   ========================= */

// GET ALL PRODUCTS (List catalog)
export async function apiGetProducts() {
    try {
        console.log("📦 Calling API: GET /api/admin/products");
        const res = await fetch(`${BASE_URL}/api/admin/products`, {
            method: "GET",
            credentials: "include",
        });

        console.log("📦 API Response status:", res.status);
        const result = await parseJsonSafe(res);
        console.log("📦 API Response data:", result);
        return result;
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

// GET SINGLE PRODUCT
export async function apiGetProduct(code) {
    const res = await fetch(`${BASE_URL}/api/admin/products/${code}`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// CREATE NEW PRODUCT
export async function apiCreateProduct(productData) {
    const res = await fetch(`${BASE_URL}/api/admin/products`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(productData),
    });

    return await parseJsonSafe(res);
}

// UPDATE EXISTING PRODUCT
export async function apiUpdateProduct(productData) {
    const res = await fetch(`${BASE_URL}/api/admin/products`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(productData),
    });

    return await parseJsonSafe(res);
}

// DELETE PRODUCT (Soft delete)
export async function apiDeleteProduct(code) {
    const res = await fetch(`${BASE_URL}/api/admin/products/${code}`, {
        method: "DELETE",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

/* =========================
   CATEGORY APIs
   ========================= */

// GET ALL CATEGORIES
export async function apiGetCategories() {
    try {
        const res = await fetch(`${BASE_URL}/api/admin/categories`, {
            method: "GET",
            credentials: "include",
        });

        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}



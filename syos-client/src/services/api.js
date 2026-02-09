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

// CREATE CATEGORY
export async function apiCreateCategory(categoryData) {
    const res = await fetch(`${BASE_URL}/api/admin/categories`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(categoryData),
    });

    return await parseJsonSafe(res);
}

// UPDATE CATEGORY
export async function apiUpdateCategory(categoryData) {
    const res = await fetch(`${BASE_URL}/api/admin/categories`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(categoryData),
    });

    return await parseJsonSafe(res);
}

// DELETE CATEGORY
export async function apiDeleteCategory(categoryId) {
    const res = await fetch(`${BASE_URL}/api/admin/categories/${categoryId}`, {
        method: "DELETE",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

/* =========================
   INVENTORY APIs
   ========================= */

// GET INVENTORY (with optional filters)
export async function apiGetInventory(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const url = queryString 
        ? `${BASE_URL}/api/admin/inventory?${queryString}` 
        : `${BASE_URL}/api/admin/inventory`;
    
    const res = await fetch(url, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// RECEIVE STOCK
export async function apiReceiveStock(data) {
    const res = await fetch(`${BASE_URL}/api/admin/inventory/receive`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
    });

    return await parseJsonSafe(res);
}

// TRANSFER STOCK
export async function apiTransferStock(data) {
    console.log('🔧 API: apiTransferStock called with:', data);
    
    const res = await fetch(`${BASE_URL}/api/admin/inventory/transfer`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
    });

    console.log('📡 API: Transfer response status:', res.status, res.statusText);
    
    const result = await parseJsonSafe(res);
    console.log('📦 API: Transfer parsed response:', result);
    
    return result;
}

/* =========================
   CASHIER APIs
   ========================= */

// GET ALL CASHIERS
export async function apiGetCashiers() {
    const res = await fetch(`${BASE_URL}/api/admin/cashiers`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET SINGLE CASHIER
export async function apiGetCashier(userId) {
    const res = await fetch(`${BASE_URL}/api/admin/cashiers/${userId}`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// CREATE CASHIER
export async function apiCreateCashier(data) {
    const res = await fetch(`${BASE_URL}/api/admin/cashiers`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
    });

    return await parseJsonSafe(res);
}

// UPDATE CASHIER
export async function apiUpdateCashier(data) {
    const res = await fetch(`${BASE_URL}/api/admin/cashiers`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(data),
    });

    return await parseJsonSafe(res);
}

// DEACTIVATE CASHIER
export async function apiDeactivateCashier(userId) {
    const res = await fetch(`${BASE_URL}/api/admin/cashiers/${userId}`, {
        method: "DELETE",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

/* =========================
   BILLS/ORDERS APIs
   ========================= */

// GET ALL BILLS (Admin/Manager)
export async function apiGetAllBills() {
    const res = await fetch(`${BASE_URL}/api/admin/bills`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET CASHIER BILLS (Cashier - own bills only)
export async function apiGetCashierBills() {
    const res = await fetch(`${BASE_URL}/api/cashier/bills`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET BILL DETAILS
export async function apiGetBillDetails(billNumber) {
    const res = await fetch(`${BASE_URL}/api/cashier/bills/${billNumber}`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// CREATE BILL (Cashier)
export async function apiCreateBill(billData) {
    console.log('🔧 API: apiCreateBill called with data:', billData);
    
    const res = await fetch(`${BASE_URL}/api/cashier/bills`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(billData),
    });

    console.log('📡 API: Response status:', res.status, res.statusText);
    
    const result = await parseJsonSafe(res);
    console.log('📦 API: Parsed response:', result);
    
    return result;
}

/* =========================
   DISCOUNT & PROMOTIONS APIs
   ========================= */

// SET PRODUCT DISCOUNT
export async function apiSetProductDiscount(discountData) {
    const res = await fetch(`${BASE_URL}/api/admin/products/discount`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(discountData),
    });

    return await parseJsonSafe(res);
}

// REMOVE PRODUCT DISCOUNT
export async function apiRemoveProductDiscount(productCode) {
    const res = await fetch(`${BASE_URL}/api/admin/products/discount/${productCode}`, {
        method: "DELETE",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// SET BATCH DISCOUNT
export async function apiSetBatchDiscount(discountData) {
    const res = await fetch(`${BASE_URL}/api/admin/batches/discount`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(discountData),
    });

    return await parseJsonSafe(res);
}

// REMOVE BATCH DISCOUNT
export async function apiRemoveBatchDiscount(batchId) {
    const res = await fetch(`${BASE_URL}/api/admin/batches/discount/${batchId}`, {
        method: "DELETE",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET CASHIER PROMOTIONS (Active discounts for cashiers to view)
export async function apiGetCashierPromotions() {
    const res = await fetch(`${BASE_URL}/api/cashier/promotions`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET ALL PRODUCTS WITH DISCOUNTS (For discount management)
export async function apiGetProductsWithDiscounts() {
    const res = await fetch(`${BASE_URL}/api/admin/products/discounts`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

// GET PRODUCT BATCHES (For batch discount management)
export async function apiGetProductBatches(productCode) {
    const res = await fetch(`${BASE_URL}/api/admin/batches?productCode=${productCode}`, {
        method: "GET",
        credentials: "include",
    });

    return await parseJsonSafe(res);
}

/* =========================
   CUSTOMER SHOPPING APIs
   ========================= */

// GET CUSTOMER PRODUCTS (with discounts applied)
export async function apiGetCustomerProducts(params = {}) {
    try {
        // Build query string from params
        const queryParams = new URLSearchParams();
        if (params.category) queryParams.append('category', params.category);
        if (params.search) queryParams.append('search', params.search);
        if (params.minPrice) queryParams.append('minPrice', params.minPrice);
        if (params.maxPrice) queryParams.append('maxPrice', params.maxPrice);
        if (params.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params.page) queryParams.append('page', params.page);
        if (params.limit) queryParams.append('limit', params.limit);
        
        const queryString = queryParams.toString();
        const url = `${BASE_URL}/api/customer/products${queryString ? '?' + queryString : ''}`;
        
        const res = await fetch(url, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

// GET CUSTOMER PRODUCT DETAILS
export async function apiGetCustomerProduct(productCode) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/products/${productCode}`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// GET CUSTOMER CATEGORIES
export async function apiGetCustomerCategories() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/categories`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

/* =========================
   SHOPPING CART APIs
   ========================= */

// GET CART
export async function apiGetCart() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/cart`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: { items: [], subtotal: 0, totalDiscount: 0, total: 0 } };
    }
}

// ADD TO CART
export async function apiAddToCart(productCode, quantity = 1) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/cart`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ productCode, quantity }),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// UPDATE CART ITEM
export async function apiUpdateCartItem(cartId, quantity) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/cart/${cartId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ quantity }),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// REMOVE CART ITEM
export async function apiRemoveCartItem(cartId) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/cart/${cartId}`, {
            method: "DELETE",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// CLEAR CART
export async function apiClearCart() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/cart`, {
            method: "DELETE",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

/* =========================
   CUSTOMER CHECKOUT & ORDERS APIs
   ========================= */

// CHECKOUT (Place Order)
export async function apiCheckout(orderData) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/checkout`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(orderData),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// GET CUSTOMER ORDERS
export async function apiGetCustomerOrders() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/orders`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

// GET ORDER DETAILS
export async function apiGetOrderDetails(billNumber) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/orders/${billNumber}`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// CANCEL ORDER
export async function apiCancelOrder(billNumber) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/orders/${billNumber}/cancel`, {
            method: "PUT",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

/* =========================
   WISHLIST APIs
   ========================= */

// GET WISHLIST
export async function apiGetWishlist() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/wishlist`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

// ADD TO WISHLIST
export async function apiAddToWishlist(productCode) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/wishlist`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ productCode }),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// REMOVE FROM WISHLIST
export async function apiRemoveFromWishlist(productCode) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/wishlist/${productCode}`, {
            method: "DELETE",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

/* =========================
   ADDRESS MANAGEMENT APIs
   ========================= */

// GET ADDRESSES
export async function apiGetAddresses() {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/addresses`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [] };
    }
}

// ADD ADDRESS
export async function apiAddAddress(addressData) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/addresses`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(addressData),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// UPDATE ADDRESS
export async function apiUpdateAddress(addressId, addressData) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/addresses/${addressId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(addressData),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

// DELETE ADDRESS
export async function apiDeleteAddress(addressId) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/addresses/${addressId}`, {
            method: "DELETE",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

/* =========================
   PRODUCT REVIEWS APIs
   ========================= */

// GET PRODUCT REVIEWS
export async function apiGetProductReviews(productCode) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/reviews/${productCode}`, {
            method: "GET",
            credentials: "include",
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message, data: [], averageRating: 0, totalReviews: 0 };
    }
}

// ADD PRODUCT REVIEW
export async function apiAddProductReview(reviewData) {
    try {
        const res = await fetch(`${BASE_URL}/api/customer/reviews`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(reviewData),
        });
        return await parseJsonSafe(res);
    } catch (error) {
        console.error("❌ API Error:", error);
        return { success: false, message: error.message };
    }
}

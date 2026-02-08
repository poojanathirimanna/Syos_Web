# 📚 SYOS Web API - Complete Endpoints Documentation

## 🎯 All API Endpoints with Request Bodies

---

## 🔐 Authentication APIs

### 1. Login
**POST** `/api/auth/login`
```json
{
  "username": "customer1",
  "password": "password123"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "customer1",
    "userType": "CUSTOMER",
    "fullName": "John Doe"
  }
}
```

---

### 2. Register
**POST** `/api/auth/register`
```json
{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "fullName": "John Doe",
  "phoneNumber": "0771234567",
  "userType": "CUSTOMER"
}
```

---

### 3. Google Login
**POST** `/api/auth/google-login`
```json
{
  "credential": "google_oauth_token_here"
}
```

---

### 4. Get Current User
**GET** `/api/auth/me`
No request body needed.

---

### 5. Logout
**POST** `/api/auth/logout`
No request body needed.

---

## 🛒 Customer APIs

### 6. Customer Checkout (NEW! 🆕)
**POST** `/api/customer/checkout`
```json
{
  "items": [
    {
      "productCode": "P001",
      "quantity": 2
    },
    {
      "productCode": "P002",
      "quantity": 1
    }
  ],
  "paymentMethod": "CREDIT_CARD",
  "deliveryAddress": "123 Main Street, Apartment 4B",
  "deliveryCity": "Colombo",
  "deliveryPostalCode": "10100",
  "deliveryPhone": "0771234567",
  "paymentMethodDetails": "{\"cardType\":\"Visa\",\"cardNumber\":\"****1234\",\"expiryDate\":\"12/25\"}"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Order placed successfully! 🎉",
  "data": {
    "billNumber": "BILL-20260208-000001",
    "trackingNumber": "TRACK-20260208-ABC12345",
    "orderStatus": "PENDING",
    "estimatedDeliveryDate": "2026-02-13",
    "totalAmount": 1500.00
  }
}
```

---

### 7. Get All Customer Orders (NEW! 🆕)
**GET** `/api/customer/orders`
No request body needed.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "billNumber": "BILL-20260208-000001",
      "orderStatus": "PENDING",
      "totalAmount": 1500.00,
      "trackingNumber": "TRACK-20260208-ABC12345"
    }
  ]
}
```

---

### 8. Get Single Order Details (NEW! 🆕)
**GET** `/api/customer/orders/{billNumber}`
No request body needed.

---

### 9. Cancel Order (NEW! 🆕)
**PUT** `/api/customer/orders/{billNumber}/cancel`
No request body needed.

---

### 10. Get Customer Products
**GET** `/api/customer/products`
**GET** `/api/customer/products/{productCode}`
No request body needed.

---

### 11. Add to Cart
**POST** `/api/customer/cart`
```json
{
  "productCode": "P001",
  "quantity": 2
}
```

---

### 12. Get Cart
**GET** `/api/customer/cart`
No request body needed.

---

### 13. Update Cart Item
**PUT** `/api/customer/cart/{cartId}`
```json
{
  "quantity": 5
}
```

---

### 14. Remove from Cart
**DELETE** `/api/customer/cart/{cartId}`
No request body needed.

---

### 15. Clear Cart
**DELETE** `/api/customer/cart`
No request body needed.

---

### 16. Add to Wishlist
**POST** `/api/customer/wishlist`
```json
{
  "productCode": "P001"
}
```

---

### 17. Get Wishlist
**GET** `/api/customer/wishlist`
No request body needed.

---

### 18. Remove from Wishlist
**DELETE** `/api/customer/wishlist/{productCode}`
No request body needed.

---

### 19. Add Review
**POST** `/api/customer/reviews`
```json
{
  "productCode": "P001",
  "rating": 5,
  "comment": "Excellent product!"
}
```

---

### 20. Get Product Reviews
**GET** `/api/customer/reviews/{productCode}`
No request body needed.

---

### 21. Get Customer Addresses
**GET** `/api/customer/addresses`
No request body needed.

---

### 22. Add Address
**POST** `/api/customer/addresses`
```json
{
  "addressLine1": "123 Main Street",
  "addressLine2": "Apartment 4B",
  "city": "Colombo",
  "postalCode": "10100",
  "phoneNumber": "0771234567",
  "isDefault": true
}
```

---

### 23. Update Address
**PUT** `/api/customer/addresses/{addressId}`
```json
{
  "addressLine1": "456 New Street",
  "city": "Kandy",
  "postalCode": "20000",
  "phoneNumber": "0772345678",
  "isDefault": false
}
```

---

### 24. Delete Address
**DELETE** `/api/customer/addresses/{addressId}`
No request body needed.

---

## 💰 Cashier APIs

### 25. Create Bill (Cashier Checkout)
**POST** `/api/cashier/bills`
```json
{
  "items": [
    {
      "productCode": "P001",
      "quantity": 2
    }
  ],
  "paymentMethod": "CASH",
  "amountPaid": 2000.00
}
```
**Response:**
```json
{
  "success": true,
  "data": {
    "billNumber": "BILL-20260208-000001",
    "totalAmount": 1500.00,
    "changeAmount": 500.00
  }
}
```

---

### 26. Get Cashier Bills
**GET** `/api/cashier/bills`
No request body needed.

---

### 27. Get Bill Details
**GET** `/api/cashier/bills/{billNumber}`
No request body needed.

---

### 28. Get Available Products (Cashier)
**GET** `/api/cashier/available-products`
No request body needed.

---

### 29. Get Active Promotions (Cashier)
**GET** `/api/cashier/promotions`
No request body needed.

---

## 👨‍💼 Admin APIs

### 30. Queue Stats (NEW! 🆕)
**GET** `/api/admin/queue-stats`
No request body needed.

**Response:**
```json
{
  "success": true,
  "data": {
    "queueSize": 5,
    "remainingCapacity": 995,
    "totalCapacity": 1000,
    "numWorkers": 20,
    "utilizationPercent": 0.5,
    "status": "LOW"
  }
}
```

---

### 31. Get All Products
**GET** `/api/admin/products`
No request body needed.

---

### 32. Get Single Product
**GET** `/api/admin/products/{productCode}`
No request body needed.

---

### 33. Create Product
**POST** `/api/admin/products`
```json
{
  "productCode": "P001",
  "name": "Product Name",
  "description": "Product description",
  "unitPrice": 500.00,
  "categoryId": 1,
  "imageUrl": "/images/product.jpg",
  "reorderLevel": 10,
  "maxStockLevel": 100
}
```

---

### 34. Update Product
**PUT** `/api/admin/products/{productCode}`
```json
{
  "name": "Updated Product Name",
  "unitPrice": 550.00,
  "description": "Updated description"
}
```

---

### 35. Delete Product
**DELETE** `/api/admin/products/{productCode}`
No request body needed.

---

### 36. Apply Product Discount
**POST** `/api/admin/products/discount/{productCode}`
```json
{
  "discountPercentage": 20,
  "startDate": "2026-02-08",
  "endDate": "2026-02-28"
}
```

---

### 37. Remove Product Discount
**DELETE** `/api/admin/products/discount/{productCode}`
No request body needed.

---

### 38. Get Inventory
**GET** `/api/admin/inventory`
No request body needed.

---

### 39. Receive Stock
**POST** `/api/admin/inventory/receive`
```json
{
  "productCode": "P001",
  "quantity": 100,
  "batchNumber": "BATCH-001",
  "expiryDate": "2027-12-31",
  "purchasePrice": 400.00,
  "supplierId": "SUP001",
  "receivedBy": "admin1"
}
```

---

### 40. Transfer Stock
**POST** `/api/admin/inventory/transfer`
```json
{
  "productCode": "P001",
  "batchId": 5,
  "quantity": 20,
  "fromLocation": "STORAGE",
  "toLocation": "SHELF",
  "userId": "admin1"
}
```

---

### 41. Get All Cashiers
**GET** `/api/admin/cashiers`
No request body needed.

---

### 42. Create Cashier
**POST** `/api/admin/cashiers`
```json
{
  "username": "cashier1",
  "password": "password123",
  "fullName": "Jane Smith",
  "email": "cashier1@example.com",
  "phoneNumber": "0771234567"
}
```

---

### 43. Update Cashier
**PUT** `/api/admin/cashiers/{cashierId}`
```json
{
  "fullName": "Jane Doe",
  "email": "jane.doe@example.com",
  "phoneNumber": "0772345678"
}
```

---

### 44. Delete Cashier
**DELETE** `/api/admin/cashiers/{cashierId}`
No request body needed.

---

## 📊 Summary by Method

### GET Requests (No Body)
- `/api/auth/me`
- `/api/customer/orders`
- `/api/customer/orders/{billNumber}`
- `/api/customer/products`
- `/api/customer/products/{productCode}`
- `/api/customer/cart`
- `/api/customer/wishlist`
- `/api/customer/reviews/{productCode}`
- `/api/customer/addresses`
- `/api/cashier/bills`
- `/api/cashier/bills/{billNumber}`
- `/api/cashier/available-products`
- `/api/cashier/promotions`
- `/api/admin/queue-stats` 🆕
- `/api/admin/products`
- `/api/admin/products/{productCode}`
- `/api/admin/inventory`
- `/api/admin/cashiers`

### POST Requests (With Body)
- `/api/auth/login` - Login credentials
- `/api/auth/register` - User registration
- `/api/auth/logout` - No body
- `/api/auth/google-login` - Google credential
- `/api/customer/checkout` 🆕 - Order items + delivery info
- `/api/customer/cart` - Product code + quantity
- `/api/customer/wishlist` - Product code
- `/api/customer/reviews` - Rating + comment
- `/api/customer/addresses` - Address details
- `/api/cashier/bills` - Bill items + payment
- `/api/admin/products` - Product details
- `/api/admin/products/discount/{productCode}` - Discount details
- `/api/admin/inventory/receive` - Stock receipt
- `/api/admin/inventory/transfer` - Transfer details
- `/api/admin/cashiers` - Cashier details

### PUT Requests (With Body)
- `/api/customer/orders/{billNumber}/cancel` 🆕 - No body
- `/api/customer/cart/{cartId}` - New quantity
- `/api/customer/addresses/{addressId}` - Updated address
- `/api/admin/products/{productCode}` - Updated product
- `/api/admin/cashiers/{cashierId}` - Updated cashier

### DELETE Requests (No Body)
- `/api/customer/cart/{cartId}`
- `/api/customer/cart` - Clear all
- `/api/customer/wishlist/{productCode}`
- `/api/customer/addresses/{addressId}`
- `/api/admin/products/{productCode}`
- `/api/admin/products/discount/{productCode}`
- `/api/admin/cashiers/{cashierId}`

---

## 🆕 New Customer Checkout Endpoints

These are the 4 new endpoints we just implemented:

1. **POST** `/api/customer/checkout` - Place order
2. **GET** `/api/customer/orders` - List all orders
3. **GET** `/api/customer/orders/{billNumber}` - Order details
4. **PUT** `/api/customer/orders/{billNumber}/cancel` - Cancel order
5. **GET** `/api/admin/queue-stats` - Monitor queue

---

## 🔑 Authentication

All endpoints (except `/api/auth/login` and `/api/auth/register`) require:
- Valid session cookie (JSESSIONID)
- Obtained after successful login
- Automatically handled by browser/Postman

---

## 📝 Common Response Format

### Success Response:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response data */ }
}
```

### Error Response:
```json
{
  "success": false,
  "message": "Error message here",
  "data": null
}
```

---

## 🎯 Quick Reference for Customer Checkout

### Complete Flow:
1. **Login:** `POST /api/auth/login`
2. **Browse Products:** `GET /api/customer/products`
3. **Add to Cart:** `POST /api/customer/cart`
4. **View Cart:** `GET /api/customer/cart`
5. **Checkout:** `POST /api/customer/checkout` 🆕
6. **View Orders:** `GET /api/customer/orders` 🆕
7. **Track Order:** `GET /api/customer/orders/{billNumber}` 🆕
8. **Cancel if needed:** `PUT /api/customer/orders/{billNumber}/cancel` 🆕

---

**Total Endpoints: 44+**
**New Endpoints: 5** 🎉


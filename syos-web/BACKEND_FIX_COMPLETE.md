# 🎯 COMPLETE FIX: Customer Products Backend Inventory Issue

## 📋 Summary
Fixed the backend to correctly query **WEBSITE location** inventory for customer orders instead of SHELF inventory.

---

## ✅ Files Modified

### 1. **ApiCustomerProductsServlet.java**
- **Location**: `src/main/java/com/syos/web/presentation/api/customer/ApiCustomerProductsServlet.java`
- **Changes**: 
  - Line ~178: Changed browse products to use `getWebsiteQuantity()` instead of `getShelfQuantity()`
  - Line ~246: Changed product details to use `getWebsiteQuantity()` instead of `getShelfQuantity()`

### 2. **AddToCartUseCase.java**
- **Location**: `src/main/java/com/syos/web/application/usecases/AddToCartUseCase.java`
- **Changes**:
  - Line ~47: Changed stock availability check to use `getWebsiteQuantity()` instead of `getShelfQuantity()`

### 3. **CreateBillUseCase.java**
- **Location**: `src/main/java/com/syos/web/application/usecases/CreateBillUseCase.java`
- **Changes**:
  - Line ~78: Added conditional check to use `getWebsiteQuantity()` for ONLINE orders, `getShelfQuantity()` for IN_STORE
  - Line ~191: Updated to deduct from correct location (WEBSITE for ONLINE, SHELF for IN_STORE)

### 4. **BillDao.java**
- **Location**: `src/main/java/com/syos/web/infrastructure/persistence/dao/BillDao.java`
- **Changes**:
  - Added overloaded `deductStockForSale()` method that accepts location parameter
  - Calls new stored procedure `deduct_stock_for_sale_v2` that supports SHELF and WEBSITE locations

---

## 🗄️ Database Migration Required

### **NEW Stored Procedure Created**
**File**: `database_migration_v2.sql` (created in project root)

This SQL script creates a new stored procedure `deduct_stock_for_sale_v2` that:
- Accepts a location parameter (SHELF or WEBSITE)
- Deducts inventory from the specified location using FEFO (First Expire First Out)
- Uses optimistic locking to prevent race conditions
- Logs stock movements correctly

**YOU MUST RUN THIS SQL SCRIPT BEFORE TESTING!**

---

## 🚀 DEPLOYMENT STEPS (CRITICAL!)

### Step 1: Run Database Migration
```sql
-- Connect to your MySQL database
mysql -u root -p syos_db

-- Run the migration script
source C:\Users\USER\Desktop\3rd Year\CCCP\CCCP2\SYOS\syos-web\database_migration_v2.sql

-- OR copy/paste the contents from the file
```

### Step 2: Rebuild Backend
1. Open IntelliJ IDEA
2. **Build → Rebuild Project** (Ctrl+Shift+F9)
3. Wait for compilation to complete

### Step 3: Restart Tomcat
1. **Stop Tomcat** server
2. **Clean** the deployment:
   - Delete `webapps/syos-web` folder in Tomcat
   - Delete `syos-web.war` in Tomcat webapps
3. **Redeploy**:
   - Build WAR: `Build → Build Artifacts → syos-web:war → Build`
   - Copy WAR to Tomcat webapps folder
4. **Start Tomcat**

### Step 4: Verify Database
Check that inventory exists in WEBSITE location:
```sql
SELECT * FROM inventory_locations 
WHERE product_code = 'apple001' AND location = 'WEBSITE';
```

Expected result:
```
product_code | location | quantity
apple001     | WEBSITE  | 1000
```

---

## 🧪 TESTING

### Test 1: Customer Product Listing
```bash
GET http://localhost:8080/api/customer/products
```

**Expected Response** for apple001:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "productCode": "apple001",
        "name": "Apple",
        "availableQuantity": 1000,  // ✅ Should show 1000 (from WEBSITE)
        "inStock": true,
        ...
      }
    ]
  }
}
```

### Test 2: Add to Cart
```bash
POST http://localhost:8080/api/customer/cart
Content-Type: application/json

{
  "productCode": "apple001",
  "quantity": 5
}
```

**Expected**: Should succeed (checks WEBSITE inventory)

### Test 3: Customer Checkout
```bash
POST http://localhost:8080/api/customer/checkout
Content-Type: application/json

{
  "items": [
    {"productCode": "apple001", "quantity": 10}
  ],
  "paymentMethod": "CREDIT_CARD",
  "deliveryAddress": "123 Test St",
  ...
}
```

**Expected**: 
- Order created successfully
- Inventory deducted from WEBSITE location
- SHELF location remains unchanged

### Test 4: Verify Inventory Deduction
After placing an order for 10 apples:
```sql
SELECT * FROM inventory_locations 
WHERE product_code = 'apple001' AND location = 'WEBSITE';
```

Expected:
```
quantity: 990  (was 1000, deducted 10)
```

---

## 🔍 How It Works Now

### Customer Flow (ONLINE Channel)
```
Customer → Browse Products → Shows WEBSITE inventory ✅
         → Add to Cart     → Checks WEBSITE inventory ✅
         → Checkout        → Deducts from WEBSITE    ✅
```

### Cashier Flow (IN_STORE Channel)
```
Cashier → POS System → Shows SHELF inventory ✅
        → Create Bill → Deducts from SHELF    ✅
```

### Inventory Location Logic
| User Type | Channel   | Reads From | Deducts From |
|-----------|-----------|------------|--------------|
| Customer  | ONLINE    | WEBSITE    | WEBSITE      |
| Cashier   | IN_STORE  | SHELF      | SHELF        |

---

## 🐛 Troubleshooting

### Issue 1: Still showing availableQuantity: 0
**Solution**: 
- Make sure you ran the database migration
- Restart Tomcat server
- Clear browser cache and refresh

### Issue 2: Error "deduct_stock_for_sale_v2 does not exist"
**Solution**: 
- Run the SQL migration script: `database_migration_v2.sql`
- Check MySQL connection and permissions

### Issue 3: Insufficient stock error when WEBSITE has stock
**Solution**:
- Check that the channel is set to "ONLINE" in the request
- Verify WEBSITE location has inventory in database
- Check backend logs for which location it's checking

### Issue 4: IDE shows "Cannot resolve method isDeleted()"
**Solution**:
- This is an IDE caching issue
- File → Invalidate Caches / Restart
- Or Build → Rebuild Project

---

## 📊 Database Schema Reference

### Inventory Locations Table
```sql
CREATE TABLE inventory_locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50),
    batch_id INT,
    location ENUM('MAIN', 'SHELF', 'WEBSITE'),  -- 3 locations
    quantity INT,
    version INT DEFAULT 0,  -- for optimistic locking
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id)
);
```

### Stock Movements Log
```sql
-- Every deduction is logged here
SELECT * FROM stock_movements 
WHERE product_code = 'apple001' 
ORDER BY movement_date DESC 
LIMIT 10;
```

---

## ✨ Features

### ✅ What's Fixed
- Customer products API returns WEBSITE inventory
- Add to cart checks WEBSITE availability
- Customer checkout deducts from WEBSITE
- Cashier POS still uses SHELF (unchanged)
- Proper inventory location separation

### ✅ What's Preserved
- FEFO (First Expire First Out) logic
- Optimistic locking for concurrency
- Stock movement logging
- Backward compatibility with cashier system

---

## 📝 Notes

- The old stored procedure `deduct_stock_for_sale` still exists (for backward compatibility)
- The new procedure `deduct_stock_for_sale_v2` accepts location parameter
- Both cashiers and customers can work simultaneously without conflicts
- Each location (MAIN, SHELF, WEBSITE) is independent

---

## 🎉 Result

After deploying these changes:
- ✅ Customers see products from WEBSITE location
- ✅ Online orders deduct from WEBSITE inventory
- ✅ Cashier POS remains unaffected (uses SHELF)
- ✅ No inventory mixing between channels
- ✅ Proper stock tracking per location

---

## 📞 Support

If you encounter any issues:
1. Check backend logs: `logs/catalina.out`
2. Check database for stored procedure: `SHOW PROCEDURE STATUS WHERE Name LIKE 'deduct%';`
3. Verify inventory data: `SELECT * FROM inventory_locations WHERE location = 'WEBSITE';`

---

**Last Updated**: February 9, 2026
**Status**: ✅ Ready for Deployment


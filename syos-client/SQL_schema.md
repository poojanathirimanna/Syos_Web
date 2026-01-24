 
CREATE DATABASE if not exists syos_billing_Web;
USE syos_billing_Web;

-- ===================================================================
-- CORE TABLES (Assignment 1)
-- ===================================================================

-- -------------------------------------------------------------------
-- 1. Product Categories
-- -------------------------------------------------------------------
CREATE TABLE product_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------------
-- 2. Products
-- -------------------------------------------------------------------
CREATE TABLE products (
    product_code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    image_url LONGTEXT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id),
    INDEX idx_product_name (name),
    INDEX idx_category (category_id)
);

-- -------------------------------------------------------------------
-- 3. Stock Batches (with expiry dates for FEFO)
-- -------------------------------------------------------------------
CREATE TABLE stock_batches (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    purchase_date DATE NOT NULL,
    expiry_date DATE,
    available_quantity INT NOT NULL DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    -- Concurrency control (Assignment 2)
    version INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    INDEX idx_batch_product (product_code),
    INDEX idx_batch_expiry (product_code, expiry_date),
    INDEX idx_batch_version (batch_id, version)
);

-- -------------------------------------------------------------------
-- 4. Inventory Locations (MAIN/SHELF/WEBSITE with batch tracking)
-- -------------------------------------------------------------------
CREATE TABLE inventory_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    batch_id INT NOT NULL,
    location ENUM('MAIN', 'SHELF', 'WEBSITE') NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    moved_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Concurrency control (Assignment 2)
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id),
    UNIQUE KEY unique_batch_location (batch_id, location),
    INDEX idx_product_location (product_code, location),
    INDEX idx_location_version (id, version)
);

-- -------------------------------------------------------------------
-- 5. Stock Movement History (for reshelving reports)
-- -------------------------------------------------------------------
CREATE TABLE stock_movements (
    movement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    batch_id INT NOT NULL,
    quantity INT NOT NULL,
    from_location ENUM('MAIN', 'SHELF', 'WEBSITE', 'SUPPLIER') NOT NULL,
    to_location ENUM('MAIN', 'SHELF', 'WEBSITE', 'CUSTOMER') NOT NULL,
    movement_type ENUM('RESTOCK', 'TRANSFER', 'SALE', 'ADJUSTMENT', 'RETURN') NOT NULL,
    movement_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(50),
    bill_number VARCHAR(50),
    notes TEXT,
    previous_quantity INT,
    new_quantity INT,
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id),
    INDEX idx_movement_date (movement_date),
    INDEX idx_product (product_code),
    INDEX idx_type (movement_type),
    INDEX idx_user (user_id)
);

-- -------------------------------------------------------------------
-- 6. Roles
-- -------------------------------------------------------------------
CREATE TABLE roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- -------------------------------------------------------------------
-- 7. Users
-- -------------------------------------------------------------------
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    INDEX idx_role (role_id)
);

-- -------------------------------------------------------------------
-- 8. Business Day Tracking
-- -------------------------------------------------------------------
CREATE TABLE business_day (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_business_active (active)
);

-- -------------------------------------------------------------------
-- 9. Bills
-- -------------------------------------------------------------------
CREATE TABLE bills (
    bill_number VARCHAR(50) PRIMARY KEY,
    transaction_date DATETIME NOT NULL,
    cashier_id VARCHAR(50),
    customer_id VARCHAR(50),
    channel VARCHAR(20) NOT NULL DEFAULT 'IN_STORE',
    subtotal DECIMAL(10, 2),
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2),
    business_day_id BIGINT,
    FOREIGN KEY (cashier_id) REFERENCES users(user_id),
    FOREIGN KEY (business_day_id) REFERENCES business_day(id),
    INDEX idx_bill_date (transaction_date),
    INDEX idx_bill_cashier (cashier_id),
    INDEX idx_bill_channel (channel),
    INDEX idx_bill_cashier_date (cashier_id, transaction_date)
);

-- -------------------------------------------------------------------
-- 10. Bill Items (with batch tracking)
-- -------------------------------------------------------------------
CREATE TABLE bill_items (
    bill_item_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    batch_id INT,
    quantity INT NOT NULL,
    price_at_sale DECIMAL(10, 2) NOT NULL,
    discount_applied DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (bill_number) REFERENCES bills(bill_number),
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id),
    INDEX idx_billitem_bill (bill_number),
    INDEX idx_billitem_product (product_code)
);

-- -------------------------------------------------------------------
-- 11. Payment Methods Lookup
-- -------------------------------------------------------------------
CREATE TABLE payment_methods (
    payment_method_id INT PRIMARY KEY,
    method_name VARCHAR(50) NOT NULL UNIQUE
);

-- -------------------------------------------------------------------
-- 12. Payments
-- -------------------------------------------------------------------
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) NOT NULL,
    payment_method_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    amount_tendered DECIMAL(10, 2),
    change_given DECIMAL(10, 2),
    transaction_ref VARCHAR(255),
    payment_date DATETIME NOT NULL,
    FOREIGN KEY (bill_number) REFERENCES bills(bill_number),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(payment_method_id),
    INDEX idx_payment_bill (bill_number)
);

-- -------------------------------------------------------------------
-- 13. Customers (for online orders)
-- -------------------------------------------------------------------
CREATE TABLE customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ===================================================================
-- ASSIGNMENT 2: CONCURRENCY & MULTI-USER SUPPORT
-- ===================================================================

-- -------------------------------------------------------------------
-- 14. User Sessions (Track concurrent users)
-- -------------------------------------------------------------------
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    device_info VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    logout_time TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_session_user (user_id),
    INDEX idx_session_active (is_active),
    INDEX idx_session_last_activity (last_activity)
);

-- -------------------------------------------------------------------
-- 15. Request Log (Track all API requests for concurrency testing)
-- -------------------------------------------------------------------
CREATE TABLE request_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    request_type VARCHAR(100) NOT NULL,
    user_id VARCHAR(50),
    session_id VARCHAR(255),
    request_timestamp TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
    processing_start TIMESTAMP(3) NULL,
    processing_end TIMESTAMP(3) NULL,
    status ENUM('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'QUEUED',
    response_time_ms INT,
    error_message TEXT,
    request_data JSON,
    response_data JSON,
    server_thread VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES user_sessions(session_id),
    INDEX idx_status (status),
    INDEX idx_timestamp (request_timestamp),
    INDEX idx_user (user_id),
    INDEX idx_type (request_type)
);

-- -------------------------------------------------------------------
-- 16. Inventory Transactions (Audit trail for concurrent updates)
-- -------------------------------------------------------------------
CREATE TABLE inventory_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('SALE', 'RESTOCK', 'TRANSFER', 'ADJUSTMENT', 'RETURN') NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    batch_id INT,
    quantity_change INT NOT NULL,
    from_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    to_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    user_id VARCHAR(50),
    bill_number VARCHAR(50),
    timestamp TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
    previous_quantity INT,
    new_quantity INT,
    notes TEXT,
    FOREIGN KEY (product_code) REFERENCES products(product_code),
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (bill_number) REFERENCES bills(bill_number),
    INDEX idx_product (product_code),
    INDEX idx_timestamp (timestamp),
    INDEX idx_type (transaction_type),
    INDEX idx_user (user_id)
);

-- -------------------------------------------------------------------
-- 17. Bill Sequences (Thread-safe bill number generation)
-- -------------------------------------------------------------------
CREATE TABLE bill_sequences (
    id INT PRIMARY KEY AUTO_INCREMENT,
    business_day_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_day_id) REFERENCES business_day(id)
);

-- -------------------------------------------------------------------
-- 18. Application Locks (Prevent race conditions)
-- -------------------------------------------------------------------
CREATE TABLE application_locks (
    lock_name VARCHAR(100) PRIMARY KEY,
    holder_session_id VARCHAR(255),
    acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    lock_data JSON,
    INDEX idx_expires (expires_at)
);

-- -------------------------------------------------------------------
-- 19. Performance Metrics (Monitor concurrent load)
-- -------------------------------------------------------------------
CREATE TABLE performance_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(10,2),
    recorded_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
    concurrent_users INT,
    active_sessions INT,
    metadata JSON,
    INDEX idx_metric_name (metric_name),
    INDEX idx_timestamp (recorded_at)
);

-- ===================================================================
-- STORED PROCEDURES & FUNCTIONS
-- ===================================================================

-- -------------------------------------------------------------------
-- Function: Generate next bill number (thread-safe)
-- -------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION get_next_bill_number(day_id BIGINT) 
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE seq_num INT;
    
    -- Insert and get auto-increment value atomically
    INSERT INTO bill_sequences (business_day_id) VALUES (day_id);
    SET seq_num = LAST_INSERT_ID();
    
    -- Format: BILL-20260117-0001
    RETURN CONCAT('BILL-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(seq_num, 4, '0'));
END$$

DELIMITER ;

-- -------------------------------------------------------------------
-- Procedure: Get inventory by FEFO (First Expire First Out)
-- -------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE get_fefo_inventory(
    IN p_product_code VARCHAR(50),
    IN p_location ENUM('MAIN', 'SHELF', 'WEBSITE')
)
BEGIN
    SELECT 
        il.id,
        il.product_code,
        il.batch_id,
        il.quantity,
        il.location,
        sb.expiry_date,
        sb.discount_percentage,
        p.name as product_name,
        p.unit_price
    FROM inventory_locations il
    JOIN stock_batches sb ON il.batch_id = sb.batch_id
    JOIN products p ON il.product_code = p.product_code
    WHERE il.product_code = p_product_code
      AND il.location = p_location
      AND il.quantity > 0
    ORDER BY sb.expiry_date ASC, sb.purchase_date ASC;
END$$

DELIMITER ;

-- -------------------------------------------------------------------
-- Procedure: Transfer stock between locations (FEFO)
-- -------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE transfer_stock_fefo(
    IN p_product_code VARCHAR(50),
    IN p_from_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    IN p_to_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    IN p_quantity INT,
    IN p_user_id VARCHAR(50)
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_batch_id INT;
    DECLARE v_available_qty INT;
    DECLARE v_transfer_qty INT;
    DECLARE remaining_qty INT DEFAULT p_quantity;
    
    DECLARE batch_cursor CURSOR FOR
        SELECT il.batch_id, il.quantity
        FROM inventory_locations il
        JOIN stock_batches sb ON il.batch_id = sb.batch_id
        WHERE il.product_code = p_product_code
          AND il.location = p_from_location
          AND il.quantity > 0
        ORDER BY sb.expiry_date ASC, sb.purchase_date ASC
        FOR UPDATE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    OPEN batch_cursor;
    
    transfer_loop: LOOP
        FETCH batch_cursor INTO v_batch_id, v_available_qty;
        
        IF done OR remaining_qty <= 0 THEN
            LEAVE transfer_loop;
        END IF;
        
        -- Calculate how much to transfer from this batch
        SET v_transfer_qty = LEAST(v_available_qty, remaining_qty);
        
        -- Deduct from source location
        UPDATE inventory_locations
        SET quantity = quantity - v_transfer_qty,
            version = version + 1
        WHERE batch_id = v_batch_id 
          AND location = p_from_location;
        
        -- Add to destination location
        INSERT INTO inventory_locations (product_code, batch_id, location, quantity)
        VALUES (p_product_code, v_batch_id, p_to_location, v_transfer_qty)
        ON DUPLICATE KEY UPDATE 
            quantity = quantity + v_transfer_qty,
            version = version + 1;
        
        -- Log the movement
        INSERT INTO stock_movements (
            product_code, batch_id, quantity, from_location, to_location,
            movement_type, user_id, previous_quantity, new_quantity
        ) VALUES (
            p_product_code, v_batch_id, v_transfer_qty, p_from_location, p_to_location,
            'TRANSFER', p_user_id, v_available_qty, v_available_qty - v_transfer_qty
        );
        
        SET remaining_qty = remaining_qty - v_transfer_qty;
    END LOOP;
    
    CLOSE batch_cursor;
    
    IF remaining_qty > 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Insufficient stock for transfer';
    ELSE
        COMMIT;
    END IF;
END$$

DELIMITER ;

-- -------------------------------------------------------------------
-- Procedure: Deduct stock for sale (FEFO with optimistic locking)
-- -------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE deduct_stock_for_sale(
    IN p_product_code VARCHAR(50),
    IN p_quantity INT,
    IN p_bill_number VARCHAR(50),
    IN p_user_id VARCHAR(50),
    OUT o_batch_id INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_batch_id INT;
    DECLARE v_available_qty INT;
    DECLARE v_deduct_qty INT;
    DECLARE v_version INT;
    DECLARE remaining_qty INT DEFAULT p_quantity;
    
    DECLARE batch_cursor CURSOR FOR
        SELECT il.batch_id, il.quantity, il.version
        FROM inventory_locations il
        JOIN stock_batches sb ON il.batch_id = sb.batch_id
        WHERE il.product_code = p_product_code
          AND il.location = 'SHELF'
          AND il.quantity > 0
        ORDER BY sb.expiry_date ASC, sb.purchase_date ASC
        FOR UPDATE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    OPEN batch_cursor;
    
    sale_loop: LOOP
        FETCH batch_cursor INTO v_batch_id, v_available_qty, v_version;
        
        IF done OR remaining_qty <= 0 THEN
            LEAVE sale_loop;
        END IF;
        
        SET v_deduct_qty = LEAST(v_available_qty, remaining_qty);
        
        -- Update with optimistic locking
        UPDATE inventory_locations
        SET quantity = quantity - v_deduct_qty,
            version = version + 1
        WHERE batch_id = v_batch_id 
          AND location = 'SHELF'
          AND version = v_version;
        
        -- Check if update succeeded
        IF ROW_COUNT() = 0 THEN
            ROLLBACK;
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Concurrent modification detected';
        END IF;
        
        -- Log the sale
        INSERT INTO stock_movements (
            product_code, batch_id, quantity, from_location, to_location,
            movement_type, user_id, bill_number, previous_quantity, new_quantity
        ) VALUES (
            p_product_code, v_batch_id, v_deduct_qty, 'SHELF', 'CUSTOMER',
            'SALE', p_user_id, p_bill_number, v_available_qty, v_available_qty - v_deduct_qty
        );
        
        SET remaining_qty = remaining_qty - v_deduct_qty;
        SET o_batch_id = v_batch_id;
    END LOOP;
    
    CLOSE batch_cursor;
    
    IF remaining_qty > 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Insufficient stock for sale';
    ELSE
        COMMIT;
    END IF;
END$$

DELIMITER ;

-- ===================================================================
-- INITIAL DATA SETUP
-- ===================================================================

-- Insert roles
INSERT INTO roles (role_id, role_name) VALUES
(1, 'MANAGER'),
(2, 'CASHIER');

-- Insert payment methods
INSERT INTO payment_methods (payment_method_id, method_name) VALUES
(1, 'CASH'),
(2, 'ONLINE_GATEWAY');
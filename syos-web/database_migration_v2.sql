-- ===================================================================
-- DATABASE MIGRATION: Add support for WEBSITE inventory deduction
-- Run this SQL script in your MySQL database
-- ===================================================================

USE syos_db;

-- -------------------------------------------------------------------
-- Procedure: Deduct stock for sale V2 (supports SHELF and WEBSITE)
-- This is the enhanced version that accepts location parameter
-- -------------------------------------------------------------------
DELIMITER $$

DROP PROCEDURE IF EXISTS deduct_stock_for_sale_v2$$

CREATE PROCEDURE deduct_stock_for_sale_v2(
    IN p_product_code VARCHAR(50),
    IN p_quantity INT,
    IN p_location ENUM('SHELF', 'WEBSITE'),
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
    DECLARE v_msg VARCHAR(255);

    DECLARE batch_cursor CURSOR FOR
        SELECT il.batch_id, il.quantity, il.version
        FROM inventory_locations il
        JOIN stock_batches sb ON il.batch_id = sb.batch_id
        WHERE il.product_code = p_product_code
          AND il.location = p_location
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
          AND location = p_location
          AND version = v_version;

        IF ROW_COUNT() = 0 THEN
            ROLLBACK;
            SET v_msg = 'Concurrent modification detected';
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
        END IF;

        -- Log the sale (from specified location to CUSTOMER)
        INSERT INTO stock_movements (
            product_code, batch_id, quantity, from_location, to_location,
            movement_type, user_id, bill_number, previous_quantity, new_quantity
        ) VALUES (
            p_product_code, v_batch_id, v_deduct_qty, p_location, 'CUSTOMER',
            'SALE', p_user_id, p_bill_number, v_available_qty, v_available_qty - v_deduct_qty
        );

        SET remaining_qty = remaining_qty - v_deduct_qty;
        SET o_batch_id = v_batch_id;
    END LOOP;

    CLOSE batch_cursor;

    IF remaining_qty > 0 THEN
        ROLLBACK;
        SET v_msg = CONCAT('Insufficient stock in ', CAST(p_location AS CHAR), ' location for sale');
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
    ELSE
        COMMIT;
    END IF;
END$$

DELIMITER ;

-- -------------------------------------------------------------------
-- Verification: Test the new stored procedure
-- -------------------------------------------------------------------
-- You can test it with:
-- CALL deduct_stock_for_sale_v2('apple001', 1, 'WEBSITE', 'TEST-BILL-001', 'customer1', @batch_id);
-- SELECT @batch_id;

SELECT '✅ Migration completed! New stored procedure deduct_stock_for_sale_v2 created.' AS status;


-- ============================================================================
-- FOOD DELIVERY MICROSERVICES - DATABASE SEQUENCE FIX SCRIPT
-- ============================================================================
-- Purpose: Reset all database sequences to resolve duplicate key constraint violations
-- Issue: Sequences are trying to use IDs that already exist (ID=100)
-- Date: 2026-06-11
-- ============================================================================

-- Connect to each database and execute the corresponding section

-- ============================================================================
-- RESTAURANT DATABASE (Port 5433)
-- ============================================================================

-- Check current sequence values
SELECT 'restaurants_id_seq current value:', nextval('restaurants_id_seq');
SELECT 'menu_items_id_seq current value:', nextval('menu_items_id_seq');

-- Check max IDs in tables
SELECT 'Max restaurant ID:', COALESCE(MAX(id), 0) FROM restaurants;
SELECT 'Max menu_item ID:', COALESCE(MAX(id), 0) FROM menu_items;

-- Reset sequences to max ID + 1
SELECT setval('restaurants_id_seq', COALESCE((SELECT MAX(id) FROM restaurants), 0) + 1, false);
SELECT setval('menu_items_id_seq', COALESCE((SELECT MAX(id) FROM menu_items), 0) + 1, false);

-- Verify new sequence values
SELECT 'restaurants_id_seq new value:', currval('restaurants_id_seq');
SELECT 'menu_items_id_seq new value:', currval('menu_items_id_seq');

-- Alternative: If you want to start from a specific number (e.g., 101)
-- ALTER SEQUENCE restaurants_id_seq RESTART WITH 101;
-- ALTER SEQUENCE menu_items_id_seq RESTART WITH 101;

-- ============================================================================
-- ORDER DATABASE (Port 5434)
-- ============================================================================

-- Check current sequence value
SELECT 'orders_id_seq current value:', nextval('orders_id_seq');
SELECT 'order_items_id_seq current value:', nextval('order_items_id_seq');

-- Check max IDs in tables
SELECT 'Max order ID:', COALESCE(MAX(id), 0) FROM orders;
SELECT 'Max order_item ID:', COALESCE(MAX(id), 0) FROM order_items;

-- Reset sequences to max ID + 1
SELECT setval('orders_id_seq', COALESCE((SELECT MAX(id) FROM orders), 0) + 1, false);
SELECT setval('order_items_id_seq', COALESCE((SELECT MAX(id) FROM order_items), 0) + 1, false);

-- Verify new sequence values
SELECT 'orders_id_seq new value:', currval('orders_id_seq');
SELECT 'order_items_id_seq new value:', currval('order_items_id_seq');

-- Alternative: Start from specific number
-- ALTER SEQUENCE orders_id_seq RESTART WITH 101;
-- ALTER SEQUENCE order_items_id_seq RESTART WITH 101;

-- ============================================================================
-- PAYMENT DATABASE (Port 5435)
-- ============================================================================

-- Check current sequence value
SELECT 'payments_id_seq current value:', nextval('payments_id_seq');

-- Check max ID in table
SELECT 'Max payment ID:', COALESCE(MAX(id), 0) FROM payments;

-- Reset sequence to max ID + 1
SELECT setval('payments_id_seq', COALESCE((SELECT MAX(id) FROM payments), 0) + 1, false);

-- Verify new sequence value
SELECT 'payments_id_seq new value:', currval('payments_id_seq');

-- Alternative: Start from specific number
-- ALTER SEQUENCE payments_id_seq RESTART WITH 101;

-- ============================================================================
-- USER DATABASE (Port 5432)
-- ============================================================================

-- Check current sequence value
SELECT 'users_id_seq current value:', nextval('users_id_seq');

-- Check max ID in table
SELECT 'Max user ID:', COALESCE(MAX(id), 0) FROM users;

-- Reset sequence to max ID + 1
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 0) + 1, false);

-- Verify new sequence value
SELECT 'users_id_seq new value:', currval('users_id_seq');

-- Alternative: Start from specific number
-- ALTER SEQUENCE users_id_seq RESTART WITH 102;

-- ============================================================================
-- NOTIFICATION DATABASE (Port 5436)
-- ============================================================================

-- Check current sequence value
SELECT 'notifications_id_seq current value:', nextval('notifications_id_seq');

-- Check max ID in table
SELECT 'Max notification ID:', COALESCE(MAX(id), 0) FROM notifications;

-- Reset sequence to max ID + 1
SELECT setval('notifications_id_seq', COALESCE((SELECT MAX(id) FROM notifications), 0) + 1, false);

-- Verify new sequence value
SELECT 'notifications_id_seq new value:', currval('notifications_id_seq');

-- Alternative: Start from specific number
-- ALTER SEQUENCE notifications_id_seq RESTART WITH 111;

-- ============================================================================
-- EXECUTION INSTRUCTIONS
-- ============================================================================

/*

OPTION 1: Execute via psql command line

# Restaurant DB
psql -h localhost -p 5433 -U postgres -d restaurantdb -f DATABASE_FIX_SCRIPT.sql

# Order DB
psql -h localhost -p 5434 -U postgres -d orderdb -f DATABASE_FIX_SCRIPT.sql

# Payment DB
psql -h localhost -p 5435 -U postgres -d paymentdb -f DATABASE_FIX_SCRIPT.sql

# User DB
psql -h localhost -p 5432 -U postgres -d userdb -f DATABASE_FIX_SCRIPT.sql

# Notification DB
psql -h localhost -p 5436 -U postgres -d notificationdb -f DATABASE_FIX_SCRIPT.sql


OPTION 2: Execute via Docker

# Restaurant DB
docker exec -i restaurant-db psql -U postgres -d restaurantdb < DATABASE_FIX_SCRIPT.sql

# Order DB
docker exec -i order-db psql -U postgres -d orderdb < DATABASE_FIX_SCRIPT.sql

# Payment DB
docker exec -i payment-db psql -U postgres -d paymentdb < DATABASE_FIX_SCRIPT.sql

# User DB
docker exec -i user-db psql -U postgres -d userdb < DATABASE_FIX_SCRIPT.sql

# Notification DB
docker exec -i notification-db psql -U postgres -d notificationdb < DATABASE_FIX_SCRIPT.sql


OPTION 3: Quick Fix - Execute individual commands

# Connect to each database and run:
SELECT setval('restaurants_id_seq', COALESCE((SELECT MAX(id) FROM restaurants), 0) + 1, false);
SELECT setval('menu_items_id_seq', COALESCE((SELECT MAX(id) FROM menu_items), 0) + 1, false);
SELECT setval('orders_id_seq', COALESCE((SELECT MAX(id) FROM orders), 0) + 1, false);
SELECT setval('order_items_id_seq', COALESCE((SELECT MAX(id) FROM order_items), 0) + 1, false);
SELECT setval('payments_id_seq', COALESCE((SELECT MAX(id) FROM payments), 0) + 1, false);
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 0) + 1, false);
SELECT setval('notifications_id_seq', COALESCE((SELECT MAX(id) FROM notifications), 0) + 1, false);

*/

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

/*
After running the fix, verify that sequences are correct:

SELECT 'restaurants', nextval('restaurants_id_seq') as next_id;
SELECT 'menu_items', nextval('menu_items_id_seq') as next_id;
SELECT 'orders', nextval('orders_id_seq') as next_id;
SELECT 'order_items', nextval('order_items_id_seq') as next_id;
SELECT 'payments', nextval('payments_id_seq') as next_id;
SELECT 'users', nextval('users_id_seq') as next_id;
SELECT 'notifications', nextval('notifications_id_seq') as next_id;

The next_id should be greater than the max ID in each table.
*/

-- ============================================================================
-- CLEANUP (OPTIONAL)
-- ============================================================================

/*
If you want to delete all test data with ID >= 100:

DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE id >= 100);
DELETE FROM orders WHERE id >= 100;
DELETE FROM menu_items WHERE id >= 100;
DELETE FROM restaurants WHERE id >= 100;
DELETE FROM payments WHERE id >= 100;
DELETE FROM users WHERE id >= 100;
DELETE FROM notifications WHERE id >= 100;

Then reset sequences:
ALTER SEQUENCE restaurants_id_seq RESTART WITH 100;
ALTER SEQUENCE menu_items_id_seq RESTART WITH 100;
ALTER SEQUENCE orders_id_seq RESTART WITH 100;
ALTER SEQUENCE order_items_id_seq RESTART WITH 100;
ALTER SEQUENCE payments_id_seq RESTART WITH 100;
ALTER SEQUENCE users_id_seq RESTART WITH 100;
ALTER SEQUENCE notifications_id_seq RESTART WITH 100;
*/

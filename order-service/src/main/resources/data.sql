-- Clear existing data first (for PostgreSQL with create-drop strategy)
DELETE
FROM order_items;
DELETE
FROM orders;

-- Sample data for Order Service
INSERT INTO orders (user_id, restaurant_id, delivery_address, status, total_amount, created_at, updated_at)
VALUES (1, 1, '123 Main St, NYC 10001', 'DELIVERED', 52.48, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 2, '456 Oak Ave, LA 90210', 'CONFIRMED', 22.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 3, '789 Pine Rd, SF 94102', 'CREATED', 28.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 1, '321 Elm St, Chicago, IL 60601', 'PREPARING', 35.75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, '654 Maple Ave, Boston, MA 02101', 'OUT_FOR_DELIVERY', 41.20, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

INSERT INTO order_items (menu_item_id, quantity, price, order_id)
VALUES (1, 2, 18.99, 1),
       (2, 1, 14.50, 1),
       (3, 1, 22.25, 2),
       (4, 1, 13.50, 2),
       (5, 1, 28.90, 3),
       (1, 1, 12.99, 4),
       (2, 1, 15.99, 4),
       (3, 2, 8.99, 5),
       (4, 1, 6.99, 5),
       (5, 1, 8.99, 5);

-- Reset sequence counters to start from 100 (PostgreSQL syntax)
SELECT setval('orders_id_seq', 100, false);
SELECT setval('order_items_id_seq', 100, false);
-- Sample data for notifications table
-- PostgreSQL compatible SQL with proper sequence reset

-- Clear existing data first (for idempotent loading)
DELETE
FROM notifications;

INSERT INTO notifications (user_id, message, type, status, is_read, created_at)
VALUES (1, 'Welcome to Food Delivery App!', 'WELCOME', 'SENT', false, CURRENT_TIMESTAMP),
       (1, 'Your order #1001 has been confirmed.', 'ORDER_CONFIRMATION', 'SENT', false, CURRENT_TIMESTAMP),
       (2, 'Your order is out for delivery.', 'ORDER_UPDATE', 'SENT', false, CURRENT_TIMESTAMP),
       (2, 'Payment successful.', 'PAYMENT_UPDATE', 'SENT', false, CURRENT_TIMESTAMP),
       (3, 'Your order has been delivered.', 'ORDER_DELIVERED', 'SENT', true, CURRENT_TIMESTAMP);

-- Reset sequence counter to start from 100 (PostgreSQL syntax)
SELECT setval('notifications_id_seq', 100, false);

-- Note: The created_at timestamps will be set to current time when inserted
-- The user_id values correspond to users in the user-service database
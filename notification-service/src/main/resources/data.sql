-- Sample data for notifications table
-- H2 and PostgreSQL compatible SQL

-- Clear existing data first (for idempotent loading)
DELETE FROM notifications;

-- Insert sample notifications with all required NOT NULL columns including title
INSERT INTO notifications (user_id, title, message, type, status, is_read, created_at)
VALUES (1, 'Welcome', 'Welcome to Food Delivery App!', 'WELCOME', 'SENT', false, CURRENT_TIMESTAMP),
       (1, 'Order Confirmed', 'Your order #1001 has been confirmed.', 'ORDER_CONFIRMATION', 'SENT', false, CURRENT_TIMESTAMP),
       (2, 'Order Update', 'Your order is out for delivery.', 'ORDER_UPDATE', 'SENT', false, CURRENT_TIMESTAMP),
       (2, 'Payment Success', 'Payment successful.', 'PAYMENT_UPDATE', 'SENT', false, CURRENT_TIMESTAMP),
       (3, 'Order Delivered', 'Your order has been delivered.', 'ORDER_DELIVERED', 'SENT', true, CURRENT_TIMESTAMP);

-- Note: The created_at timestamps will be set to current time when inserted
-- The user_id values correspond to users in the user-service database
-- Sequence reset removed for H2 compatibility (auto-increment handles this)
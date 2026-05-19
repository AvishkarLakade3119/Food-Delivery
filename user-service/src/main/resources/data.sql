-- User Service Sample Data
-- PostgreSQL compatible SQL with proper sequence reset and idempotent inserts
INSERT INTO users (username, password, first_name, last_name, name, email, phone, address, role, created_at, updated_at)
VALUES ('john_doe', 'password123', 'John', 'Doe', 'John Doe', 'john.doe@example.com', '+1234567890',
        '123 Main St, New York, NY 10001', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('jane_smith', 'password123', 'Jane', 'Smith', 'Jane Smith', 'jane.smith@example.com', '+1987654321',
        '456 Oak Ave, Los Angeles, CA 90210', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('mike_johnson', 'password123', 'Mike', 'Johnson', 'Mike Johnson', 'mike.johnson@example.com', '+1122334455',
        '789 Pine Rd, Chicago, IL 60601', 'RESTAURANT_OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('admin_user', 'admin123', 'Admin', 'User', 'Admin User', 'admin@fooddelivery.com', '+1555000000',
        '999 Admin Plaza, Admin City, AC 00000', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('delivery_driver', 'driver123', 'Tom', 'Driver', 'Tom Driver', 'tom.driver@fooddelivery.com', '+1555111111',
        '777 Driver Lane, Driver City, DC 11111', 'DELIVERY', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);
-- Reset sequence counter to start from 100 (PostgreSQL syntax)
SELECT setval('users_id_seq', 100, false);
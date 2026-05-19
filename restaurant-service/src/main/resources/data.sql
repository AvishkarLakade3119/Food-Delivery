-- Restaurant Service Sample Data
-- FIXED: Removed explicit IDs to prevent auto-increment conflicts
-- PostgreSQL auto-generates IDs starting from 1, 2, 3 for seed data
-- New POST requests will get IDs starting from 100 (after sequence reset)

INSERT INTO restaurants (name, address, phone, cuisine_type, rating, is_active, created_at, updated_at)
VALUES ('Pizza Palace', '123 Food Street, New York, NY 10001', '+1234567890', 'Italian', 4.5, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ('Burger Barn', '456 Grill Avenue, Los Angeles, CA 90210', '+1987654321', 'American', 4.2, true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Sushi Zen', '789 Tokyo Lane, San Francisco, CA 94102', '+1122334455', 'Japanese', 4.8, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Menu Items Sample Data
-- FIXED: Removed explicit IDs to prevent auto-increment conflicts
-- Using restaurant_id 1, 2, 3 which will be auto-generated above

INSERT INTO menu_items (restaurant_id, name, description, price, is_available, created_at, updated_at)
VALUES (1, 'Margherita Pizza', 'Fresh tomatoes, mozzarella cheese, basil, olive oil', 18.99, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (1, 'Pepperoni Pizza', 'Pizza with pepperoni, tomato sauce, and mozzarella', 15.99, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 'Classic Burger', 'Beef patty with lettuce, tomato, and pickles', 9.99, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 'Cheese Fries', 'Crispy fries topped with melted cheese', 6.99, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 'California Roll', 'Sushi roll with crab, avocado, and cucumber', 8.99, true, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- RESET SEQUENCE COUNTERS TO START FROM 100
-- PostgreSQL uses sequences (bigserial), NOT H2 ALTER COLUMN RESTART syntax
-- This ensures new POST requests start from ID=100, avoiding conflicts with seed data (1, 2, 3)
SELECT setval('restaurants_id_seq', 100, false);
SELECT setval('menu_items_id_seq', 100, false);
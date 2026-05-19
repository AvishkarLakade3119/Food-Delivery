-- Sample data for Payment Service
-- Clear existing data first (for PostgreSQL with create-drop strategy)
DELETE
FROM payments;

INSERT INTO payments (order_id, amount, payment_method, status, transaction_id, created_at, updated_at)
VALUES (1, 52.48, 'CREDIT_CARD', 'SUCCESS', 'txn_001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 35.75, 'DEBIT_CARD', 'SUCCESS', 'txn_002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 28.90, 'DIGITAL_WALLET', 'PENDING', 'txn_003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 35.75, 'CREDIT_CARD', 'SUCCESS', 'txn_004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 41.20, 'DEBIT_CARD', 'SUCCESS', 'txn_005', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Reset sequence counter to start from 100 (PostgreSQL syntax)
SELECT setval('payments_id_seq', 100, false);
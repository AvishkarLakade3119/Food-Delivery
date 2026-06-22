-- Create databases for all microservices
CREATE DATABASE IF NOT EXISTS userdb;
CREATE DATABASE IF NOT EXISTS restaurantdb;
CREATE DATABASE IF NOT EXISTS orderdb;
CREATE DATABASE IF NOT EXISTS paymentdb;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE userdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE restaurantdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE paymentdb TO postgres;
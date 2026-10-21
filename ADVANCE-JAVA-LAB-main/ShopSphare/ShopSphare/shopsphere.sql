-- =====================================================================
-- Database Schema for ShopSphere E-Commerce System
-- Target Engine: MySQL 8.0+
-- Character Set: utf8mb4 / utf8mb4_unicode_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `shopsphere`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `shopsphere`;

-- Disable foreign key checks during schema creation/reset
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables if needed (in reverse dependency order)
DROP TABLE IF EXISTS `coupon_usage`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `coupons`;
DROP TABLE IF EXISTS `wishlist`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `addresses`;
DROP TABLE IF EXISTS `users`;

-- =====================================================================
-- 1. Table: users
-- Stores user accounts, authentication credentials, and roles.
-- =====================================================================
CREATE TABLE `users` (
    `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(191) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL COMMENT 'Secure hash using bcrypt, Argon2id, or PBKDF2',
    `phone_number` VARCHAR(20) NULL,
    `role` ENUM('customer', 'admin', 'vendor') NOT NULL DEFAULT 'customer',
    `status` ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uq_users_email` (`email`),
    KEY `idx_users_role` (`role`),
    KEY `idx_users_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 2. Table: addresses
-- Stores shipping and billing addresses for users.
-- =====================================================================
CREATE TABLE `addresses` (
    `address_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `address_type` ENUM('shipping', 'billing', 'both') NOT NULL DEFAULT 'shipping',
    `is_default` BOOLEAN NOT NULL DEFAULT FALSE,
    `recipient_name` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `street_address_line1` VARCHAR(255) NOT NULL,
    `street_address_line2` VARCHAR(255) NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `postal_code` VARCHAR(20) NOT NULL,
    `country` VARCHAR(100) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`address_id`),
    KEY `idx_addresses_user_id` (`user_id`),
    CONSTRAINT `fk_addresses_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 3. Table: categories
-- Product categories supporting nested/hierarchical taxonomies.
-- =====================================================================
CREATE TABLE `categories` (
    `category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `parent_id` INT UNSIGNED NULL,
    `name` VARCHAR(100) NOT NULL,
    `slug` VARCHAR(120) NOT NULL,
    `description` TEXT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`category_id`),
    UNIQUE KEY `uq_categories_slug` (`slug`),
    KEY `idx_categories_parent_id` (`parent_id`),
    KEY `idx_categories_is_active` (`is_active`),
    CONSTRAINT `fk_categories_parent_id`
        FOREIGN KEY (`parent_id`)
        REFERENCES `categories` (`category_id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 4. Table: products
-- Catalog products including pricing, inventory, and status.
-- =====================================================================
CREATE TABLE `products` (
    `product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `category_id` INT UNSIGNED NULL,
    `sku` VARCHAR(64) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `slug` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `sale_price` DECIMAL(10, 2) NULL,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `status` ENUM('draft', 'active', 'out_of_stock', 'archived') NOT NULL DEFAULT 'active',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`product_id`),
    UNIQUE KEY `uq_products_sku` (`sku`),
    UNIQUE KEY `uq_products_slug` (`slug`),
    KEY `idx_products_category_id` (`category_id`),
    KEY `idx_products_status` (`status`),
    KEY `idx_products_price` (`price`),
    CONSTRAINT `fk_products_category_id`
        FOREIGN KEY (`category_id`)
        REFERENCES `categories` (`category_id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 5. Table: cart
-- Stores shopping cart items associated with registered users.
-- =====================================================================
CREATE TABLE `cart` (
    `cart_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `quantity` INT UNSIGNED NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`cart_id`),
    UNIQUE KEY `uq_cart_user_product` (`user_id`, `product_id`),
    KEY `idx_cart_product_id` (`product_id`),
    CONSTRAINT `fk_cart_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_cart_product_id`
        FOREIGN KEY (`product_id`)
        REFERENCES `products` (`product_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 6. Table: wishlist
-- Stores saved/bookmarked products for users.
-- =====================================================================
CREATE TABLE `wishlist` (
    `wishlist_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`wishlist_id`),
    UNIQUE KEY `uq_wishlist_user_product` (`user_id`, `product_id`),
    KEY `idx_wishlist_product_id` (`product_id`),
    CONSTRAINT `fk_wishlist_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_wishlist_product_id`
        FOREIGN KEY (`product_id`)
        REFERENCES `products` (`product_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 7. Table: coupons
-- Stores discount vouchers and promotional campaign rules.
-- =====================================================================
CREATE TABLE `coupons` (
    `coupon_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL,
    `discount_type` ENUM('percentage', 'fixed_amount') NOT NULL,
    `discount_value` DECIMAL(10, 2) NOT NULL,
    `min_order_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `max_discount_amount` DECIMAL(10, 2) NULL COMMENT 'Applies to percentage discounts',
    `usage_limit` INT UNSIGNED NULL COMMENT 'Total allowed redemption count; NULL for unlimited',
    `usage_per_user` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Allowed redemptions per customer',
    `start_date` DATETIME NOT NULL,
    `end_date` DATETIME NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`coupon_id`),
    UNIQUE KEY `uq_coupons_code` (`code`),
    KEY `idx_coupons_active_dates` (`is_active`, `start_date`, `end_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 8. Table: orders
-- Stores purchase orders, financial totals, and lifecycle statuses.
-- =====================================================================
CREATE TABLE `orders` (
    `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_number` VARCHAR(64) NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `shipping_address_id` BIGINT UNSIGNED NULL,
    `billing_address_id` BIGINT UNSIGNED NULL,
    `coupon_id` INT UNSIGNED NULL,
    `order_status` ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled', 'refunded') NOT NULL DEFAULT 'pending',
    `subtotal` DECIMAL(10, 2) NOT NULL,
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `shipping_fee` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `tax_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(10, 2) NOT NULL,
    `notes` TEXT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uq_orders_order_number` (`order_number`),
    KEY `idx_orders_user_id` (`user_id`),
    KEY `idx_orders_shipping_address` (`shipping_address_id`),
    KEY `idx_orders_billing_address` (`billing_address_id`),
    KEY `idx_orders_coupon_id` (`coupon_id`),
    KEY `idx_orders_status` (`order_status`),
    KEY `idx_orders_created_at` (`created_at`),
    CONSTRAINT `fk_orders_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_shipping_address_id`
        FOREIGN KEY (`shipping_address_id`)
        REFERENCES `addresses` (`address_id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_billing_address_id`
        FOREIGN KEY (`billing_address_id`)
        REFERENCES `addresses` (`address_id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_coupon_id`
        FOREIGN KEY (`coupon_id`)
        REFERENCES `coupons` (`coupon_id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 9. Table: order_items
-- Line items associated with each order snapshotting historical pricing.
-- =====================================================================
CREATE TABLE `order_items` (
    `order_item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `quantity` INT UNSIGNED NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL COMMENT 'Unit price at moment of purchase',
    `total_price` DECIMAL(10, 2) NOT NULL COMMENT 'quantity * unit_price',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_item_id`),
    KEY `idx_order_items_order_id` (`order_id`),
    KEY `idx_order_items_product_id` (`product_id`),
    CONSTRAINT `fk_order_items_order_id`
        FOREIGN KEY (`order_id`)
        REFERENCES `orders` (`order_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_order_items_product_id`
        FOREIGN KEY (`product_id`)
        REFERENCES `products` (`product_id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 10. Table: payments
-- Records payment attempts, transactions, and gateways.
-- =====================================================================
CREATE TABLE `payments` (
    `payment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `payment_method` ENUM('credit_card', 'debit_card', 'paypal', 'stripe', 'cod', 'bank_transfer') NOT NULL,
    `transaction_id` VARCHAR(191) NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `payment_status` ENUM('pending', 'completed', 'failed', 'refunded') NOT NULL DEFAULT 'pending',
    `payment_gateway_response` TEXT NULL,
    `paid_at` DATETIME NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`),
    UNIQUE KEY `uq_payments_transaction_id` (`transaction_id`),
    KEY `idx_payments_order_id` (`order_id`),
    KEY `idx_payments_status` (`payment_status`),
    CONSTRAINT `fk_payments_order_id`
        FOREIGN KEY (`order_id`)
        REFERENCES `orders` (`order_id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 11. Table: reviews
-- Customer product reviews and 1-5 star ratings.
-- =====================================================================
CREATE TABLE `reviews` (
    `review_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `rating` TINYINT UNSIGNED NOT NULL,
    `title` VARCHAR(150) NULL,
    `comment` TEXT NULL,
    `is_verified_purchase` BOOLEAN NOT NULL DEFAULT FALSE,
    `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'approved',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`review_id`),
    UNIQUE KEY `uq_reviews_user_product` (`user_id`, `product_id`),
    KEY `idx_reviews_product_id` (`product_id`),
    KEY `idx_reviews_rating` (`rating`),
    KEY `idx_reviews_status` (`status`),
    CONSTRAINT `fk_reviews_product_id`
        FOREIGN KEY (`product_id`)
        REFERENCES `products` (`product_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_reviews_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `chk_reviews_rating` CHECK (`rating` BETWEEN 1 AND 5)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- 12. Table: coupon_usage
-- Tracks redemptions of promotional coupons per user and order.
-- =====================================================================
CREATE TABLE `coupon_usage` (
    `usage_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `coupon_id` INT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `discount_applied` DECIMAL(10, 2) NOT NULL,
    `used_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`usage_id`),
    UNIQUE KEY `uq_coupon_order` (`coupon_id`, `order_id`),
    KEY `idx_coupon_usage_user` (`user_id`),
    KEY `idx_coupon_usage_coupon` (`coupon_id`),
    CONSTRAINT `fk_coupon_usage_coupon_id`
        FOREIGN KEY (`coupon_id`)
        REFERENCES `coupons` (`coupon_id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT `fk_coupon_usage_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_coupon_usage_order_id`
        FOREIGN KEY (`order_id`)
        REFERENCES `orders` (`order_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- Seed Initial Sample Data for Demonstration
-- =====================================================================
INSERT INTO `categories` (`category_id`, `parent_id`, `name`, `slug`, `description`, `is_active`) VALUES
(1, NULL, 'Electronics', 'electronics', 'Computers, smartphones, and high-tech gadgets.', TRUE),
(2, NULL, 'Apparel & Fashion', 'apparel-fashion', 'Modern fashion, footwear, and casual wear.', TRUE),
(3, NULL, 'Home & Living', 'home-living', 'Furniture, kitchenware, and interior accessories.', TRUE),
(4, NULL, 'Accessories', 'accessories', 'Watches, bags, headphones, and daily gear.', TRUE);

INSERT INTO `products` (`product_id`, `category_id`, `sku`, `name`, `slug`, `description`, `price`, `sale_price`, `stock_quantity`, `status`) VALUES
(1, 1, 'SKU-LAPTOP-01', 'Ultra Slim Laptop 15\"', 'ultra-slim-laptop-15', 'High-performance laptop featuring a vibrant display, 16GB RAM, and 512GB NVMe SSD.', 999.99, 899.99, 25, 'active'),
(2, 1, 'SKU-PHONE-02', 'Pro 5G Smartphone', 'pro-5g-smartphone', 'Next-gen flagship smartphone with 120Hz OLED screen and advanced dual-lens camera.', 699.00, NULL, 50, 'active'),
(3, 4, 'SKU-HEADPHONE-03', 'Noise Cancelling Headphones', 'noise-cancelling-headphones', 'Over-ear wireless headphones with active noise cancellation and 30-hour battery life.', 199.50, 149.50, 80, 'active'),
(4, 1, 'SKU-KEYBOARD-04', 'Mechanical Gaming Keyboard', 'mechanical-gaming-keyboard', 'RGB backlit mechanical keyboard with tactile switches and aerospace aluminum frame.', 129.99, NULL, 40, 'active'),
(5, 4, 'SKU-WATCH-05', 'Smart Fitness Watch', 'smart-fitness-watch', 'Waterproof fitness smartwatch with heart-rate monitoring, GPS, and custom watch faces.', 249.99, 199.99, 60, 'active'),
(6, 4, 'SKU-BACKPACK-06', 'Urban Waterproof Backpack', 'urban-waterproof-backpack', 'Durable, weather-resistant everyday backpack with dedicated padded laptop compartment.', 79.99, NULL, 100, 'active');

INSERT INTO `coupons` (`coupon_id`, `code`, `discount_type`, `discount_value`, `min_order_amount`, `max_discount_amount`, `usage_limit`, `usage_per_user`, `start_date`, `end_date`, `is_active`) VALUES
(1, 'SAVE10', 'percentage', 10.00, 50.00, 25.00, 500, 2, '2025-01-01 00:00:00', '2030-12-31 23:59:59', TRUE),
(2, 'WELCOME20', 'fixed_amount', 20.00, 100.00, NULL, 1000, 1, '2025-01-01 00:00:00', '2030-12-31 23:59:59', TRUE);

-- Password for both accounts is 'Password123!' (hashed with PBKDF2)
INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `password_hash`, `phone_number`, `role`, `status`) VALUES
(1, 'Admin', 'User', 'admin@shopsphere.com', '65536:s4mPLe5aLtValUE1234:s4mPLeH4shValUE1234567890abcdef', '555-0100', 'admin', 'active'),
(2, 'Jane', 'Doe', 'customer@shopsphere.com', '65536:s4mPLe5aLtValUE1234:s4mPLeH4shValUE1234567890abcdef', '555-0101', 'customer', 'active');

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;



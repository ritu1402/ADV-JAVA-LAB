# ShopSphere E-Commerce Database Schema

This repository contains the complete MySQL database schema for the **ShopSphere** e-commerce platform.

## File Overview

- [`shopsphere.sql`](file:///home/sahil-sepat/Desktop/ShopSphare/shopsphere.sql): Complete SQL script defining database creation, 12 relational tables, constraints, indexes, and referential actions.

---

## Tables Summary

| # | Table Name | Description | Key Relationships |
|---|------------|-------------|-------------------|
| 1 | `users` | User accounts, credentials, and authentication roles. Secure password hashing field (`VARCHAR(255)`). | Referenced by `addresses`, `cart`, `wishlist`, `orders`, `reviews`, `coupon_usage` |
| 2 | `addresses` | Customer shipping and billing address records. | `user_id` &rarr; `users(user_id)` |
| 3 | `categories` | Product categories supporting multi-level hierarchy via self-referencing foreign key. | `parent_id` &rarr; `categories(category_id)` |
| 4 | `products` | Product inventory, pricing, SKUs, and availability statuses. | `category_id` &rarr; `categories(category_id)` |
| 5 | `cart` | Active shopping cart items per user with uniqueness constraint on `(user_id, product_id)`. | `user_id` &rarr; `users`, `product_id` &rarr; `products` |
| 6 | `wishlist` | Saved product lists per user with uniqueness constraint on `(user_id, product_id)`. | `user_id` &rarr; `users`, `product_id` &rarr; `products` |
| 7 | `coupons` | Promotional vouchers with percentage or fixed discount rules and redemption thresholds. | Referenced by `orders`, `coupon_usage` |
| 8 | `orders` | Customer checkout orders recording totals, discounts, shipping fees, tax, and order statuses. | `user_id` &rarr; `users`, `shipping_address_id` / `billing_address_id` &rarr; `addresses`, `coupon_id` &rarr; `coupons` |
| 9 | `order_items` | Snapshot of purchased products, quantities, and historical unit prices per order. | `order_id` &rarr; `orders`, `product_id` &rarr; `products` |
| 10 | `payments` | Transactions, payment methods, gateway responses, and payment statuses. | `order_id` &rarr; `orders` |
| 11 | `reviews` | Customer ratings (1-5 stars) and reviews with verified purchase flags. | `product_id` &rarr; `products`, `user_id` &rarr; `users` |
| 12 | `coupon_usage` | Audit log of applied coupons per user and order. | `coupon_id` &rarr; `coupons`, `user_id` &rarr; `users`, `order_id` &rarr; `orders` |

---

## How to Run

Execute the SQL script in MySQL 8.0+:

```bash
mysql -u <username> -p < shopsphere.sql
```


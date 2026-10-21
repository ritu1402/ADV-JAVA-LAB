package com.shopsphere.service;

import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(Product product) throws SQLException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be empty.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be non-negative.");
        }
        if (product.getSalePrice() != null && product.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sale price must be non-negative.");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            product.setStockQuantity(0);
        }
        if (product.getStatus() == null) {
            product.setStatus("active");
        }

        return productDAO.create(product);
    }

    public Optional<Product> getProductById(Long productId) throws SQLException {
        if (productId == null || productId <= 0) {
            return Optional.empty();
        }
        return productDAO.findById(productId);
    }

    public Optional<Product> getProductBySku(String sku) throws SQLException {
        if (sku == null || sku.trim().isEmpty()) {
            return Optional.empty();
        }
        return productDAO.findBySku(sku.trim());
    }

    public List<Product> getProductsByCategory(Integer categoryId) throws SQLException {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }
        return productDAO.findByCategory(categoryId);
    }

    public List<Product> getActiveProducts(int page, int pageSize) throws SQLException {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 20;
        int offset = (page - 1) * pageSize;
        return productDAO.findAllActive(pageSize, offset);
    }

    public boolean updateProduct(Product product) throws SQLException {
        if (product == null || product.getProductId() == null) {
            throw new IllegalArgumentException("Cannot update product without an ID.");
        }
        return productDAO.update(product);
    }

    public boolean adjustStock(Long productId, int quantityDelta) throws SQLException {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        return productDAO.updateStock(productId, quantityDelta);
    }

    public boolean deleteProduct(Long productId) throws SQLException {
        if (productId == null || productId <= 0) {
            return false;
        }
        return productDAO.delete(productId);
    }

    public boolean isProductInStock(Long productId, int requiredQuantity) throws SQLException {
        Optional<Product> productOpt = productDAO.findById(productId);
        return productOpt.isPresent() && 
               "active".equalsIgnoreCase(productOpt.get().getStatus()) &&
               productOpt.get().getStockQuantity() >= requiredQuantity;
    }
}


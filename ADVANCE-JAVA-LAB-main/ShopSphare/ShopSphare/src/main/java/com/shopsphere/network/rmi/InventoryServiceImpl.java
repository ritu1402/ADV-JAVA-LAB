package com.shopsphere.network.rmi;

import com.shopsphere.model.Product;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Academic Demonstration: Implementation of the RMI InventoryService.
 * Extends UnicastRemoteObject to enable remote method invocation over JRMP (Java Remote Method Protocol).
 */
public class InventoryServiceImpl extends UnicastRemoteObject implements InventoryService {

    private static final long serialVersionUID = 1L;

    private final ConcurrentMap<String, Product> productCatalog;

    public InventoryServiceImpl() throws RemoteException {
        super();
        this.productCatalog = new ConcurrentHashMap<>();
        seedMockCatalog();
    }

    private void seedMockCatalog() {
        productCatalog.put("SKU-LAPTOP-01", new Product(
            1L, 1, "SKU-LAPTOP-01", "Ultra Slim Laptop 15\"", "ultra-slim-laptop-15",
            "High performance laptop with 16GB RAM and 512GB SSD",
            new BigDecimal("999.99"), new BigDecimal("899.99"), 25, "active",
            LocalDateTime.now(), LocalDateTime.now()
        ));

        productCatalog.put("SKU-PHONE-02", new Product(
            2L, 1, "SKU-PHONE-02", "Pro 5G Smartphone", "pro-5g-smartphone",
            "Flagship smartphone with OLED display and dual cameras",
            new BigDecimal("699.00"), null, 50, "active",
            LocalDateTime.now(), LocalDateTime.now()
        ));

        productCatalog.put("SKU-HEADPHONE-03", new Product(
            3L, 4, "SKU-HEADPHONE-03", "Noise Cancelling Headphones", "noise-cancelling-headphones",
            "Wireless over-ear headphones with active noise cancellation",
            new BigDecimal("199.50"), new BigDecimal("149.50"), 100, "active",
            LocalDateTime.now(), LocalDateTime.now()
        ));
    }

    @Override
    public int getStock(String sku) throws RemoteException {
        if (sku == null) return 0;
        Product p = productCatalog.get(sku.toUpperCase());
        int stock = (p != null) ? p.getStockQuantity() : 0;
        System.out.println("[RMI Server] Remote call getStock('" + sku + "') -> " + stock);
        return stock;
    }

    @Override
    public boolean updateStock(String sku, int quantityDelta) throws RemoteException {
        if (sku == null) return false;
        Product p = productCatalog.get(sku.toUpperCase());
        if (p == null) {
            System.out.println("[RMI Server] Remote call updateStock failed: SKU '" + sku + "' not found.");
            return false;
        }

        int newStock = Math.max(0, p.getStockQuantity() + quantityDelta);
        p.setStockQuantity(newStock);
        p.setUpdatedAt(LocalDateTime.now());
        System.out.println("[RMI Server] Remote call updateStock('" + sku + "', " + quantityDelta + ") -> new stock: " + newStock);
        return true;
    }

    @Override
    public Product getProductDetails(String sku) throws RemoteException {
        if (sku == null) return null;
        Product p = productCatalog.get(sku.toUpperCase());
        System.out.println("[RMI Server] Remote call getProductDetails('" + sku + "') -> " + (p != null ? p.getName() : "null"));
        return p;
    }

    @Override
    public Map<String, Integer> getAllInventory() throws RemoteException {
        Map<String, Integer> map = new HashMap<>();
        productCatalog.forEach((sku, prod) -> map.put(sku, prod.getStockQuantity()));
        System.out.println("[RMI Server] Remote call getAllInventory() -> " + map.size() + " items");
        return Collections.unmodifiableMap(map);
    }
}


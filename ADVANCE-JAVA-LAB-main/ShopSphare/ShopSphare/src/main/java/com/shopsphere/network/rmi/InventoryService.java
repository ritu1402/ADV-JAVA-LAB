package com.shopsphere.network.rmi;

import com.shopsphere.model.Product;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Academic Demonstration: Java RMI Remote Interface.
 * Defines remotely invocable business methods for inventory management.
 */
public interface InventoryService extends Remote {

    /**
     * Checks current available stock for a product SKU.
     */
    int getStock(String sku) throws RemoteException;

    /**
     * Updates product stock by a delta.
     */
    boolean updateStock(String sku, int quantityDelta) throws RemoteException;

    /**
     * Fetches serialized Product entity across the network.
     * Demonstrates transmission of Serializable Java objects.
     */
    Product getProductDetails(String sku) throws RemoteException;

    /**
     * Returns snapshot of entire inventory map.
     */
    Map<String, Integer> getAllInventory() throws RemoteException;
}


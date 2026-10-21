package com.shopsphere.network.rmi;

import com.shopsphere.model.Product;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

/**
 * Academic Demonstration: RMI Client.
 * Connects to the RMI Registry, looks up the remote InventoryService stub,
 * and transparently invokes remote procedures across the JVM boundary.
 */
public class RmiInventoryClient {

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : RmiInventoryServer.REGISTRY_PORT;

        System.out.println("[RmiClient] Locating RMI Registry at " + host + ":" + port + "...");

        try {
            // 1. Locate RMI Registry
            Registry registry = LocateRegistry.getRegistry(host, port);

            // 2. Look up the remote interface proxy/stub by its registered identifier
            System.out.println("[RmiClient] Looking up '" + RmiInventoryServer.SERVICE_NAME + "'...");
            InventoryService inventoryService = (InventoryService) registry.lookup(RmiInventoryServer.SERVICE_NAME);
            System.out.println("[RmiClient] Remote stub reference acquired: " + inventoryService.getClass().getName());

            // 3. Remote Invocation: Check Stock
            String testSku = "SKU-LAPTOP-01";
            System.out.println("\n--- 1. Invoking Remote Method: getStock(\"" + testSku + "\") ---");
            int initialStock = inventoryService.getStock(testSku);
            System.out.println("[RmiClient] Initial stock for " + testSku + ": " + initialStock + " units");

            // 4. Remote Invocation: Update Stock
            System.out.println("\n--- 2. Invoking Remote Method: updateStock(\"" + testSku + "\", -5) ---");
            boolean updated = inventoryService.updateStock(testSku, -5);
            System.out.println("[RmiClient] Stock update successful? " + updated);
            int newStock = inventoryService.getStock(testSku);
            System.out.println("[RmiClient] New stock for " + testSku + ": " + newStock + " units");

            // 5. Remote Invocation: Serialized Object Transfer
            System.out.println("\n--- 3. Invoking Remote Method: getProductDetails(\"" + testSku + "\") ---");
            Product product = inventoryService.getProductDetails(testSku);
            if (product != null) {
                System.out.println("[RmiClient] Successfully received serialized Product object over wire:");
                System.out.println("            - Name:  " + product.getName());
                System.out.println("            - Price: $" + product.getPrice());
                System.out.println("            - Sale:  $" + product.getSalePrice());
                System.out.println("            - Stock: " + product.getStockQuantity());
            }

            // 6. Remote Invocation: Full Inventory Snapshot
            System.out.println("\n--- 4. Invoking Remote Method: getAllInventory() ---");
            Map<String, Integer> fullInventory = inventoryService.getAllInventory();
            System.out.println("[RmiClient] Current catalog levels: " + fullInventory);

            System.out.println("\n[RmiClient] All remote invocations completed successfully.");

        } catch (Exception e) {
            System.err.println("[RmiClient] Remote invocation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


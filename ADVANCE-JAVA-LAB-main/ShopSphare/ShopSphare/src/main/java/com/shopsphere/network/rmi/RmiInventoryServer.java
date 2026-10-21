package com.shopsphere.network.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Academic Demonstration: RMI Server bootstrap launcher.
 * Creates an in-process RMI Registry on port 1099 and publishes the InventoryService stub.
 */
public class RmiInventoryServer {

    public static final int REGISTRY_PORT = 1099;
    public static final String SERVICE_NAME = "InventoryService";

    public static void main(String[] args) {
        try {
            System.out.println("[RmiServer] Initializing RMI Registry on port " + REGISTRY_PORT + "...");
            Registry registry;
            try {
                // Attempt to create registry programmatically
                registry = LocateRegistry.createRegistry(REGISTRY_PORT);
                System.out.println("[RmiServer] New RMI Registry successfully created on port " + REGISTRY_PORT);
            } catch (Exception e) {
                // If registry already running on port, locate it
                registry = LocateRegistry.getRegistry(REGISTRY_PORT);
                System.out.println("[RmiServer] Attached to existing RMI Registry on port " + REGISTRY_PORT);
            }

            // Instantiate remote service implementation
            InventoryService inventoryService = new InventoryServiceImpl();

            // Bind the remote object's stub in the registry
            registry.rebind(SERVICE_NAME, inventoryService);
            System.out.println("[RmiServer] '" + SERVICE_NAME + "' successfully bound to RMI Registry.");
            System.out.println("[RmiServer] Ready to receive remote method invocations. (Press Ctrl+C to stop)");

            // Keep alive
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("[RmiServer] Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


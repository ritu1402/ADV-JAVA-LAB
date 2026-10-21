package com.shopsphere.network.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Academic Demonstration: Low-Level TCP Socket Server using ServerSocket and byte streams.
 * Processes client text commands (e.g., 'GET_STOCK', 'UPDATE_STOCK', 'LIST_ALL').
 */
public class SocketInventoryServer {

    public static final int DEFAULT_PORT = 8085;
    private final int port;
    private final ConcurrentMap<String, Integer> inventoryStore;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final ExecutorService clientThreadPool;

    public SocketInventoryServer(int port) {
        this.port = port;
        this.inventoryStore = new ConcurrentHashMap<>();
        this.clientThreadPool = Executors.newCachedThreadPool();
        seedMockInventory();
    }

    private void seedMockInventory() {
        inventoryStore.put("SKU-LAPTOP-01", 15);
        inventoryStore.put("SKU-PHONE-02", 42);
        inventoryStore.put("SKU-HEADPHONE-03", 88);
        inventoryStore.put("SKU-KEYBOARD-04", 30);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("[SocketServer] Inventory Server started on port " + port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SocketServer] New client connected: " + clientSocket.getRemoteSocketAddress());
                clientThreadPool.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (!running) {
                    System.out.println("[SocketServer] Server stopped listening.");
                    break;
                }
                System.err.println("[SocketServer] Accept error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[SocketServer] Error closing socket: " + e.getMessage());
        }
        clientThreadPool.shutdown();
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                // Low-level byte streams wrapped in character readers/writers with explicit UTF-8
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
            ) {
                writer.println("200 CONNECTED ShopSphere Inventory Socket Protocol v1.0");

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    System.out.println("[SocketServer] Received command: " + line);
                    String[] tokens = line.split("\\s+");
                    String command = tokens[0].toUpperCase();

                    if ("QUIT".equals(command) || "EXIT".equals(command)) {
                        writer.println("200 BYE Connection terminating.");
                        break;
                    }

                    switch (command) {
                        case "PING":
                            writer.println("200 PONG");
                            break;

                        case "GET_STOCK":
                            if (tokens.length < 2) {
                                writer.println("400 ERROR Missing SKU argument. Usage: GET_STOCK <sku>");
                            } else {
                                String sku = tokens[1].toUpperCase();
                                Integer stock = inventoryStore.get(sku);
                                if (stock != null) {
                                    writer.println("200 OK " + sku + " " + stock);
                                } else {
                                    writer.println("404 NOT_FOUND SKU '" + sku + "' does not exist.");
                                }
                            }
                            break;

                        case "UPDATE_STOCK":
                            if (tokens.length < 3) {
                                writer.println("400 ERROR Missing arguments. Usage: UPDATE_STOCK <sku> <delta>");
                            } else {
                                String sku = tokens[1].toUpperCase();
                                try {
                                    int delta = Integer.parseInt(tokens[2]);
                                    Integer current = inventoryStore.get(sku);
                                    if (current != null) {
                                        int updated = Math.max(0, current + delta);
                                        inventoryStore.put(sku, updated);
                                        writer.println("200 OK " + sku + " " + updated);
                                    } else {
                                        writer.println("404 NOT_FOUND SKU '" + sku + "' not found.");
                                    }
                                } catch (NumberFormatException e) {
                                    writer.println("400 ERROR Invalid delta quantity.");
                                }
                            }
                            break;

                        case "LIST_ALL":
                            StringBuilder sb = new StringBuilder("200 ITEMS ");
                            inventoryStore.forEach((k, v) -> sb.append(k).append(":").append(v).append(","));
                            if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
                            writer.println(sb.toString());
                            break;

                        default:
                            writer.println("500 UNKNOWN_COMMAND Unrecognized command: " + command);
                            break;
                    }
                }
            } catch (IOException e) {
                System.err.println("[SocketServer] Client handler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("[SocketServer] Client socket closed.");
                } catch (IOException e) {
                    // Suppress
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        SocketInventoryServer server = new SocketInventoryServer(port);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("[SocketServer] Fatal startup error: " + e.getMessage());
        }
    }
}


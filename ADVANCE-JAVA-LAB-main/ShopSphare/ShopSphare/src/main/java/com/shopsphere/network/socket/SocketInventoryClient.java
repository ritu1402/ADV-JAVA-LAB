package com.shopsphere.network.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Academic Demonstration: Low-Level TCP Socket Client using java.net.Socket.
 * Demonstrates connecting to SocketInventoryServer, transmitting byte streams,
 * and parsing server protocol responses.
 */
public class SocketInventoryClient {

    private final String host;
    private final int port;

    public SocketInventoryClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void runDemo() {
        System.out.println("[SocketClient] Connecting to " + host + ":" + port + "...");

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            // Read server welcome banner
            String serverBanner = in.readLine();
            System.out.println("[Server Banner] -> " + serverBanner);

            // Command sequence for academic demonstration
            String[] testCommands = {
                "PING",
                "GET_STOCK SKU-LAPTOP-01",
                "GET_STOCK SKU-NONEXISTENT",
                "UPDATE_STOCK SKU-LAPTOP-01 -3",
                "GET_STOCK SKU-LAPTOP-01",
                "LIST_ALL",
                "QUIT"
            };

            for (String cmd : testCommands) {
                System.out.println("\n[Client -> Server]: " + cmd);
                out.println(cmd);

                String response = in.readLine();
                System.out.println("[Server -> Client]: " + response);
            }

            System.out.println("\n[SocketClient] Demo completed successfully.");
        } catch (IOException e) {
            System.err.println("[SocketClient] Network error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : SocketInventoryServer.DEFAULT_PORT;

        SocketInventoryClient client = new SocketInventoryClient(host, port);
        client.runDemo();
    }
}


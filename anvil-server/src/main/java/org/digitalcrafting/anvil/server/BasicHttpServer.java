package org.digitalcrafting.anvil.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BasicHttpServer {
    private ServerSocket serverSocket;
    private final int port;
    private final Map<String, Map<String, HttpPathHandler>> handlersMap = new HashMap<>();

    public BasicHttpServer(int port) {
        this.port = port;
    }

    public BasicHttpServer addHandler(String method, String path, HttpPathHandler handler) {
        handlersMap.putIfAbsent(method, new HashMap<>());
        handlersMap.get(method).put(path, handler);
        return this;
    }

    public void start() {
        System.out.println("Web server started");

        try {
            startup();
            Socket clientSocket;
            while ((clientSocket = serverSocket.accept()) != null) {
                HttpSocketHandler socketHandler = new HttpSocketHandler(clientSocket, handlersMap);
                Thread t = new Thread(socketHandler);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
        } finally {
            System.out.println("Server shutdown...");
            shutdown();
        }
    }

    private void startup() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started");
    }

    private void shutdown() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Couldn't close the server socket.");
            System.out.println("Exiting program.");
        }
    }
}

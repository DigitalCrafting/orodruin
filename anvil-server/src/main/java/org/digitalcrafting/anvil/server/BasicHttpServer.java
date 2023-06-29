package org.digitalcrafting.anvil.server;

import org.digitalcrafting.anvil.common.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BasicHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicHttpServer.class);
    private final int port;
    private final Map<String, HttpPathHandler> handlersMap = new HashMap<>();
    private ServerSocket serverSocket;

    public BasicHttpServer() {
        this.port = Integer.parseInt(PropertiesReader.get("server.port"));
    }

    public BasicHttpServer(int port) {
        this.port = port;
    }

    public BasicHttpServer addHandler(String path, HttpPathHandler handler) {
        handlersMap.put(path, handler);
        return this;
    }

    public void start() {
        try {
            startup();
            Socket clientSocket;
            while ((clientSocket = serverSocket.accept()) != null) {
                HttpSocketHandler socketHandler = new HttpSocketHandler(clientSocket, handlersMap);
                Thread t = new Thread(socketHandler);
                t.start();
            }
        } catch (IOException e) {
            LOGGER.error("Error occurred: " + e);
        } finally {
            LOGGER.info("Server shutdown...");
            shutdown();
        }
    }

    private void startup() throws IOException {
        serverSocket = new ServerSocket(port);
        LOGGER.info("Server has started on port {}", port);
    }

    private void shutdown() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't close the server socket.");
            LOGGER.error("Exiting program.");
        }
    }
}

package org.digitalcrafting.anvil.client;

import java.net.ServerSocket;

public class BasicHttpClient {
    private ServerSocket serverSocket;
    private final int port;

    public BasicHttpClient(int port) {
        this.port = port;
    }

    public void start() {
        // TODO
        // probably just have 'exchange' method ? Check out RestTemplate
    }
}

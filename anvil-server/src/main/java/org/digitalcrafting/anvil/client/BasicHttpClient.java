package org.digitalcrafting.anvil.client;

import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicHttpClient.class);

    private final String address;
    private final int port;

    public BasicHttpClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public HttpResponse exchange(HttpRequest request) {
        HttpResponse response = null;

        try (Socket clientSocket = new Socket(address, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream();
        ) {

            request.send(out);

            response = new HttpResponse(in);
            if (!response.parse()) {
                throw new RuntimeException("Could not parse the response");
            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong " + e);
        } finally {
            LOGGER.info("Goodbye!");
        }

        return response;
    }
}

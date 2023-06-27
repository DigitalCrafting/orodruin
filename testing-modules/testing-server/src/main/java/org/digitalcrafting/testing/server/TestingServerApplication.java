package org.digitalcrafting.testing.server;

import org.digitalcrafting.anvil.server.BasicHttpServer;

import java.io.IOException;

public class TestingServerApplication {
    public static void main(String[] args) {
        new BasicHttpServer(8080)
                .addHandler("GET", "/api/", (request, response) -> response.body = "Called GET method on path '/api/'")
                .addHandler("POST", "/api/", (request, response) -> {
                    try {
                        response.body = "Called POST method on path '/api/' with body " + request.getBodyAsString();
                    } catch (IOException e) {
                        response.statusCode = 400;
                        response.message = "Bad Request";
                    }
                }).start();
    }
}

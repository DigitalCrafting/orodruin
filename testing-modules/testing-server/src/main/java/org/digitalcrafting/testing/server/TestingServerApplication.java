package org.digitalcrafting.testing.server;

import org.digitalcrafting.anvil.server.BasicHttpServer;

public class TestingServerApplication {
    public static void main(String[] args) {
        new BasicHttpServer()
                .addHandler("/api/", new TestHandler())
                .start();
    }
}

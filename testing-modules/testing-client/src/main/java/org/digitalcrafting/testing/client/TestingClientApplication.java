package org.digitalcrafting.testing.client;

import org.digitalcrafting.anvil.client.BasicHttpClient;
import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestingClientApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestingClientApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Welcome to HTTP Client");

        try (
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            BasicHttpClient client = new BasicHttpClient("127.0.0.1", 8080);
            String input = "";

            LOGGER.info("Web client started");
            LOGGER.info("Connected to server at 127.0.0.1:8080");
            LOGGER.info("What message do you want to send?");

            while (true) {
                System.out.print("> ");
                input = bufferedReader.readLine();
                HttpRequest request = new HttpRequest("POST", "/api/");
                request.body = input;

                HttpResponse response = client.exchange(request);

                LOGGER.info("Response from server: " + response.body);

                if ("close".equalsIgnoreCase(input)) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Buffered reader failed " + e);
        } finally {
            LOGGER.info("Goodbye!");
        }
    }
}
